package io.github.petvat.katan.server.service.client

import io.github.petvat.katan.shared.UserId
import java.nio.channels.SocketChannel
import java.time.Instant

import kotlin.concurrent.Volatile

@JvmInline
value class ClientId(val value: String)


@JvmInline
value class ConnectionId(val value: String)


data class Connection(
    val id: ConnectionId, // TODO: USeful?
    val socketChannel: SocketChannel
)


/*
 TODO: Do instead:

 ConnectedClient(
 id: ClientId
 auth/user: Auth


  NOTE: Because Auth should be used on model.
 */


data class ConnectedClient(
    val id: ClientId,
    @Volatile var auth: Auth = Auth.Unauth,

    @Volatile
    var connection: Connection? = null // null while disconnected
) {

    @Volatile
    var disconnectedAt: Instant? = null

    val isConnected get() = connection != null
}

sealed interface AuthCredentials {
    data class Guest(val name: String) : AuthCredentials
    data class User(val name: String, val psw: String) : AuthCredentials
    data class Admin(val name: String, val secret: String) : AuthCredentials
}

sealed interface Auth {
    val id: UserId
    val name: String

    data object Unauth : Auth {
        override val name get() = "unauth"
        override val id: UserId
            get() = error("Unauth client.")
    }

    data class Guest(
        override val name: String,
        override val id: UserId
    ) : Auth {
        init {
            require(id.value.startsWith("guest"))
        }

    }

    data class User(
        override val name: String,
        val psw: String,
        override val id: UserId
    ) : Auth {
        init {
            require(id.value.startsWith("user"))
        }
    }

}

