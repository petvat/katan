package io.github.petvat.katan.server.service.client

import io.github.petvat.katan.server.service.session.Session
import io.github.petvat.katan.shared.UserId

@JvmInline
value class ClientId(val value: String)


data class ConnectedClient(
    val id: ClientId,
    var auth: Auth,
    val sessions: List<Session>
) {
    init {
        require(
            id.value.startsWith(
                when (auth) {
                    is Auth.Guest -> "guest:"
                    is Auth.Unauth -> "unauth:"
                    is Auth.User -> "user:"
                }
            )
        )
    }
}

sealed interface AuthCredentials {
    data class Guest(val name: String) : AuthCredentials
    data class User(val name: String) : AuthCredentials
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
    ) : Auth

    data class User(
        override val name: String,
        override val id: UserId
    ) : Auth

}

