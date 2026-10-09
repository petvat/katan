package io.github.petvat.katan.server.nio

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.server.service.channel.IdType
import io.github.petvat.katan.server.service.channel.generateClientId
import io.github.petvat.katan.server.service.client.*
import io.github.petvat.katan.server.service.gateway.RequestProcessor
import io.github.petvat.katan.shared.MessageReader
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * This class represents a NIO server.
 *
 * @property start Starts up the server on a single thread
 * @property acceptConnection Accepts a new client connection
 * @property handleResponse Sends a response back to client
 * @property acceptClientRequest Processes a client request.
 *
 */
class NioServer(
    private val requestProcessor: RequestProcessor,
    private val clientRegistry: ClientRegistry
) {
    private val logger = KotlinLogging.logger { }

    private val requestChannel = Channel<Pair<ClientId, String>>()

    private val responseChannel = Channel<Pair<SocketChannel, String>>()

    private val serverChannel = ServerSocketChannel.open()

    private val selector = Selector.open()

    private val serverScope = CoroutineScope(Dispatchers.Default)


    private val writers = ConcurrentHashMap<SocketChannel, MessageWriter>()
    private val readers = ConcurrentHashMap<SocketChannel, MessageReader>()

    // private val clientBuffers = ConcurrentHashMap<SocketChannel, Pair<ByteBuffer, StringBuilder>>()

    private val readBuffers = ConcurrentHashMap<SocketChannel, ByteBuffer>()

    private suspend fun processSelectedKeys() {
        selector.selectedKeys().forEach {
            when {
                it.isAcceptable && it.channel() is ServerSocketChannel -> {
                    acceptConnection()
                }

                it.isReadable && it.channel() is SocketChannel -> acceptClientRequest(it)

                // in processSelectedKeys' when block:
                it.isWritable && it.channel() is SocketChannel -> {
                    val ch = it.channel() as SocketChannel
                    val writer = writers[ch] ?: return
                    if (writer.flush()) {
                        it.interestOpsAnd(SelectionKey.OP_READ) // done draining. Stop listening for writable
                    }
                }
            }
        }
        selector.selectedKeys().clear()
    }

    /**
     * @note [responsePool] > 1 will break with blocking client
     */
    fun start(portNumber: Int, requestPool: Int = 3, responsePool: Int = 1) {
        try {
            serverChannel.configureBlocking(false)
            serverChannel.bind(InetSocketAddress(portNumber))
            serverChannel.register(selector, SelectionKey.OP_ACCEPT)

            serverScope.launch(Dispatchers.IO) {
                while (true) {
                    if (selector.select() > 0) {
                        processSelectedKeys()
                    }
                }
            }

            // This coroutine acts as consumer. Dequeues from request queue, generates response and enqueues it onto response queue.
            repeat(requestPool) {
                serverScope.launch {
                    for ((client, req) in requestChannel) {
                        logger.debug { "Received request [$req] from [$client]." }
                        requestProcessor.handle(
                            clientRegistry.get(client)!!,
                            req,
                            ::responseCallback
                        )
                    }
                }
            }

            repeat(responsePool) {
                serverScope.launch(Dispatchers.IO) {
                    for ((client, res) in responseChannel) {
                        logger.debug { "Sending response [$res] to [$client]." }
                        handleResponse(client, res)
                        // MessageWriter.write()
                    }
                }
            }

        } catch (e: Exception) {
            logger.error { "${e.message}.\nClosing connections." }
            serverScope.cancel()
            serverChannel.close()
        }

    }

    private suspend fun responseCallback(responses: Map<ConnectedClient, String>) {
        responses.forEach { (ch, res) ->
            responseChannel.send(ch.connection!!.socketChannel to res) // HACK: "!!"
        }
    }


    private fun handleResponse(client: SocketChannel, response: String) {
        val writer = writers.getOrPut(client) { MessageWriter(client) }
        writer.enqueue(response)
        if (!writer.flush()) {
            client.keyFor(selector)?.interestOpsOr(SelectionKey.OP_WRITE)
        }
    }

    /**
     * Connect a new client to server.
     * NOT FULLY CONNECTED
     * Phase 1 - Raw connection, no identity.
     *
     */
    private suspend fun acceptConnection() = withContext(Dispatchers.IO) {
        val clientSocket = serverChannel.accept()
        logger.info { "CONNECTED: ${clientSocket.socket().inetAddress.hostAddress} : ${clientSocket.socket().port}" }
        clientSocket.configureBlocking(false)

        val key = clientSocket.register(selector, SelectionKey.OP_READ)

        // Attach ClientSession to track state


        val connection = Connection(ConnectionId(UUID.randomUUID().toString()), clientSocket)
        val connectedClient = ConnectedClient(generateClientId(IdType.CLIENT), connection = connection)

        key.attach(connectedClient.id)

        clientRegistry.register(connectedClient.id, connectedClient)

        logger.info { "PENDING CONNECTION: ${clientSocket.socket().inetAddress.hostAddress}" }
    }

    /**
     * Read from a channel into a buffer.
     */
    private fun channelRead(socketChannel: SocketChannel): List<String>? {
        logger.debug { "Begin read." }
        val reader = readers.getOrPut(socketChannel) { MessageReader() }

        return try {
            val result = reader.readAvailable(socketChannel)
            if (result == null) {
                logger.debug { "DICONNECTED TYP 1, CLEAN: $socketChannel" }
                disconnectClient(socketChannel)
            }
            result
        } catch (e: Exception) {
            logger.debug { "DISCONNECTING TYP 2, ABRUPT: ${e.message}" }
            disconnectClient(socketChannel)
            emptyList()
        }
    }

    private fun disconnectClient(socketChannel: SocketChannel) {
        val key = socketChannel.keyFor(selector)
        val clientId = key.attachment() as ClientId

        // Tear down I/O immediately
        readBuffers.remove(socketChannel)
        key.cancel()
        socketChannel.close()

        clientRegistry.markDisconnected(clientId) { expiredClient ->
            logger.info { "Client ${expiredClient.id} expired after grace window." }
        }
        logger.info { "DISCONNECTED: $clientId (grace period started)." }
    }

    /**
     * Handle new client request.
     */
    private suspend fun acceptClientRequest(key: SelectionKey) = withContext(Dispatchers.IO) {
        val clientChannel = key.channel() as SocketChannel
        val clientId = key.attachment() as ClientId
        val messages = channelRead(clientChannel)
            ?: return@withContext // null -> disconnected
        if (messages.isEmpty()) return@withContext // no data available, but still open
        messages.forEach { msg -> requestChannel.send(clientId to msg) }
    }
}
