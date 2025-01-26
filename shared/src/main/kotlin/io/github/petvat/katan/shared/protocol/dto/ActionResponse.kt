package io.github.petvat.katan.shared.protocol.dto

import io.github.petvat.katan.shared.protocol.ActionCode
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.session.PrivateGameState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * This encapsules all responses related to a specific game of Katan.
 */
@Serializable
sealed class ActionResponse : Response(), ActionDTO {

    // NOTE: TurnState? Easier game state for everything in client?

    /**
     * Represents a response after a RollDice command.
     *
     * @property otherPlayersResources The cards of the other people, as they are hidden
     * @property playerNumber The player that performed this action.
     */
    @Serializable
    data class RollDice(
        val playerNumber: Int,
        val roll1: Int,
        val roll2: Int,
        val resources: ResourceMap,
        val otherPlayersResources: Map<Int, Int>, // NOTE: Settings.CARD_VISIBILITY eventually
        val moveRobber: Boolean
    ) : ActionResponse() {
        override val actionCode = ActionCode.ROLL_DICE
    }

    @Serializable
    data class MoveRobber(
        val playerNumber: Int,
        val newRobberCoordinate: Coordinates,
        val nextState: Boolean = true // TODO: Not necessary
        // val stealCardVictim: Int
    ) : ActionResponse() {
        override val actionCode = ActionCode.MOVE_ROBBER
    }

    @Serializable
    data class StealCard(
        val playerNumber: Int,
        val stealCardVictim: Int,
        val publicGameState: PrivateGameState // Check diff in client
    ) : ActionResponse() {
        override val actionCode = ActionCode.STEAL_CARD
    }

    // TODO: Use intersectionView

    @Serializable
    class Build(
        val playerNumber: Int,
        val buildKind: BuildKind,
        val coordinate: Coordinates,
        val victoryPoints: Int,
        val newLongestRoad: Boolean,
        val longestRoadHolder: Int?
    ) : ActionResponse() {
        override val actionCode = ActionCode.BUILD
    }

    @Serializable
    class InitiateTrade(
        val tradeId: Int,
        val targetPlayers: Set<Int>,
        val offer: ResourceMap,
        val request: ResourceMap
    ) : ActionResponse() {
        override val actionCode = ActionCode.INIT_TRADE
    }


    /**
     * Response to a trade.
     */
    @Serializable
    class RespondTrade(
        val responderId: Int,
        val tradeId: Int,
        val accept: Boolean,
        val publicGameState: PrivateGameState? // Present if accept is true
        // val inventory, Player could keep track of this
        // val responderCardCount: Int
    ) : ActionResponse() {
        override val actionCode = ActionCode.RESPOND_TRADE
    }

    @Serializable
    class EndTurn(
        val playerNumber: Int,
        val nextPlayer: Int
    ) : ActionResponse() {
        override val actionCode = ActionCode.TURN_END
    }

    /**
     * Response when setup i.e. placing of initial settlments has ended only.
     * This DTO compromises BuildResponse while also returning the players initial resources.
     *
     * NOTE: NOT USED.
     *
     */
    @Serializable
    data class SetupEnded(
        val coordinate: Coordinates,
        val inventory: ResourceMap,
        val otherPlayersResources: Map<Int, Int>,
    ) : ActionResponse() {
        override val actionCode = ActionCode.SETUP_END
    }

    @Serializable
    @SerialName("InitSettl")
    data class PlaceInitSettlement(
        val coordinates: Coordinates,
        val setupEnded: Boolean,
        val inventory: ResourceMap?,
        val otherPlayersResources: Map<Int, Int>?
    ) : ActionResponse() {
        override val actionCode = ActionCode.INIT_SETTL
    }


    @Serializable
    data class VictoryClaimed(
        val victoriousPlayer: Int,
        // TODO: More stuff
    ) : ActionResponse() {
        override val actionCode = ActionCode.CLAIM_VICTORY
    }
}

//
//data class ActionResponse(
//    override val header: ResponseHeader,
//    override val payload: ResponsePayLoad<ActionResponse(>
//) : ResponseMessage<ActionResponse(>(header, payload)
//
//data class RollDiceResDTO(
//    val roll1: Int,
//    val roll2: Int,
//    val resources: ResourceMap,
//    val otherPlayersResources: Map<Int, Int>,
//    val moveRobber: Boolean
//) : ActionResponse(() {
//    override val actionCode = ActionCode.ROLL_DICE
//}
//
//data class MoveRobberResDTO(
//    val newRobberCoordinate: Coordinate,
//    val nextState: Boolean = true // TODO: Not necessary
//    // val stealCardVictim: Int
//) : ActionResponse(() {
//    override val actionCode = ActionCode.MOVE_ROBBER
//
//}
//
//data class StealCardResDTO(
//    val stealCardVictim: Int,
//    val privateGameState: PublicGameState // Check diff in client
//) : ActionResponse(() {
//    override val actionCode = ActionCode.MOVE_ROBBER
//
//}
//
//class BuildResDTO(
//    val buildKind: BuildKind,
//    val coordinate: Coordinate,
//    val newLongestRoad: Boolean,
//    val longestRoadHolder: Int?
//) : ActionResponse(() {
//    override val actionCode = ActionCode.BUILD
//
//}
//
//
//class InitiateTradeResDTO(
//    val tradeId: Int,
//    val targetPlayers: Set<Int>,
//    val offer: ResourceMap,
//    val request: ResourceMap
//) : ActionResponse(() {
//    override val actionCode = ActionCode.INIT_TRADE
//
//}
//
///**
// * Response to a trade.
// */
//class TradeResDTO(
//    val responderId: Int,
//    val tradeId: Int,
//    val accept: Boolean,
//    // val inventory, Player could keep track of this
//    // val responderCardCount: Int
//) : ActionResponse(() {
//    override val actionCode = ActionCode.RESPOND_TRADE
//
//}
//
//class TurnEndedResDTO(
//    val nextPlayer: Int
//) : ActionResponse(() {
//    override val actionCode = ActionCode.TURN_END
//
//}
//
//data class PlacedInitialSettlementDTO(val coordinate: Coordinate) : ActionResponse(() {
//    override val actionCode = ActionCode.BUILD
//}
//
///**
// * Response when setup has ended only.
// */
//data class SetupEndedDTO(
//    val coordinate: Coordinate, // Temporary fix!
//    val inventory: ResourceMap
//) : ActionResponse(() {
//    override val actionCode = ActionCode.SETUP_END
//
//}
