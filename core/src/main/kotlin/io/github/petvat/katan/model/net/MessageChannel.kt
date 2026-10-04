package io.github.petvat.katan.model.net

import io.github.petvat.katan.shared.BlockingClient
import io.github.petvat.katan.shared.protocol.InMessage
import io.github.petvat.katan.shared.protocol.OutMessage
import json.KatanJson
import java.util.concurrent.BlockingQueue

interface MessageChannel {
    fun connect(host: String, port: Int): Boolean
    fun connected(): Boolean
    fun send(message: InMessage)


    fun incoming(): BlockingQueue<OutMessage>
    fun shutdown()
}


class KatanChannel : BlockingClient<InMessage, OutMessage>(), MessageChannel {
    override fun processRequest(request: InMessage): String = KatanJson.toJson(request)
    override fun processResponse(response: String): OutMessage = KatanJson.toOutMessage(response)

    override fun connect(host: String, port: Int): Boolean = start(host, port)
    override fun connected(): Boolean = isConnected()

    @Synchronized
    override fun send(message: InMessage) = forwardRequest(message)

    override fun incoming(): BlockingQueue<OutMessage> = messageQueue
    override fun shutdown() = close()
}
