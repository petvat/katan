package io.github.petvat.katan.event


import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.session.PublicUserView
import io.github.petvat.katan.shared.protocol.dto.PrivateGroupView
import io.github.petvat.katan.ui.model.PlayerColor

sealed interface Event

sealed interface UiEvent

data class BuildUiEvent(val playerColor: PlayerColor, val coordinates: Coordinates, val buildKind: BuildKind) : UiEvent

data class PlaceBuildingUiEvent(val buildKind: BuildKind) : UiEvent


interface EventListener {
    fun onEvent(event: Event)
}


/**
 * This
 */
data object GetGroupsEvent : Event

/**
 * This event firest when this user has joined a group.
 */
data class JoinEvent(val privateSessionInfo: PrivateGroupView) : Event


/**
 * This event fires when a user has joined the group.
 */
data class UserJoinedEvent(val publicUserView: PublicUserView) : Event


/**
 * This event fires on a successful game intialization.
 */
data object InitEvent : Event


/**
 * This event fires
 */
data object LobbyEvent : Event


/**
 *
 * This event fires on a successful group creation.
 */
data object CreateEvent : Event


/**
 * This event fires on receiving a new chat Event.
 */
data class ChatEvent(val name: String, val message: String) : Event


/**
 * This event fires on successful login.
 */
data object LoginEvent : Event


// TODO: Do it non-optimal way first then change later if needed.
//  Other: Change ViewModel based on events

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
    val moveRobber: Boolean,
    val playerResourceDiff: ResourceMap?,
    val otherPlayersCardCounts: Map<Int, Int>?
) : Event

/**
 * This event fires indicating the start of the next turn.
 */
data class NextTurnEvent(val playerNumber: Int) : Event // Check if possible.


/**
 * This event fires after a building has been built.
 */
data class BuildEvent(val playerNumber: Int, val buildKind: BuildKind, val coordinates: Coordinates) : Event


/**
 * This event fires if there occured and error.
 */
data class ErrorEvent(val reason: String) : Event


/**
 * This event fires after an initial building has been built.
 */
data class PlaceInitialSettlementEvent(val playerNumber: Int, val coordinates: Coordinates) : Event

