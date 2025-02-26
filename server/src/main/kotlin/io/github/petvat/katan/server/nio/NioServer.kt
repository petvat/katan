package io.github.petvat.katan.server.nio

import io.github.petvat.katan.shared.protocol.SessionId
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.server.api.KatanApi
import io.github.petvat.katan.server.client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.*
import java.util.concurrent.ConcurrentHashMap

// TODO: MOVE
/**
 * Avoid race conditions by aquiring its lock from this point.
 */
val clientLockMap = ConcurrentHashMap<SessionId, Mutex>()

suspend fun <R> wrapWithMutex(mutex: Mutex, function: suspend () -> R): suspend () -> R {
    return {
        mutex.withLock {
            function()
        }
    }
}

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
    private val requestChannel: Channel<Pair<SessionId, String>>,
    private val responseChannel: Channel<Pair<SocketChannel, String>>,
    private val testMode: Boolean = false
) {
    private val logger = KotlinLogging.logger { }

    private val serverChannel = ServerSocketChannel.open()

    private val selector = Selector.open()

    private val clients = ConcurrentHashMap<SessionId, SocketChannel>()

    private val serverScope = CoroutineScope(Dispatchers.Default)

    private val clientBuffers = ConcurrentHashMap<SocketChannel, Pair<ByteBuffer, StringBuilder>>()

    private fun generateSessionId(): SessionId {
        return SessionId(UUID.randomUUID().toString());
    }

    /**
     * Main server entry.
     *
     * @throws IOException Close connections.
     */
    fun start(portNumber: Int) {
        try {
            serverChannel.configureBlocking(false)
            serverChannel.bind(InetSocketAddress(portNumber))
            serverChannel.register(selector, SelectionKey.OP_ACCEPT)

            // This coroutine acts as the producer. Listens to client requests and enqueues them if request must be handled.
            serverScope.launch(Dispatchers.IO) {
                while (true) {
                    if (selector.select() == 0) {
                        continue
                    }
                    selector.selectedKeys().forEach { key ->
                        if (key.isAcceptable) {
                            when (val channel = key.channel()) {
                                is ServerSocketChannel -> acceptConnection(channel)
                                else -> throw RuntimeException("Unknown channel.")
                            }
                        } else if (key.isReadable) {
                            logger.debug { "Readable request" }
                            when (val channel = key.channel()) {
                                is SocketChannel -> acceptClientRequest(key)
                                else -> throw RuntimeException("Unknown channel.")
                            }
                        }
                    }
                    selector.selectedKeys().clear()
                }
            }

            // This coroutine acts as consumer. Dequeues from request queue, generates response and enqueues it onto response queue.
            val requestProcessorsPoolSize = 3
            repeat(requestProcessorsPoolSize) {
                serverScope.launch {
                    for ((client, req) in requestChannel) {
                        if (testMode) { // HACK
                            // echo
                            responseCallback(mapOf(client to req))
                        } else {
                            KatanApi.handleRequest(req, client, ::responseCallback)
                        }
                    }
                }
            }

            val responsePoolSize = 3
            repeat(responsePoolSize) {
                serverScope.launch(Dispatchers.IO) {
                    for ((client, res) in responseChannel) {
                        handleResponse(client, res)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error { "${e.message}.\nClosing connections." }
            serverScope.cancel()
            serverChannel.close()
        }
    }

    private suspend fun responseCallback(responses: Map<SessionId, String>) {
        //val json = responses.mapValues { KatanJson.messageToJson(it.value) }
        responses.forEach { (ch, res) ->
            responseChannel.send(clients[ch]!! to res)
        }
    }

    private fun handleResponse(client: SocketChannel, response: String) {
        val buffer = ByteBuffer.wrap(response.toByteArray())
        var writes = 0
        while (buffer.hasRemaining()) {
            client.write(buffer)
            writes++
        }
        // TODO: Now blocking but could use channel's MessageWriter

        logger.debug { "Writes: $writes" }

    }

    /**
     * Connect a new client to server.
     */
    private fun acceptConnection(serverChannel: ServerSocketChannel) {
        val client = serverChannel.accept()
        val socket = client.socket()
        logger.info { "CONNECTED: ${socket.inetAddress.hostAddress} : ${socket.port}" }
        client.configureBlocking(false)
        val key = client.register(selector, SelectionKey.OP_READ)
        // val clientConn = ClientConnection(client)

        // Add dedicated client buffer
        clientBuffers[client] = ByteBuffer.allocate(4096) to StringBuilder()

        // Attach ClientSession to track state
        val sid = generateSessionId()
        key.attach(sid)
        ClientRepository.addClient(
            ConnectedClient(
                sessionId = sid,
                activity = Idle,
                auth = UnAuth
            )
        )
        // ClientService.addClient(GuestState(sessionId = sid))
        clients[sid] = client

        // HACK:
        clientLockMap[sid] = Mutex()
        logger.debug { "New client is given session ID: $sid." }

        // Response with SID
        // TODO: Mutex!
        // logger.debug { "Send back ACK." }

        // TODO: Send as sessionID only! Decouple Katan-specific logic

//        handleResponse(
//            client, "sid: ${sid.value}"

//                MessageFactory.create(
//                    messageType = MessageType.ACK,
//                    data = Response.ConnectAck(sid.value),
//                    description = "Client was assigned a session ID.",
//                    success = true
//                )

        //)


    }


    // clients += clientConn


    /**
     * Read from a channel into a buffer.
     */
    private fun channelRead(socketChannel: SocketChannel): String? {
        val (buf, str) = clientBuffers[socketChannel]!!
        val delimiter = '\n'

        logger.debug { "Begin read." }

        try {
            val bytesRead = socketChannel.read(buf) // NOTE: Could overflow the buffer.
            buf.flip() // read mode

            logger.debug { "PARTIAL: $str" }

            // If the string builder is not empty, that indicates that there is a
            // partial message in the buffer
            if (bytesRead == -1) {
                logger.error { "Read failed." }
                val socket = socketChannel.socket()
                logger.info { "DISCONNETED: ${socket.inetAddress.hostAddress} : ${socket.port}" }
                clientBuffers -= socketChannel
                socketChannel.close()
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
        logger.info { "DISCONNECTED: ${socketChannel.remoteAddress}." }

        val key = socketChannel.keyFor(selector)
        val sid = key.attachment() as SessionId

        clientBuffers -= socketChannel
        clients -= sid
        clientLockMap -= sid

        // Cancel the selection key and close the channel
        key.cancel()
        socketChannel.close()
        logger.debug { "Disconnect clean-up successful." }
    }


    /**
     * Handle new client request.
     */
    private suspend fun acceptClientRequest(key: SelectionKey) = withContext(Dispatchers.IO) {
        val clientChannel = key.channel() as SocketChannel

        val sid = key.attachment() as SessionId
        logger.debug { "Aquire client lock attempt." }
        val mutex = clientLockMap[sid]!!
        wrapWithMutex(mutex) {
            val req = channelRead(clientChannel)

            if (req != null) {
                logger.info { "COMPLETE MSG: $req" }
                requestChannel.send(sid to req) // NOTE: Or client channel here if stateful
            }
        }.invoke()
    }
}
