package io.github.petvat.katan.server.service.event

import io.github.petvat.katan.server.service.engine.GameSnapshot
import io.github.petvat.katan.shared.protocol.ErrorCode

sealed interface Event {
    val description: String

    data class Failure(override val description: String, val code: ErrorCode) : LobbyEvent, GameEvent, GroupEvent {

        // TODO: fix this.
        override fun applyTo(snapshot: GameSnapshot): GameSnapshot {
            throw NotImplementedError()
        }
    }
}

