package io.github.petvat.katan.controller

import io.github.petvat.katan.event.ConnectionEvent
import io.github.petvat.katan.event.ErrorEvent
import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.networking.NioKatanClient
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.InMessage
import io.github.petvat.katan.shared.protocol.OutMessage
import io.github.petvat.katan.shared.protocol.Request
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger


class RequestSender {
    private val seq = AtomicInteger(0)
    private var client: NioKatanClient? = null   // nullable, not lateinit — see below

    fun connect(host: String, port: Int): Boolean {
        val newClient = NioKatanClient()
        val ok = newClient.start(address = host, portNumber = port)
        client = if (ok) newClient else null
        return ok
    }

    val isConnected: Boolean
        get() = client?.isConnected() ?: false

    fun send(channel: String?, payload: Request): Int? {
        val current = client ?: return null
        val mySeq = seq.incrementAndGet()
        current.forwardRequest(InMessage(seq = mySeq, channel = channel, payload = payload))
        return mySeq
    }

    val responses: LinkedBlockingQueue<OutMessage>?
        get() = client?.messageQueue

    fun close() {
        client?.close()
        client = null
    }
}
