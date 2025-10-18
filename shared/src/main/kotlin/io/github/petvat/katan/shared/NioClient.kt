package io.github.petvat.katan.shared

import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.Selector
import java.nio.channels.SocketChannel
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingQueue


/**
 * This class represents the client side endpoint of the communication socket.
 *
 * @param T The request this client will process
 * @param S The response this client will process the request into.
 */
abstract class NioClient<T, S> {
    private val logger = KotlinLogging.logger { }
    private lateinit var serverChannel: SocketChannel
    private lateinit var selector: Selector
    val messageQueue = LinkedBlockingQueue<S>()

    /**
     *
     */
    fun start(address: String = "localhost", portNumber: Int): Boolean {
        try {
            serverChannel = SocketChannel.open()
            //serverChannel.configureBlocking(true) // Hope
            serverChannel.connect(InetSocketAddress(address, portNumber))
            logger.info { "Connection established." }
            // buffer = ByteBuffer.allocate(1024)

            //selector = Selector.open()

            //serverChannel.register(selector, SelectionKey.OP_CONNECT or SelectionKey.OP_READ)

            // Await ACK response
//            val readBuffer = ByteBuffer.allocate(1024)
//            val bytesRead = serverChannel.read(readBuffer)
//            if (bytesRead > 0) {
//                readBuffer.flip()
//                logger.info { "Message from server." }
//                logger.debug { (String(readBuffer.array(), 0, bytesRead)) }
//            } else {
//                logger.error { "Bad read." }
//            }

            listenForResponses()
        } catch (io: IOException) {
            logger.error { "${io.message}" }
            return false
        }
        return true
    }


    private fun listenForResponses() {
        // Should work because SocketChannel is thread-safe for 1 writer and 1 reader.
        val runner = Thread {
            //selector.select()

            while (true) {
                val readBuffer = ByteBuffer.allocate(4096)
                if (!serverChannel.isConnected) {
                    logger.debug { "Connection closed. Stopping listener deamon" }
                    return@Thread
                }
                try {
                    val bytesRead = serverChannel.read(readBuffer)
                    if (bytesRead > 0) {
                        readBuffer.flip()
                        logger.info { "Message from server." }
                        enqueue(processResponse(String(readBuffer.array(), 0, bytesRead)))
                    }
                    readBuffer.clear()
                } catch (e: Exception) {
                    logger.debug { "DISCONNECT: Lost connection with server: ${e.message}" }
                    serverChannel.close()
                }
            }
//            var selectionKey = selector.selectedKeys().iterator()
//
//            while (selectionKey.hasNext()) {
//                val key = selectionKey.next()
//                selectionKey.remove()
//
//                if (key.isConnectable) {
//                    val channel = key.channel() as SocketChannel
//                    if (channel.isConnectionPending) {
//                        channel.finishConnect()
//                        println("Connected to the server!")
//                    }
//                }
//                if (key.isReadable) {
//                    val channel = key.channel() as SocketChannel
//                    val readBuffer = ByteBuffer.allocate(1024)
//                    val bytesRead = channel.read(readBuffer)
//                    if (bytesRead > 0) {
//                        readBuffer.flip()
//                        logger.info { "Message from server." }
//                        processResponse(String(readBuffer.array(), 0, bytesRead))
//                    }
//                    readBuffer.clear()
//                }
//            }
        }
        runner.isDaemon = true // Kill on main exit.
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
        var processed = processRequest(request)
        processed += System.lineSeparator() // marks end of request
        val writeBuffer = ByteBuffer.allocate(1024)
        writeBuffer.clear().put(processed.toByteArray()).flip()

        // Ensure client writes whole message
        while (writeBuffer.hasRemaining()) {
            serverChannel.write(writeBuffer)
        }
    }
}
