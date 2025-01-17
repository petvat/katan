package io.github.petvat.katan.shared.protocol.dto


import io.github.petvat.katan.shared.model.game.Settings
import kotlinx.serialization.Serializable


/**
 * This class encapsules all requests that are not related to a specific game of Katan.
 */
@Serializable
sealed class Request() : PayloadDTO {

    @Serializable
    data class Join(
        val sessionId: String
    ) : Request()

    @Serializable
    data class Create(
        val settings: Settings,
        val silent: Boolean
    ) : Request()

    @Serializable
    data class Login(
        val username: String,
        val password: String
    ) : Request()

    @Serializable
    data class Chat(
        val message: String,
        val recipients: Set<String>
    ) : Request()

    @Serializable
    data class Groups(
        val pagination: Int
    ) : Request()

    @Serializable
    data object Empty : Request()

    @Serializable
    data object Init : Request()

    @Serializable
    data object Leave : Request()
}

