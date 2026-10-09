package io.github.petvat.katan.shared

import io.github.petvat.katan.shared.protocol.BufferUtils
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel


/**
 * Reads length-prefixed UTF-8 JSON messages off a SocketChannel,
 * growing its internal buffer as needed for messages larger than the
 * initial allocation. One MessageReader per channel; not thread-safe for
 * concurrent reads.
 */
class MessageReader(initialCapacity: Int = 4096) {
    private var buffer: ByteBuffer = ByteBuffer.allocate(initialCapacity)

    /**
     * Reads from [channel] and returns every complete message now available.
     * Blocks on the underlying channel.read() call exactly once per call --
     * caller is expected to loop calling this repeatedly.
     *
     * @return A list of available messages, or null if EOF or socket closed
     */
    fun readAvailable(channel: SocketChannel): List<String>? {
        ensureCapacityForNextRead()

        val bytesRead = channel.read(buffer)
        if (bytesRead < 0) return null
        if (bytesRead == 0) return emptyList()

        buffer.flip()
        val messages = mutableListOf<String>()

        while (true) {
            buffer.mark()
            if (buffer.remaining() < 4) {
                buffer.reset()
                break
            }
            val length = buffer.int
            if (length < 0) throw IOException("Invalid frame length: $length")

            if (buffer.remaining() < length) {
                buffer.reset()
                growIfMessageWontFit(length)
                break
            }

            val payload = ByteArray(length)
            buffer.get(payload)
            messages.add(String(payload, Charsets.UTF_8))
        }

        buffer.compact()
        return messages
    }

    /** Grows the buffer if a fully-framed message wouldn't fit even when empty. */
    private fun growIfMessageWontFit(neededPayloadLength: Int) {
        val neededTotal = 4 + neededPayloadLength
        if (neededTotal <= buffer.capacity()) return // just needs more reads, not more room

        val grown = ByteBuffer.allocate(neededTotal)
        buffer.flip()
        grown.put(buffer)
        buffer = grown
    }

    private fun ensureCapacityForNextRead() {
        if (buffer.remaining() == 0 && buffer.position() == buffer.capacity()) {
            // Buffer's full of unconsumed data and nothing was drained --
            // growIfMessageWontFit should have already caught this on the
            // previous call. This is a defensive fallback.
            growIfMessageWontFit(buffer.capacity() + 1)
        }
    }
}
