package io.github.petvat.katan.shared

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.shared.protocol.BufferUtils
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.Selector
import java.nio.channels.SocketChannel
import java.util.concurrent.LinkedBlockingQueue


/**
 * Reads message from buffer into byte array if the buffer contains the whole message
 *
 */
fun readBuffer(buffer: ByteBuffer, length: Int): ByteArray? {
    TODO()
}


/**
 * This class represents the client side endpoint of the communication socket.
 *
 * @param T The request this client will process
 * @param S The response this client will process the request into.
 */
abstract class BlockingClient<T, S> {
    private val logger = KotlinLogging.logger { }
    private lateinit var serverChannel: SocketChannel
    private lateinit var selector: Selector
    val messageQueue = LinkedBlockingQueue<S & Any>()
    private val reader = MessageReader()

    fun isConnected(): Boolean = ::serverChannel.isInitialized && serverChannel.isConnected

    /**
     *
     */
    fun start(address: String? = "localhost", portNumber: Int): Boolean {
        try {
            serverChannel = SocketChannel.open()
            serverChannel.connect(InetSocketAddress(address, portNumber))
            serverChannel.configureBlocking(true)
            logger.info { "Connection established." }
            listenForResponses()
        } catch (io: IOException) {
            logger.error { "${io.message}" }
            return false
        }
        return true
    }


    private fun listenForResponses() {
        val runner = Thread {
            while (true) {
                if (!serverChannel.isConnected) {
                    logger.debug { "Connection closed. Stopping listener daemon" }
                    return@Thread
                }
                try {
                    val messages = reader.readAvailable(serverChannel)
                    messages.forEach { json -> enqueue(processResponse(json)) }
                } catch (e: Exception) {
                    logger.debug { "DISCONNECT: Lost connection with server: ${e.message}" }
                    serverChannel.close()
                    return@Thread
                }
            }
        }
        runner.isDaemon = true
        runner.start()
    }

    private fun enqueue(response: S) {
        messageQueue.add(response)
    }

    abstract fun processRequest(request: T): String

    abstract fun processResponse(response: String): S

    fun close() {
        logger.info { "Closing connection." }
        serverChannel.shutdownOutput()
        serverChannel.close()

    }

    /**
     * This function forwards data to the server.
     * Puts the data in a byte buffer that is written onto the server channel.
     */
    fun forwardRequest(request: T) {
        logger.debug { "Begin forwarding." }
        val processed = processRequest(request)

        val buffer = BufferUtils.writeBuffer(processed)

        // Ensure client writes whole message
        while (buffer.hasRemaining()) {
            serverChannel.write(buffer)
        }
    }
}
