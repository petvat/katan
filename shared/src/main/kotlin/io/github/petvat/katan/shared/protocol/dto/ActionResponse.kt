package io.github.petvat.katan.shared.protocol.dto

import io.github.petvat.katan.shared.protocol.ActionCode
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//
///**
// * This encapsules all responses related to a specific game of Katan.
// */
//@Serializable
//sealed class ActionResponse : Response(), ActionDTO {
//
//    // NOTE: TurnState? Easier game state for everything in client?
//
//    /**
//     * Represents a response after a RollDice command.
//     *
//     * @property otherPlayersResources The cards of the other people, as they are hidden
//     * @property playerNumber The player that performed this action.
//     */
//    @Serializable
//    @SerialName("res_dice")
//    data class RollDice(
//        val playerNumber: Int,
//        val roll1: Int,
//        val roll2: Int,
//        val resources: ResourceMap,
//        val otherPlayersResources: Map<Int, Int>, // NOTE: Settings.CARD_VISIBILITY eventually
//        val moveRobber: Boolean
//    ) : ActionResponse() {
//        override val actionCode = ActionCode.ROLL_DICE
//    }
//
//    @Serializable
//    @SerialName("res_robber")
//    data class MoveRobber(
//        val playerNumber: Int,
//        val newRobberCoordinate: Coordinates,
//        val nextState: Boolean = true // TODO: Not necessary
//        // val stealCardVictim: Int
//    ) : ActionResponse() {
//        override val actionCode = ActionCode.MOVE_ROBBER
//    }
//
//    @Serializable
//    @SerialName("res_steal")
//    data class StealCard(
//        val playerNumber: Int,
//        val stealCardVictim: Int,
//        val publicGameState: PrivateGameState // Check diff in client
//    ) : ActionResponse() {
//        override val actionCode = ActionCode.STEAL_CARD
//    }
//
//    // TODO: Use intersectionView
//
//    @Serializable
//    @SerialName("res_build")
//    class Build(
//        val playerNumber: Int,
//        val buildKind: BuildKind,
//        val coordinate: Coordinates,
//        val victoryPoints: Int,
//        val newLongestRoad: Boolean,
//        val longestRoadHolder: Int?
//    ) : ActionResponse() {
//        override val actionCode = ActionCode.BUILD
//    }
//
//    @Serializable
//    @SerialName("res_tradeInit")
//    class InitiateTrade(
//        val tradeId: Int,
//        val targetPlayers: Set<Int>,
//        val offer: ResourceMap,
//        val request: ResourceMap
//    ) : ActionResponse() {
//        override val actionCode = ActionCode.INIT_TRADE
//    }
//
//
//    /**
//     * Response to a trade.
//     */
//    @Serializable
//    @SerialName("res_tradeRes")
//    class RespondTrade(
//        val responderId: Int,
//        val tradeId: Int,
//        val accept: Boolean,
//        val publicGameState: PrivateGameState? // Present if accept is true
//        // val inventory, Player could keep track of this
//        // val responderCardCount: Int
//    ) : ActionResponse() {
//        override val actionCode = ActionCode.RESPOND_TRADE
//    }
//
//    @Serializable
//    @SerialName("res_endTurn")
//    class EndTurn(
//        val playerNumber: Int,
//        val nextPlayer: Int
//    ) : ActionResponse() {
//        override val actionCode = ActionCode.TURN_END
//    }
//
//    /**
//     * Response when setup i.e. placing of initial settlments has ended only.
//     * This DTO compromises BuildResponse while also returning the players initial resources.
//     *
//     * NOTE: NOT USED.
//     *
//     */
//    @Serializable
//    @SerialName("res_setupEnd")
//    data class SetupEnded(
//        val coordinate: Coordinates,
//        val inventory: ResourceMap,
//        val otherPlayersResources: Map<Int, Int>,
//    ) : ActionResponse() {
//        override val actionCode = ActionCode.SETUP_END
//    }
//
//    @Serializable
//    @SerialName("res_initBuild")
//    data class InitBuild(
//        val playerNumber: Int,
//        val coordinates: Coordinates,
//        val buildKind: BuildKind,
//        val setupEnded: Boolean,
//        val inventory: ResourceMap?,
//        val otherPlayersResources: Map<Int, Int>?
//    ) : ActionResponse() {
//        override val actionCode = ActionCode.INIT_BUILD
//    }
//
//    @Serializable
//    @SerialName("res_victory")
//    data class VictoryClaimed(
//        val victoriousPlayer: Int,
//        // TODO: More stuff
//    ) : ActionResponse() {
//        override val actionCode = ActionCode.CLAIM_VICTORY
//    }
//}
