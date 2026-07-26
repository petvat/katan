package io.github.petvat.katan.server.nio

import io.github.petvat.katan.shared.protocol.BufferUtils
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * Per-connection outbound queue for a non-blocking SocketChannel.
 * Call enqueue() to schedule a message; call flush() when the selector
 * reports OP_WRITE ready (or immediately after enqueue() as an optimistic
 * first attempt). Guarantees messages are written whole, in order, never
 * interleaved -- a partially-written message is never abandoned mid-frame
 * for a later one to jump ahead of.
 */
class MessageWriter(private val channel: SocketChannel) {
    private val pending = ArrayDeque<ByteBuffer>()

    @Synchronized
    fun enqueue(message: String) {
        pending.addLast(BufferUtils.writeBuffer(message))
    }

    /**
     * Attempts to drain the queue.
     *
     * @return true if everything pending was
     * fully written (caller can clear OP_WRITE interest), false if a
     * buffer is still partially written (caller should keep OP_WRITE set
     * and call flush() again once selector signals writable).
     */
    @Synchronized
    fun flush(): Boolean {
        while (pending.isNotEmpty()) {
            val buf = pending.first()
            channel.write(buf)
            if (buf.hasRemaining()) {
                return false // socket buffer's full for now; this message stays at the front of the queue
            }
            pending.removeFirst() // fully written, move to the next queued message
        }
        return true
    }

    fun hasPending(): Boolean = pending.isNotEmpty()
}


/**
 * TODO: Implement
 *
 * This class is responsible for synchronizing write pushes to a client.
 * To avoid ambuiguity on the client side, messages to a client should not be written in parallel.
 * Upon a new write request, if there is already a message that is being written,
 * the request should be put on the message queue.
 *
 * @param messageQueue
 *
 */
//class MessageWriter {
//
//}
