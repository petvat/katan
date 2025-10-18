package io.github.petvat.katan.server.service.client

import io.github.petvat.katan.server.service.presenter.ConcurrentRegistry
import io.github.petvat.katan.shared.UserId
import java.util.concurrent.ConcurrentHashMap

// TODO: This one is weird. Maybe UserId to ConnectedClient. SessionId should be Session, which is ephemeral
//class ClientRegistry {
//    private val clients = ConcurrentHashMap<ClientId, ConnectedClient>()
//
//    operator fun get(id: ClientId): ConnectedClient {
//        return clients[id] ?: error("Session $id does not exist.")
//    }
//
//    fun add(client: ConnectedClient) {
//        clients[client.id] = client
//    }
//
//    fun remove(id: ClientId) {
//        clients.remove(id)
//    }
//}

class ClientRegistry : ConcurrentRegistry<ClientId, ConnectedClient>()
class UserRegistry : ConcurrentRegistry<UserId, Auth>() { // HACK: ?
    private val clients = ConcurrentHashMap<UserId, MutableList<ClientId>>()

    fun getClientsByUser(userId: UserId): List<ClientId> {
        return clients[userId]?.toList() ?: emptyList()
    }

    fun addClientLink(userId: UserId, clients: Collection<ClientId>) {
        this.clients[userId]?.addAll(clients.toSet())
    }
}
