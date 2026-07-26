package io.github.petvat.katan.event


import io.github.petvat.katan.model.GroupSummary
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.GameMode
import io.github.petvat.katan.shared.model.game.ResourceMapData
import io.github.petvat.katan.shared.protocol.ErrorCode

sealed interface Event


interface EventListener {
    fun onEvent(event: Event)
}


/**
 * This event fires when this user has joined a group.
 *
 *
 */
data object JoinEvent : Event


/**
 * This event fires when a user has joined the group.
 */
data object UserJoinedEvent : Event


/**
 * This event fires on a successful ktxCtx intialization.
 */
data object InitEvent : Event


data class SetupEndedEvent(
    val turnPlayer: Int,
    val playerNumber: Int,
    val buildKind: BuildKind,
    val coordinates: Coordinates
) : Event


/**
 *
 * This event fires on a successful group creation.
 */
data object CreateEvent : Event

data class GroupUpdateEvent(
    val groupSummary: GroupSummary
) : Event


/**
 * This event fires on receiving a new chat Event.
 */
data class ChatEvent(val from: String, val message: String) : Event


/**
 * This event fires on successful login.
 */
data object LoginEvent : Event

/**
 * Feedback
 */
data object ConnectionEvent : Event


/**
 *
 * This event fires after a the dice has been rolled.
 *
 * @param playerResourceDiff The difference between player inventory before and after this event.
 * @param otherPlayersCardCounts The difference between card count before and after this event for all other players.
 *
 * TODO: Use this for animations in the future
 */
data class RolledDiceEvent(
    val roll1: Int,
    val roll2: Int,
    val moveRobber: Boolean
) : Event


data object MyTurnEvent : Event

data object MyTurnSetupEvent : Event


/**
 * This event fires indicating the start of the next turn.
 */
data class NextTurnEvent(val playerNumber: Int) : Event


/**
 * This event fires after a building has been built.
 */
data class BuildEvent(val playerNumber: Int, val buildKind: BuildKind, val coordinates: Coordinates) : Event


/**
 * This event fires if there occured and error.
 */
data class ErrorEvent(val reason: String, val code: ErrorCode? = null) : Event

/**
 * This event fires after an initial building has been built.
 */
data class PlaceInitialSettlementEvent(val playerNumber: Int, val coordinates: ICoordinates) : Event

