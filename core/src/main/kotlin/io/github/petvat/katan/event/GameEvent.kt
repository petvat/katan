package io.github.petvat.katan.event

import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind


data object MyTurnEvent : Event

data object MyTurnSetupEvent : Event

data class VictoryEvent(val winner: Int) : Event

data class SetupEndedEvent(
    val turnPlayer: Int,
    val playerNumber: Int,
    val buildKind: BuildKind,
    val coordinates: Coordinates
) : Event

/**
 * This event fires indicating the start of the next turn.
 */
data class NextTurnEvent(val playerNumber: Int) : Event


/**
 * This event fires after a building has been built.
 */
data class BuildEvent(val playerNumber: Int, val buildKind: BuildKind, val coordinates: Coordinates) : Event

data object ResyncEvent : Event

data class RolledDiceEvent(
    val roll1: Int,
    val roll2: Int,
    val moveRobber: Boolean
) : Event


