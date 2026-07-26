package io.github.petvat.katan.networking

import io.github.petvat.katan.controller.RequestSender
import io.github.petvat.katan.controller.ResponseProcessor


interface INetworkSession {
    fun connect(host: String?, port: Int?): Boolean
    fun poll()
    fun close()
}

/**
 * TODO: Handle disconnect!
 */
class NetworkSession(
    private val sender: RequestSender,
    private val responseProcessor: ResponseProcessor,
) : INetworkSession {
    override fun connect(host: String?, port: Int?): Boolean = sender.connect(host ?: "localhost", port ?: 1234)

    override fun poll() {
        if (!sender.isConnected) return

        sender.responses?.poll()?.let {
            responseProcessor.update(
                it
            )
        }
    }

    override fun close() = sender.close()

}
