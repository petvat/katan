package io.github.petvat.katan.server.client

import io.github.petvat.katan.shared.model.SessionId
import java.util.concurrent.ConcurrentHashMap

object ClientRepository {
    private val _clients = ConcurrentHashMap<SessionId, ConnectedClient>()

    /**
     * Read-only.
     */
    val clients get() = _clients.toMap()

    fun getClient(sessionId: SessionId): ConnectedClient? {
        return _clients[sessionId]
    }

    fun addClient(client: ConnectedClient) {
        _clients += client.sessionId to client
    }

    fun removeClient(sessionId: SessionId) {
        _clients -= sessionId
    }

    fun updateClient(sessionId: SessionId, new: ConnectedClient) {
        _clients += sessionId to new
    }
}
