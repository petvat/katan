package io.github.petvat.katan.server.client

import io.github.petvat.katan.shared.model.SessionId
import java.util.concurrent.ConcurrentHashMap

//class ClientManager {
//    private val _clients = ConcurrentHashMap<SessionId, ConnectedClient>()
//
//    // TODO: active members
//    // TODO: presistent members
//
//    /**
//     * Read-only.
//     */
//    val clients get() = _clients.toMap()
//
//    fun getClient(sessionId: SessionId): ConnectedClient? {
//        return _clients[sessionId]
//    }
//
//    fun addClient(client: ConnectedClient) {
//        _clients += client.sessionId to client
//    }
//
//    fun addClient(sid: SessionId) {
//        _clients += sid to ConnectedClient(sid, UnAuth, Idle)
//    }
//
//    fun removeClient(sessionId: SessionId) {
//        _clients -= sessionId
//    }
//
//    fun updateClient(sessionId: SessionId, new: ConnectedClient) {
//        _clients += sessionId to new
//    }
//}
