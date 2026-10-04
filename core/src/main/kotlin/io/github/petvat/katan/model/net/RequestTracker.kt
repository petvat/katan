package io.github.petvat.katan.model.net

import io.github.petvat.katan.shared.protocol.InMessage
import io.github.petvat.katan.shared.protocol.Request
import io.github.petvat.katan.shared.protocol.Response
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.exp

class RequestTimeoutException(seq: Int) :
    Exception("Request seq=$seq timed out with no reply")

class ConnectionLostException(message: String = "Connection lost") : Exception(message)


/**
 * Single owner of the outgoing [InMessage.seq] space and of pending replies.
 *
 * Every request gets a [CompletableDeferred]. Callers usually fire-and-forget
 * (errors surface globally via [Response.Error] -> ErrorEvent), but anything
 * needing the reply can await the deferred. Deferrds are completed by
 * [InboundRouter] on the pump thread; timeouts are swept by the pump loop;
 * disconnects fail everything at once.
 */
class RequestTracker(
    private val channel: MessageChannel,
    private val timeoutMs: Long = DEFAULT_TIMEOUT_MS
) {
    private val nextSeq = AtomicInteger(0)
    private val pending = ConcurrentHashMap<Int, Entry>()

    private class Entry(val deferred: CompletableDeferred<Response>, val deadline: Long)


    fun send(channelId: String?, request: Request): CompletableDeferred<Response> {
        val seq = nextSeq.getAndIncrement()
        val deferred = CompletableDeferred<Response>()
        if (!channel.connected()) {
            deferred.completeExceptionally(ConnectionLostException("Channel not connected"))
            return deferred
        }
        pending[seq] = Entry(deferred, System.currentTimeMillis() + timeoutMs)
        try {
            channel.send(InMessage(seq = seq, channel = channelId, payload = request))
        } catch (t: Throwable) {
            pending.remove(seq)
            deferred.completeExceptionally(t)
        }
        return deferred
    }

    /**
     * Called by [InboundRouter] when a message with a matching replyTo arrives.
     */
    fun resolve(replyTo: Int, response: Response) =
        pending.remove(replyTo)?.deferred?.complete(response) ?: false


    fun failAll(cause: Throwable) {
        pending.values.forEach { it.deferred.completeExceptionally(cause) }
        pending.clear()
    }

    fun sweepExpired(now: Long = System.currentTimeMillis()): Int {
        var expired = 0
        val it = pending.entries.iterator()
        while (it.hasNext()) {
            val entry = it.next().value
            if (entry.deadline <= now) {
                it.remove()
                entry.deferred.completeExceptionally(RequestTimeoutException(entry.hashCode()))
                expired++
            }
        }
        return expired
    }

    val pendingCount: Int get() = pending.size

    companion object {
        const val DEFAULT_TIMEOUT_MS = 10000L
    }
}
