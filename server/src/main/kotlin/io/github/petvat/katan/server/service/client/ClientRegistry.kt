package io.github.petvat.katan.server.service.client

import io.github.petvat.katan.server.service.presenter.ConcurrentKeyedRegistry
import io.github.petvat.katan.shared.UserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

/**
 * since a user can have multiple connections (that's the whole point of the multi-client fan-out), you may eventually want getClientsByUser to derive live clients from clientRegistry instead of maintaining a parallel mutable map — e.g. keep only UserId → Set<ClientId> registration and filter by clientRegistry.get(it)?.isConnected == true at fan-out time. Otherwise disconnected/zombie client ids will linger in the link map forever and mapNotNull will keep silently filtering them. For now, fixes 1+2 unblock you.
 */
class ClientRegistry : ConcurrentKeyedRegistry<ClientId, ConnectedClient>() {

    private val reconnectScope = CoroutineScope(Dispatchers.Default)
    private val graceWindow = Duration.ofSeconds(30)

    private val clientsByUser = ConcurrentHashMap<UserId, MutableSet<ClientId>>()


    fun link(auth: Auth, clientId: ClientId) {
        if (auth is Auth.Unauth) return
        clientsByUser.getOrPut(auth.id) { ConcurrentHashMap.newKeySet() }.add(clientId)
    }

    /**
     * Live clients of a user.
     */
    fun clientsOf(userId: UserId): List<ConnectedClient> =
        clientsByUser[userId].orEmpty().mapNotNull { get(it) }.filter { it.isConnected }

    private fun remove(clientId: ClientId) {
        val client = get(clientId) ?: return
        if (client.connection != null) return // Client reconnected before disconnect task.
        clientsByUser.values.forEach { it.remove(clientId) }
        unregister(clientId)
    }

    fun markDisconnected(clientId: ClientId, onExpired: suspend (ConnectedClient) -> Unit) {
        val client = get(clientId) ?: return
        if (client.connection == null) return // already in grace window, skip
        client.connection = null
        client.disconnectedAt = Instant.now()

        reconnectScope.launch {
            delay(graceWindow.toMillis())
            // Exipre connection if still disconnected after grace window
            if (client.connection == null && client.disconnectedAt != null) {
                remove(clientId) // Purge link and unregister
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


/**
 * TODO: User Registry should be [User] class.
 */
class UserRegistry : ConcurrentKeyedRegistry<UserId, Auth>()
