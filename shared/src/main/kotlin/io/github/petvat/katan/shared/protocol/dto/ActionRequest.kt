package io.github.petvat.katan.shared.protocol.dto

import io.github.petvat.katan.shared.protocol.ActionCode
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import kotlinx.serialization.Serializable

/**
 * This class encapsules all requests related to a game of Katan.
 *
 * The [ActionDTO] determines the specific action.
 */
@Serializable
sealed class ActionRequest() : Request(), ActionDTO {

    @Serializable
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
    data class InitiateTrade(
        val tradeID: Int,
        val targetPlayersID: Set<Int>,
        val tradeOffer: ResourceMap,
        val tradeInReturn: ResourceMap,
    ) : ActionRequest() {
        override val actionCode = ActionCode.INIT_TRADE
    }

    @Serializable
    data class RespondTrade(
        val tradeID: Int,
        val accept: Boolean
    ) : ActionRequest() {
        override val actionCode = ActionCode.RESPOND_TRADE
    }

    @Serializable
    data class StealCard(
        val stealFromPlayerID: Int
    ) : ActionRequest() {
        override val actionCode = ActionCode.STEAL_CARD
    }

    @Serializable
    data class MoveRobber(
        val newTileCoordinate: Coordinates
    ) : ActionRequest() {
        override val actionCode = ActionCode.MOVE_ROBBER
    }

    @Serializable
    data object ClaimVictory : ActionRequest() {
        override val actionCode: ActionCode = ActionCode.CLAIM_VICTORY
    }
}

//
//
///**
// * All game action request builds upon this class.
// *
// * The [ActionRequestDTO] determines the specific action.
// */
////data class ActionRequestRequest(
////    override val header: RequestHeader,
////    override val payload: RequestPayLoad<ActionRequestDTO>
////) : RequestMessage<ActionRequestDTO>(header, payload)
//
//
//abstract class ActionRequestDTO {
//    abstract val actionCode: ActionCode
//}
//
//
//sealed class Response() : PayloadDTO {
//
//    data class Chat(
//        val senderId: Int,
//        val message: String
//    ) : Response()
//
//    data class Create(
//        val groupId: String
//    ) : Response()
//
//    data class Join(
//        val publicUserInfo: PublicUserInfo
//    ) : Response()
//
//    data class Login(
//        val publicUserInfo: PublicUserInfo, // TODO: Private
//        val token: String
//    ) : Response()
//
//    data class Sessions(
//        val groups: Set<SessionDTO>
//    ) : Response()
//
//    data class SessionDTO(
//        val settings: Settings,
//        val hostId: Int,
//        val playersCount: Int
//    ) : Response()
//
//    data class RollDice(
//        val roll1: Int,
//        val roll2: Int,
//        val resources: ResourceMap,
//        val otherPlayersResources: Map<Int, Int>,
//        val moveRobber: Boolean
//    ) : ActionRequestDTO, Response() {
//        override val actionCode = ActionCode.ROLL_DICE
//    }
//
//    data class MoveRobber(
//        val newRobberCoordinate: Coordinate,
//        val nextState: Boolean = true // TODO: Not necessary
//        // val stealCardVictim: Int
//    ) : ActionRequestDTO, Response() {
//        override val actionCode = ActionCode.MOVE_ROBBER
//    }
//
//    data class StealCard(
//        val stealCardVictim: Int,
//        val publicGameState: PublicGameState // Check diff in client
//    ) : ActionRequestDTO, Response() {
//        override val actionCode = ActionCode.STEAL_CARD
//    }
//
//
//    class BuildResDTO(
//        val buildKind: BuildKind,
//        val coordinate: Coordinate,
//        val newLongestRoad: Boolean,
//        val longestRoadHolder: Int?
//    ) : ActionRequestDTO, Response() {
//        override val actionCode = ActionCode.BUILD
//    }
//
//    class InitiateTrade(
//        val tradeId: Int,
//        val targetPlayers: Set<Int>,
//        val offer: ResourceMap,
//        val request: ResourceMap
//    ) : ActionRequestDTO, Response() {
//        override val actionCode = ActionCode.INIT_TRADE
//    }
//
//}
//
///**
// * Response to a trade.
// */
//class RespondTrade(
//    val responderId: Int,
//    val tradeId: Int,
//    val accept: Boolean,
//    // val inventory, Player could keep track of this
//    // val responderCardCount: Int
//) : ActionRequestDTO, Response() {
//    override val actionCode = ActionCode.RESPOND_TRADE
//}
//
//class EndTurn(
//    val nextPlayer: Int
//) : ActionRequestDTO, Response() {
//    override val actionCode = ActionCode.TURN_END
//}
//
///**
// * Response when setup has ended only.
// */
//data class SetupEnded(
//    val coordinate: Coordinate, // Temporary fix!
//    val inventory: ResourceMap
//) : ActionRequestDTO, Response() {
//    override val actionCode = ActionCode.SETUP_END
//}
//
//
//sealed class Request() : PayloadDTO {
//
//    data class Join(
//        val groupId: String
//    ) : Request()
//
//    data class Create(
//        val settings: Settings
//    ) : Request()
//
//    data class Login(
//        val username: String,
//        val password: String
//    ) : Request()
//
//
//    data class Chat(
//        val message: String,
//        val recipients: Set<String>
//    ) : Request()
//
//    data class Build(
//        val buildKind: BuildKind,
//        val coordinate: Coordinate
//    ) : ActionRequestDTO, Request() {
//        override val actionCode = ActionCode.BUILD
//    }
//
//    /**
//     * Request to initiate a new trade offer.
//     *
//     * @see [InitiateTradeDTO]
//     */
//    data class InitiateTrade(
//        val tradeID: Int,
//        val targetPlayersID: Set<Int>,
//        val tradeOffer: ResourceMap,
//        val tradeInReturn: ResourceMap,
//    ) : ActionRequestDTO, Request() {
//        override val actionCode = ActionCode.INIT_TRADE
//    }
//
//    data class RespondTrade(
//        val tradeID: Int,
//        val accept: Boolean
//    ) : ActionRequestDTO, Request() {
//        override val actionCode = ActionCode.RESPOND_TRADE
//    }
//
//    data class StealCard(
//        val stealFromPlayerID: Int
//    ) : ActionRequestDTO, Request() {
//        override val actionCode = ActionCode.STEAL_CARD
//    }
//
//    data class MoveRobber(
//        val newTileCoordinate: Coordinate
//    ) : ActionRequestDTO, Request() {
//        override val actionCode = ActionCode.MOVE_ROBBER
//    }
//}
//
//data class Payload<out D>(
//    val success: Boolean? = null,
//    val description: String? = null,
//    val data: D? = null
//)
//
//data class RequestMessage<out R : Request>(
//    val header: Header,
//    val payload: Payload<R>
//)
//
//
///**
// * Request to build a structure at coordinate. Could be intersection or path structure.
// *
// * @see [BuildDTO]
// */
//data class BuildRequestDTO(
//    val buildKind: BuildKind,
//    val coordinate: Coordinate
//) : ActionRequestDTO {
//    override val actionCode = ActionCode.BUILD
//}
//
///**
// * Request to initiate a new trade offer.
// *
// * @see [InitiateTradeDTO]
// */
//data class InitiateTradeRequestDTO(
//    val tradeID: Int,
//    val targetPlayersID: Set<Int>,
//    val tradeOffer: ResourceMap,
//    val tradeInReturn: ResourceMap,
//) : ActionRequestDTO() {
//    override val actionCode = ActionCode.INIT_TRADE
//}
//
//data class RespondTradeRequestDTO(
//    val tradeID: Int,
//    val accept: Boolean
//) : ActionRequestDTO() {
//    override val actionCode = ActionCode.RESPOND_TRADE
//}
//
//data class StealCardRequestDTO(
//    val stealFromPlayerID: Int
//) : ActionRequestDTO() {
//    override val actionCode = ActionCode.STEAL_CARD
//}
//
//data class MoveRobberRequestDTO(
//    val newTileCoordinate: Coordinate
//) : ActionRequestDTO() {
//    override val actionCode = ActionCode.MOVE_ROBBER
//}

