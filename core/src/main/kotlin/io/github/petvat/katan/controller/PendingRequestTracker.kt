package io.github.petvat.katan.controller

import io.github.petvat.katan.shared.protocol.OutMessage
import io.github.petvat.katan.shared.protocol.Response
import java.util.concurrent.ConcurrentHashMap


//// TODO: ADD THIS.
//class PendingRequestTracker {
//    private val pending = ConcurrentHashMap<Int, (Response) -> Unit>()
//
//    /**
//     * Registers a callback to run when a reply with this seq arrives.
//     */
//    fun await(seq: Int, onReply: (Response) -> Unit) {
//        pending[seq] = onReply
//    }
//
//    /** Called by ResponseProcessor for every incoming envelope. Returns true if it was claimed. */
//    fun tryResolve(message: OutMessage): Boolean {
//        val replyTo = message.replyTo ?: return false
//        val callback = pending.remove(replyTo) ?: return false
//        callback(message.payload)
//        return true
//    }
//
//    /** Call on disconnect/timeout so stale callbacks don't linger forever. */
//    fun cancel(seq: Int) {
//        pending.remove(seq)
//    }
//}
