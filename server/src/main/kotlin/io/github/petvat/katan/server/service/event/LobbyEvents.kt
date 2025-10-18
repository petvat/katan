package io.github.petvat.katan.server.service.event

import io.github.petvat.katan.shared.UserId

sealed interface LobbyEvent : Event {
    data class Registered(val userId: UserId) : LobbyEvent {
        override val description: String
            get() = "TODO: Registred description"
    }
}
