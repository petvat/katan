package io.github.petvat.katan.shared.protocol

import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.dto.GameStateDTO
import io.github.petvat.katan.shared.protocol.dto.PrivateGroupDTO
import io.github.petvat.katan.shared.protocol.dto.PublicGroupDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


enum class MTypes() {

    REQ_JOIN,
    REQ_CREATE,

    // TCP only?
    REQ_REG_GST,
    REQ_REG,

    REQ_LEAVE,
    REQ_CHAT,
    REQ_INIT,

    REQ_GAMEACTION,
}

@Serializable
sealed class Request {
    abstract val requestId: Int

    abstract val type: MTypes


    /// REG REQUESTS

    @Serializable
    @SerialName("reg-guest")
    data class GuestRegister(override val requestId: Int, val name: String) : Request() {
        @Transient
        override val type = MTypes.REQ_REG_GST
    }

    @Serializable
    @SerialName("reg")
    data class Register(override val requestId: Int, val username: String, val psw: String) : Request() {
        @Transient
        override val type = MTypes.REQ_REG
    }

    // **************
    // LOBBY REQUESTS

    @Serializable
    @SerialName("join")
    data class Join(override val requestId: Int, val groupId: String) : Request() {
        @Transient
        override val type = MTypes.REQ_JOIN
    }

    @Serializable
    @SerialName("create")
    data class Create(override val requestId: Int, val settings: Settings) : Request() {
        @Transient
        override val type = MTypes.REQ_CREATE
    }

    // **************
    // GROUP REQUESTS
    @Serializable
    @SerialName("leave")
    data class Leave(override val requestId: Int) : Request() {
        @Transient
        override val type = MTypes.REQ_LEAVE
    }

    @Serializable
    @SerialName("chat")
    data class Chat(override val requestId: Int, val message: String) : Request() {
        @Transient
        override val type = MTypes.REQ_CHAT
    }

    @Serializable
    @SerialName("init")
    data class Init(override val requestId: Int) : Request() {
        @Transient
        override val type = MTypes.REQ_INIT
    }

    // *************
    // GAME REQUESTS

    @Serializable
    @SerialName("rolldice")
    data class RollDice(override val requestId: Int) : Request() {
        @Transient
        override val type = MTypes.REQ_GAMEACTION
    }

    @Serializable
    @SerialName("move_robber")
    data class MoveRobber(override val requestId: Int, val coordinates: HexCoordinates) : Request() {
        @Transient
        override val type = MTypes.REQ_GAMEACTION
    }

    @Serializable
    @SerialName("build")
    data class Build(override val requestId: Int, val buildkind: BuildKind, val coordinates: Coordinates) : Request() {
        @Transient
        override val type = MTypes.REQ_GAMEACTION
    }

    @Serializable
    @SerialName("init_build")
    data class BuildInitSettl(override val requestId: Int, val coordinates: Coordinates) : Request() {
        @Transient
        override val type = MTypes.REQ_GAMEACTION
    }

    @Serializable
    @SerialName("steal")
    data class Steal(override val requestId: Int, val playerNumber: Int) : Request() {
        @Transient
        override val type = MTypes.REQ_GAMEACTION
    }


    @Serializable
    @SerialName("init_trade")
    data class InitTrade(
        override val requestId: Int,
        val targetPlayers: Set<Int>,
        val offer: ResourceMap,
        val inReturn: ResourceMap
    ) : Request() {
        @Transient
        override val type = MTypes.REQ_GAMEACTION
    }

    @Serializable
    @SerialName("end_turn")
    data class EndTurn(override val requestId: Int) : Request() {
        @Transient
        override val type = MTypes.REQ_GAMEACTION
    }


    @Serializable
    @SerialName("claim_vict")
    data class ClaimVictory(override val requestId: Int) : Request() {
        @Transient
        override val type = MTypes.REQ_GAMEACTION
    }
}

@Serializable
enum class ErrorCode {
    GROUP_FULL,
    DENIED,
    FMT,
    NOT_FOUND,
    SERVER_ERROR
}

@Serializable
sealed class Response {

    abstract val description: String?

    // LOBBY RESPONSES

    @Serializable
    @SerialName("registered")
    data class Registered(val sid: String, override val description: String?) : Response()

    @Serializable
    @SerialName("lobby_update")
    data class LobbyUpdate(val groupDTO: PublicGroupDTO, override val description: String?) : Response()

    @Serializable
    @SerialName("group_created")
    data class GroupCreated(
        val groupId: String,
        val level: PermissionLevel,
        val settings: Settings,
        override val description: String?
    ) : Response()

    // GROUP RESPONSES

    /**
     * TODO: Use PublicUserDTO
     */
    @Serializable
    @SerialName("user_joined")
    data class UserJoined(val sessionId: String, val name: String, override val description: String?) : Response()

    @Serializable
    @SerialName("joined_ok")
    data class Joined(val groupDTO: PrivateGroupDTO, override val description: String?) : Response()

    @Serializable
    @SerialName("user_left")
    data class Left(val sessionId: String, override val description: String?) : Response()

    /**
     * NOTE: currently assumes that gameId is the same as the GroupId.
     */
    @Serializable
    @SerialName("game_init")
    data class Init(val privateGameState: GameStateDTO, override val description: String?) : Response()

    @Serializable
    @SerialName("dice_rolled")
    data class DiceRolled(
        val playerNumber: Int,
        val roll1: Int,
        val roll2: Int,
        val resources: ResourceMap,
        val othersResources: Map<Int, ResourceMap>,
        val moveRobber: Boolean,
        override val description: String?
    ) : Response()

    @Serializable
    @SerialName("new_build")
    data class Build(
        val builder: Int,
        val buildkind: BuildKind,
        val coordinates: Coordinates,
        val victoryPoints: Int,
        override val description: String?
    ) : Response()


    @Serializable
    @SerialName("setup_ended")
    data class SetupEnded(
        val build: Build,
        val thisPlayer: ResourceMap,
        val otherPlayers: Map<Int, ResourceMap>,
        override val description: String?
    ) : Response()

    @Serializable
    @SerialName("new_chat")
    data class Chat(
        val from: String,
        val message: String,
        override val description: String?
    ) : Response()

    // GAME RESPONSES
    // IN COMMON : PlayerNumber

    @Serializable
    @SerialName("victory_claimed")
    data class VictoryClaimed(
        val winner: Int,
        override val description: String?
    ) : Response()

    @Serializable
    @SerialName("end_turn")
    data class EndTurn(
        override val description: String?
    ) : Response()

    @Serializable
    @SerialName("robber_moved")
    data class RobberMoved(
        val playerNumber: Int,
        val coordinates: Coordinates,
        override val description: String?
    ) : Response()

    @Serializable
    @SerialName("trade_inited")
    data class InitTrade(
        val playerNumber: Int,
        val tradeId: Int,
        val targetPlayers: Set<Int>,
        val offer: ResourceMap,
        val inReturn: ResourceMap,
        override val description: String?
    ) : Response()

    @Serializable
    @SerialName("trade_response")
    data class TradeResponse(
        val playerNumber: Int,
        val tradeId: Int,
        val accept: Boolean,
        override val description: String?
    ) : Response()


    // ALL

    @Serializable
    @SerialName("error")
    data class Error(val requestId: Int, val code: ErrorCode, override val description: String?) : Response()

    /**
     * Used for simple acknowledgement responses.
     */
    @Serializable
    @SerialName("ok")
    data class OK(val requestId: Int, override val description: String?) : Response()
}


//@Serializable
//data class Messages(
//    val header: Header,
//    val payload: Payloads
//
//
//) {
//    init {
//        when (header.messageType) {
//            MessageType.CHAT -> TODO()
//            MessageType.ACTION -> TODO()
//            MessageType.JOIN -> TODO()
//            MessageType.CREATE -> TODO()
//            MessageType.INIT -> TODO()
//            MessageType.LOGIN -> TODO()
//            MessageType.GET_GROUPS -> TODO()
//            MessageType.ACK -> TODO()
//            MessageType.GROUP_PUSH -> TODO()
//        }
//    }
//}
//
//
//@Serializable
//sealed interface Payloads
//
//@Serializable
//sealed class Req : Payloads {
//
//    data class Join(
//        val groupId: String,
//    ) : Req()
//}
//
//@Serializable
//sealed class Res : Payloads {
//    abstract val description: String?
//
//    data class Join(
//        val groupId: String,
//        override val description: String? = null
//    ) : Res()
//
//    data class Error(
//        val code: String,
//        override val description: String
//    ) : Res()
//}
//
//
//sealed class ActionReq : Payloads {
//    abstract val actionCode: ActionCode
//
//    data object RollDice : ActionReq() {
//        override val actionCode = ActionCode.ROLL_DICE
//    }
//
//}
