package io.github.petvat.katan.shared.protocol.dto

import io.github.petvat.katan.shared.protocol.ActionCode
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This class encapsules all requests related to a game of Katan.
 *
 * The [ActionDTO] determines the specific action.
 */
@Serializable
sealed class ActionRequest() : Request(), ActionDTO {

    @Serializable
    @SerialName("req_dice")
    data object RollDice : ActionRequest() {
        override val actionCode = ActionCode.ROLL_DICE
    }

    @Serializable
    @SerialName("req_build")
    data class Build(
        val buildKind: BuildKind,
        val coordinate: Coordinates
    ) : ActionRequest() {
        override val actionCode = ActionCode.BUILD
    }

    /**
     * Request to initiate a new trade offer.
     */
    @Serializable
    @SerialName("req_initTrade")
    data class InitiateTrade(
        val tradeID: Int,
        val targetPlayersID: Set<Int>,
        val tradeOffer: ResourceMap,
        val tradeInReturn: ResourceMap,
    ) : ActionRequest() {
        override val actionCode = ActionCode.INIT_TRADE
    }

    @Serializable
    @SerialName("req_resTrade")
    data class RespondTrade(
        val tradeID: Int,
        val accept: Boolean
    ) : ActionRequest() {
        override val actionCode = ActionCode.RESPOND_TRADE
    }

    @Serializable
    @SerialName("req_steal")
    data class StealCard(
        val stealFromPlayerID: Int
    ) : ActionRequest() {
        override val actionCode = ActionCode.STEAL_CARD
    }

    @Serializable
    @SerialName("req_robber")
    data class MoveRobber(
        val newTileCoordinate: Coordinates
    ) : ActionRequest() {
        override val actionCode = ActionCode.MOVE_ROBBER
    }

    @Serializable
    @SerialName("req_victory")
    data object ClaimVictory : ActionRequest() {
        override val actionCode: ActionCode = ActionCode.CLAIM_VICTORY
    }

    @Serializable
    @SerialName("req_endTurn")
    data object EndTurn : ActionRequest() {
        override val actionCode = ActionCode.TURN_END
    }
}
