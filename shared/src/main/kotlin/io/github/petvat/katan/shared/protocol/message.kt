package io.github.petvat.katan.shared.protocol


//enum class MTypes() {
//
//    REQ_JOIN,
//    REQ_CREATE,
//
//    // TCP only?
//    REQ_REG_GST,
//    REQ_REG,
//
//    REQ_LEAVE,
//    REQ_CHAT,
//    REQ_INIT,
//
//    REQ_GAMEACTION,
//}
//
//@Serializable
//sealed class Request2 {
//    abstract val seq: Int
//    abstract val channel: String
//    abstract val type: MTypes
//
//
//    /// REG REQUESTS
//
//    @Serializable
//    @SerialName("reg-guest")
//    data class GuestRegister(
//        override val channel: String = "-",
//        override val seq: Int,
//        val name: String
//    ) : Request() {
//        @Transient
//        override val type = MTypes.REQ_REG_GST
//    }
//
//    @Serializable
//    @SerialName("resume")
//    data class Resume(val token: String) : Request() {
//        override val seq: Int
//            get() = TODO("Not yet implemented")
//        override val channel: String
//            get() = TODO("Not yet implemented")
//        override val type: MTypes
//            get() = TODO("Not yet implemented")
//    }
//
//
//    @Serializable
//    @SerialName("reg")
//    data class Register(
//        override val channel: String = "-",
//        override val seq: Int, val username: String, val psw: String
//    ) : Request() {
//        @Transient
//        override val type = MTypes.REQ_REG
//    }
//
//    // **************
//    // LOBBY REQUESTS
//
//    @Serializable
//    @SerialName("join")
//    data class Join(override val channel: String, override val seq: Int) : Request() {
//        @Transient
//        override val type = MTypes.REQ_JOIN
//    }
//
//    @Serializable
//    @SerialName("create")
//    data class Create(override val channel: String = "", override val seq: Int, val settings: Settings) : Request() {
//        @Transient
//        override val type = MTypes.REQ_CREATE
//    }
//
//    // **************
//    // GROUP REQUESTS
//    @Serializable
//    @SerialName("leave")
//    data class Leave(override val channel: String, override val seq: Int) : Request() {
//        @Transient
//        override val type = MTypes.REQ_LEAVE
//    }
//
//    @Serializable
//    @SerialName("chat")
//    data class Chat(override val channel: String, override val seq: Int, val message: String) : Request() {
//        @Transient
//        override val type = MTypes.REQ_CHAT
//    }
//
//    @Serializable
//    @SerialName("init")
//    data class Init(override val channel: String, override val seq: Int) : Request() {
//        @Transient
//        override val type = MTypes.REQ_INIT
//    }
//
//    // *************
//    // GAME REQUESTS
//
//    @Serializable
//    @SerialName("rolldice")
//    data class RollDice(override val channel: String, override val seq: Int) : Request() {
//        @Transient
//        override val type = MTypes.REQ_GAMEACTION
//    }
//
//    @Serializable
//    @SerialName("move_robber")
//    data class MoveRobber(override val channel: String, override val seq: Int, val coordinates: HexCoordinates) :
//        Request() {
//        @Transient
//        override val type = MTypes.REQ_GAMEACTION
//    }
//
//    @Serializable
//    @SerialName("build")
//    data class Build(
//        override val channel: String,
//        override val seq: Int,
//        val buildkind: BuildKind,
//        val coordinates: Coordinates
//    ) : Request() {
//        @Transient
//        override val type = MTypes.REQ_GAMEACTION
//    }
//
//    @Serializable
//    @SerialName("init_build")
//    data class BuildInitSettl(override val channel: String, override val seq: Int, val coordinates: Coordinates) :
//        Request() {
//        @Transient
//        override val type = MTypes.REQ_GAMEACTION
//    }
//
//    @Serializable
//    @SerialName("steal")
//    data class Steal(override val channel: String, override val seq: Int, val playerNumber: Int) : Request() {
//        @Transient
//        override val type = MTypes.REQ_GAMEACTION
//    }
//
//
//    @Serializable
//    @SerialName("init_trade")
//    data class InitTrade(
//        override val channel: String,
//        override val seq: Int,
//        val targetPlayers: Set<Int>,
//        val offer: ResourceMapData,
//        val inReturn: ResourceMapData
//    ) : Request() {
//        @Transient
//        override val type = MTypes.REQ_GAMEACTION
//    }
//
//    @Serializable
//    @SerialName("res_trade")
//    data class RespondTrade(
//        override val channel: String,
//        override val seq: Int,
//        val targetPlayers: Set<Int>,
//        val tradeId: Int,
//        val accept: Boolean
//    ) : Request() {
//        @Transient
//        override val type = MTypes.REQ_GAMEACTION
//    }
//
//
//    @Serializable
//    @SerialName("end_turn")
//    data class EndTurn(override val channel: String, override val seq: Int) : Request() {
//        @Transient
//        override val type = MTypes.REQ_GAMEACTION
//    }
//
//
//    @Serializable
//    @SerialName("claim_vict")
//    data class ClaimVictory(override val channel: String, override val seq: Int) : Request() {
//        @Transient
//        override val type = MTypes.REQ_GAMEACTION
//    }
//}
//
//@Serializable
//enum class ErrorCode {
//    GROUP_FULL,
//    DENIED,
//    FMT,
//    NOT_FOUND,
//    SERVER_ERROR,
//    ALREADY_AUTHENTICATED
//}
//
//@Serializable
//sealed interface Response2 {
//
//    val description: String
//    val seq: Int
//
//    // LOBBY RESPONSES
////
////    @Serializable
////    @SerialName("connected")
////    data class Connected(
////        override val seq: Int,
////        val clientId: String,
////        override val description: String = "Connection success.",
////
////    ) : Response
//
//    @Serializable
//    @SerialName("registered")
//    data class Registered(
//        override val seq: Int,
//        val clientId: String,
//        override val description: String = "Register success.",
//        val resumeToken: String
//    ) : Response2
//
////    @Serializable
////    @SerialName("lobby_update")
////    data class LobbyUpdate(val groupDTO: PublicGroupDTO, override val description: String?) : Response
//
//
//    @Serializable
//    @SerialName("lobby_update")
//    data class GroupUpdate(
//        override val seq: Int,
//        val groupId: String,
//        val memberCount: Int,
//        val capacity: Int,
//    ) : Response2 {
//        override val description: String
//            get() = "TODO: LOBBY UPDATE DESC"
//
//    }
//
//    @Serializable
//    @SerialName("group_created")
//    data class GroupCreated(
//        override val seq: Int,
//        val groupId: String,
//        val settings: Settings,
//        override val description: String
//    ) : Response2
//
//    // GROUP RESPONSES
//
//    /**
//     * TODO: Use PublicUserDTO
//     */
//    @Serializable
//    @SerialName("user_joined")
//    data class UserJoined(
//        override val seq: Int,
//        val groupId: String,
//        val userId: String,
//        val name: String,
//        override val description: String = "User joined group."
//    ) : Response2
//
//    @Serializable
//    @SerialName("joined_ok")
//    data class Joined(
//        val groupId: String,
//        val members: Map<String, String>, // NOTE: Just Client Id and Name for now.
//        val settings: Settings,
//        override val description: String = "You joined the group.",
//        override val seq: Int
//    ) : Response2
//
//    @Serializable
//    @SerialName("user_left")
//    data class Left(override val seq: Int, val groupId: String, override val description: String) : Response2
//
//    /**
//     * NOTE: currently assumes that gameId is the same as the GroupId.
//     */
//    @Serializable
//    @SerialName("game_init")
//    data class Init(
//        override val seq: Int,
//        val privateGameState: GameSnapshotPlayerView,
//        override val description: String
//    ) : Response2
//
//
//    @Serializable
//    @SerialName("game_init_spec")
//    data class InitSpectator(
//        override val seq: Int,
//        val publicGameState: GameSnapshotSpectatorView,
//        override val description: String
//    ) :
//        Response2
//
//
//    @Serializable
//    @SerialName("dice_rolled")
//    data class DiceRolled(
//        override val seq: Int,
//        val gameId: String,
//        val playerNumber: Int,
//        val roll1: Int,
//        val roll2: Int,
//        val resources: ResourceMapData,
//        val othersResources: Map<Int, Int>,
//        val moveRobber: Boolean,
//        override val description: String
//    ) : Response2
//
//    @Serializable
//    @SerialName("new_build")
//    data class Build(
//        override val seq: Int,
//        val gameId: String,
//        val builder: Int,
//        val buildkind: BuildKind,
//        val coordinates: Coordinates,
//        val victoryPoints: Map<Int, Int>,
//        override val description: String
//    ) : Response2
//
//
//    @Serializable
//    @SerialName("init_build_placed")
//    data class InitBuildingPlaced(
//        override val seq: Int,
//        val gameId: String,
//        val builder: Int,
//        val nextPlayer: Int,
//        val buildkind: BuildKind,
//        val coordinates: Coordinates,
//        val victoryPoints: Map<Int, Int>,
//        override val description: String
//    ) : Response2
//
//    @Serializable
//    @SerialName("setup_ended")
//    data class SetupEnded(
//        override val seq: Int,
//        val gameId: String,
//        val builder: Int,
//        val buildkind: BuildKind,
//        val coordinates: Coordinates,
//        val victoryPoints: Map<Int, Int>,
//        val thisPlayer: ResourceMapData,
//        val otherPlayers: Map<Int, Int>,
//        override val description: String
//    ) : Response2
//
//    /**
//     * @property from the user Id of the sender.
//     */
//    @Serializable
//    @SerialName("new_chat")
//    data class Chat(
//        override val seq: Int,
//        val from: String,
//        val message: String,
//        override val description: String
//    ) : Response2
//
//    // GAME RESPONSES
//    // IN COMMON : PlayerNumber
//
//    @Serializable
//    @SerialName("victory_claimed")
//    data class VictoryClaimed(
//        override val seq: Int,
//        val gameId: String,
//        val winner: Int,
//        override val description: String
//    ) : Response2
//
//    @Serializable
//    @SerialName("end_turn")
//    data class EndTurn(
//        override val seq: Int,
//        val gameId: String,
//        override val description: String
//    ) : Response2
//
//    @Serializable
//    @SerialName("robber_moved")
//    data class RobberMoved(
//        override val seq: Int,
//        val gameId: String,
//        val playerNumber: Int,
//        val coordinates: Coordinates,
//        override val description: String
//    ) : Response2
//
//    @Serializable
//    @SerialName("trade_inited")
//    data class InitTrade(
//        override val seq: Int,
//        val gameId: String,
//        val playerNumber: Int,
//        val tradeId: Int,
//        val targetPlayers: Set<Int>,
//        val offer: ResourceMapData,
//        val inReturn: ResourceMapData,
//        override val description: String
//    ) : Response2
//
//    @Serializable
//    @SerialName("trade_response")
//    data class TradeResponse(
//        override val seq: Int,
//        val gameId: String,
//        val playerNumber: Int,
//        val tradeId: Int,
//        val accept: Boolean,
//        override val description: String
//    ) : Response2
//
//
//    // ALL
//
//    @Serializable
//    @SerialName("error")
//    data class Error(override val seq: Int, val code: ErrorCode, override val description: String) : Response2
//
//    /**
//     * Used for simple acknowledgement responses.
//     */
//    @Serializable
//    @SerialName("ok")
//    data class OK(override val seq: Int, val requestId: Int, override val description: String) : Response2
//}


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
