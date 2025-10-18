package io.github.petvat.katan.server.service.event

import io.github.petvat.katan.server.service.engine.GameSnapshot
import io.github.petvat.katan.server.service.engine.Phase
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.PlayerId
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.ResourceMap


sealed interface GameEvent : Event {

    fun applyTo(snapshot: GameSnapshot): GameSnapshot


    data class DiceRolledSummary(
        val roll1: Int,
        val roll2: Int,
        val resources: Map<Int, ResourceMap>,
        val nextPhase: Phase
    ) : GameEvent {
        override fun applyTo(snapshot: GameSnapshot): GameSnapshot {
            TODO()
        }

        override val description: String
            get() = "Dice rolled."
    }

    data class CardStolen(
        val victim: PlayerId,
        val resources: ResourceMap
    ) : GameEvent {
        override fun applyTo(snapshot: GameSnapshot): GameSnapshot {
            TODO("Not yet implemented")
        }

        override val description = "Stole from $victim!"
    }

    data class Built(
        val coordinates: Coordinates,
        val buildKind: BuildKind,
        val vps: Map<Int, Int>
    ) : GameEvent {
        override fun applyTo(snapshot: GameSnapshot): GameSnapshot {
            TODO("Not yet implemented")
        }

        override val description = "$buildKind was built at $coordinates."
    }

}
