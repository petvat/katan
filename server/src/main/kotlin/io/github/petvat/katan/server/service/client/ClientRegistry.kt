package io.github.petvat.katan.server.service.client

import io.github.petvat.katan.server.service.presenter.ConcurrentKeyedRegistry
import io.github.petvat.katan.server.service.presenter.ConcurrentTypedRegistry
import io.github.petvat.katan.shared.UserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class ClientRegistry : ConcurrentKeyedRegistry<ClientId, ConnectedClient>() {

    private val reconnectScope = CoroutineScope(Dispatchers.Default)
    private val graceWindow = Duration.ofSeconds(30)

    fun markDisconnected(clientId: ClientId, onExpired: suspend (ConnectedClient) -> Unit) {
        val client = get(clientId) ?: return
        client.connection = null
        client.disconnectedAt = Instant.now()

        reconnectScope.launch {
            delay(graceWindow.toMillis())
            // Exipre connection if still disconnected after grace window
            if (client.connection == null && client.disconnectedAt != null) {
                unregister(clientId)
                onExpired(client) // Deattach client
            }
        }
    }

    fun markReconnected(clientId: ClientId, connection: Connection) {
        val client = get(clientId) ?: error("Reconnecting client not found: $clientId")
        client.connection = connection
        client.disconnectedAt = null
    }
}


class UserRegistry : ConcurrentKeyedRegistry<UserId, Auth>() { // HACK: ?
    private val clients = ConcurrentHashMap<UserId, MutableList<ClientId>>()

    fun getClientsByUser(userId: UserId): List<ClientId> {
        return clients[userId]?.toList() ?: emptyList()
    }

    fun addClientLink(userId: UserId, clients: Collection<ClientId>) {
        this.clients[userId]?.addAll(clients.toSet())
    }
}
