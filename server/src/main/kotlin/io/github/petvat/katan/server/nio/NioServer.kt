package io.github.petvat.katan.server.nio

import io.github.petvat.katan.shared.model.SessionId
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.server.service.client.Auth
import io.github.petvat.katan.server.service.client.ClientId
import io.github.petvat.katan.server.service.client.ClientRegistry
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.service.RequestProcessor
import io.github.petvat.katan.server.service.session.Session
import io.github.petvat.katan.server.service.session.SessionRegistry
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
 * TODO: Make abstract with abstract func respond()
 *
 * @property start Starts up the server on a single thread
 * @property acceptConnection Accepts a new client connection
 * @property handleResponse Sends a response back to client
 * @property acceptClientRequest Processes a client request.
 *
 */
class NioServer(
    private val requestProcessor: RequestProcessor,
    private val sessionRegistry: SessionRegistry,
    private val clientRegistry: ClientRegistry
) {
    private val logger = KotlinLogging.logger { }

    private val requestChannel = Channel<Pair<SessionId, String>>()

    private val responseChannel = Channel<Pair<SocketChannel, String>>()

    private val serverChannel = ServerSocketChannel.open()

    private val selector = Selector.open()

    private val serverScope = CoroutineScope(Dispatchers.Default)

    private val clientBuffers = ConcurrentHashMap<SocketChannel, Pair<ByteBuffer, StringBuilder>>()


    // TODO: Move
    private fun generateSessionId() = SessionId(UUID.randomUUID().toString())

    private suspend fun processSelectedKeys() {
        selector.selectedKeys().forEach {
            when {
                it.isAcceptable && it.channel() is ServerSocketChannel -> {
                    acceptConnection()
                }

                it.isReadable && it.channel() is SocketChannel -> acceptClientRequest(it)
            }
        }
        selector.selectedKeys().clear()
    }

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
                        requestProcessor.handle(
                            clientRegistry[sessionRegistry[client].belongsTo],
                            req,
                            ::responseCallback
                        )
                    }
                }
            }

            repeat(responsePool) {
                serverScope.launch(Dispatchers.IO) {
                    for ((client, res) in responseChannel) {
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

    private suspend fun responseCallback(responses: Map<Session, String>) {
        responses.forEach { (ch, res) ->
            responseChannel.send(ch.socketChannel to res)
        }
    }

    private fun handleResponse(client: SocketChannel, response: String) {
        val buffer = ByteBuffer.wrap(response.toByteArray())
        while (buffer.hasRemaining()) {
            client.write(buffer)
        }
        // TODO: Now blocking but could use channel's MessageWriter
    }

    /**
     * Connect a new client to server.
     *
     * TODO: Why withConetxt?
     */
    private suspend fun acceptConnection() = withContext(Dispatchers.IO) {
        val clientSocket = serverChannel.accept()
        logger.info { "CONNECTED: ${clientSocket.socket().inetAddress.hostAddress} : ${clientSocket.socket().port}" }
        clientSocket.configureBlocking(false)

        val key = clientSocket.register(selector, SelectionKey.OP_READ)

        // Attach ClientSession to track state
        val clientId = ClientId(UUID.randomUUID().toString())
        val session = Session(clientId, SessionId(UUID.randomUUID().toString()), clientSocket)
        val client = ConnectedClient(clientId, Auth.Unauth, mutableListOf(session))

        sessionRegistry.add(session)
        clientRegistry.add(client)

        key.attach(session)

        // Add dedicated client buffer
        // TODO: Add to MessageWriter
        clientBuffers[clientSocket] = ByteBuffer.allocate(4096) to StringBuilder()

        logger.debug { "New client is given session ID: ${session.id}." }

        val token = "token:${UUID.randomUUID()}"
        responseChannel.send(clientSocket to token)
    }

    /**
     * Read from a channel into a buffer.
     */
    private fun channelRead(socketChannel: SocketChannel): String? {
        val (buf, str) = clientBuffers[socketChannel]!!
        val delimiter = '\n'

        logger.debug { "Begin read." }

        // MessageReader.read() // MessageReader(blocking = true)

        try {
            val bytesRead = socketChannel.read(buf) // NOTE: Could overflow the buffer.
            buf.flip() // read mode

            logger.debug { "PARTIAL: $str" }

            // If the string builder is not empty, that indicates that there is a
            // partial message in the buffer
            if (bytesRead == -1) {
                logger.error { "Read failed." }
                disconnectClient(socketChannel)
                return null
            }
            while (buf.hasRemaining()) {
                val c = buf.get().toInt().toChar()
                if (c == delimiter) {
                    if (buf.hasRemaining()) {
                        // There is a partial message in the buffer.
                        buf.compact()
                    } else {
                        buf.clear()
                    }
                    val msg = str.toString()
                    // We can clear the str because it now contains a complete message.
                    str.clear()
                    logger.debug { "Complete msg return: $msg" }
                    return msg
                } else {
                    str.append(c)
                }
            }
            // If we never reach a delimiter, we had a partial read.
            // Buffer should be empty.
            buf.flip() // write mode
            return null
        } catch (e: Exception) {
            logger.debug { "DISCONNECTING TYP 2, ABRUPT: ${e.message}" }
            disconnectClient(socketChannel)
            return null
        }
    }

    private fun disconnectClient(socketChannel: SocketChannel) {

        val key = socketChannel.keyFor(selector)
        val sid = key.attachment() as SessionId

        clientBuffers.remove(socketChannel)
        sessionRegistry.remove(sid)

        // Cancel the selection key and close the channel
        key.cancel()
        socketChannel.close()
        logger.info { "DISCONNECTED: ${socketChannel.remoteAddress}." }
    }

    /**
     * Handle new client request.
     */
    private suspend fun acceptClientRequest(key: SelectionKey) = withContext(Dispatchers.IO) {
        val clientChannel = key.channel() as SocketChannel
        val session = key.attachment() as Session

        logger.debug { "Aquire client lock attempt." }

        val req = channelRead(clientChannel)

        if (req != null) {
            logger.debug { "COMPLETE MSG: $req" }
            requestChannel.send(session.id to req) // NOTE: Or client channel here if stateful
        }

        // TODO: Shouldn't block. Use MessageWriter and remove lock. Implement hierarchical locking of resources in LockManager.
//        lockManager.callInMutex(sid) {
//
//        }
    }
}
