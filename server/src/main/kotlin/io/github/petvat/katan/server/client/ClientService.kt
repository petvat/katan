package io.github.petvat.katan.server.client

import io.github.petvat.katan.shared.protocol.SessionId
import java.util.concurrent.ConcurrentHashMap

object ClientService {

    private val _clients = ConcurrentHashMap<SessionId, ClientState>()

    val clients get() = _clients.toMap()

    fun getClient(sessionId: SessionId): ClientState? {
        return _clients[sessionId]
    }

    fun addClient(client: ClientState) {
        _clients[client.sessionId] = client
    }

    fun updateClient(sessionId: SessionId, new: ClientState) {
        _clients[sessionId] = new
    }

}
