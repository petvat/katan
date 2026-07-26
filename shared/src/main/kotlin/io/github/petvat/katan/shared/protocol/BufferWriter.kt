package io.github.petvat.katan.shared.protocol

import java.io.IOException
import java.nio.ByteBuffer

object BufferUtils {

    /**
     * Creates a buffer of [data] with length framing.
     *
     * [4 bytes: payload length as a big-endian Int] [exactly that many bytes: UTF-8 JSON text]
     */
    fun writeBuffer(data: String): ByteBuffer {
        val payload = data.toByteArray()
        val buffer = ByteBuffer.allocate(4 + payload.size)
        buffer.putInt(payload.size)
        buffer.put(payload)
        buffer.flip()
        return buffer
    }

    /**
     * Retrieves length-prefixed messages from [buffer].
     * Returns the message if the full message is present, or null otherwise.
     */
    fun readBuffer(buffer: ByteBuffer): List<String> {
        buffer.flip() // Read mode
        val messages = mutableListOf<String>()

        while (true) {
            buffer.mark() // mark start of potential message
            if (buffer.remaining() < 4) {
                buffer.reset()
                break // Full message not available
            }
            val length = buffer.int
            if (length < 0) throw IOException("Invalid frame length: $length")

            if (buffer.remaining() < length) {
                buffer.reset()
                // growIfMessageWontFit(length)
                break
            }

            val payload = ByteArray(length)
            buffer.get(payload)
            messages.add(String(payload, Charsets.UTF_8))
        }

        buffer.compact() // // preserve partials
        return messages
    }

//    /** Grows the buffer if a fully-framed message wouldn't fit even when empty. */
//    fun growIfMessageWontFit(buffer: ByteBuffer, neededPayloadLength: Int): ByteBuffer {
//        val neededTotal = 4 + neededPayloadLength
//        if (neededTotal <= buffer.capacity()) return buffer // just needs more reads, not more room
//
//        val grown = ByteBuffer.allocate(neededTotal)
//        buffer.flip()
//        grown.put(buffer)
//        return grown
//    }
//
//    fun ensureCapacityForNextRead(buffer: ByteBuffer) {
//        if (buffer.remaining() == 0 && buffer.position() == buffer.capacity()) {
//            // Buffer's full of unconsumed data and nothing was drained --
//            // growIfMessageWontFit should have already caught this on the
//            // previous call; this is a defensive fallback.
//            growIfMessageWontFit(buffer, buffer.capacity() + 1)
//        }
//    }
}
