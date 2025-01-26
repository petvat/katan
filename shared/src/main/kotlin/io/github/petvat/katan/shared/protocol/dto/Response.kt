package io.github.petvat.katan.shared.protocol.dto


import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.session.*
import io.github.petvat.katan.shared.protocol.PermissionLevel
import kotlinx.serialization.Serializable

/**
 * This class encapsules all responses that are not related to a specific game of Katan.
 */
@Serializable
sealed class Response() : PayloadDTO {

    @Serializable
    data class Chat(
        val senderId: String,
        val message: String
    ) : Response()

    @Serializable
    data class Create(
        // val groupView: RestrictedGroupView
        val groupId: String,
        val level: PermissionLevel,
        val settings: Settings
    ) : Response()

    @Serializable
    data class Join(
        val groupView: PrivateGroupView? = null,
        val joinedUser: PublicUserView? = null
    ) : Response()

    @Serializable
    data class Login(
        val userInfo: PrivateUserView,
        val token: String
    ) : Response()

    @Serializable
    data class Groups(
        val groups: Collection<PublicGroupView>
    ) : Response()

//    @Serializable
//    data class SessionDTO(
//        val settings: Settings,
//        val hostId: Int,
//        val playersCount: Int,
//        val joinable: Boolean
//    ) : Response()

    @Serializable
    data class Init(
        val privateGameState: PrivateGameState
    ) : Response()

    @Serializable
    data object Empty : Response()

    @Serializable
    data class ConnectionAccept(val sessionId: String) : Response()

}


//
//data class RollDice(
//    val roll1: Int,
//    val roll2: Int,
//    val resources: ResourceMap,
//    val otherPlayersResources: Map<Int, Int>,
//    val moveRobber: Boolean
//) : ActionDTO, Response() {
//    override val actionCode = ActionCode.ROLL_DICE
//}
//
//data class MoveRobber(
//    val newRobberCoordinate: Coordinate,
//    val nextState: Boolean = true // TODO: Not necessary
//    // val stealCardVictim: Int
//) : ActionDTO, Response() {
//    override val actionCode = ActionCode.MOVE_ROBBER
//}
//
//data class StealCard(
//    val stealCardVictim: Int,
//    val privateGameState: PublicGameState // Check diff in client
//) : ActionDTO, Response() {
//    override val actionCode = ActionCode.STEAL_CARD
//}
//
//class Build(
//    val buildKind: BuildKind,
//    val coordinate: Coordinate,
//    val newLongestRoad: Boolean,
//    val longestRoadHolder: Int?
//) : ActionDTO, Response() {
//    override val actionCode = ActionCode.BUILD
//}
//
//class InitiateTrade(
//    val tradeId: Int,
//    val targetPlayers: Set<Int>,
//    val offer: ResourceMap,
//    val request: ResourceMap
//) : ActionDTO, Response() {
//    override val actionCode = ActionCode.INIT_TRADE
//}
//
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
//) : ActionDTO, Response() {
//    override val actionCode = ActionCode.RESPOND_TRADE
//}
//
//class EndTurn(
//    val nextPlayer: Int
//) : ActionDTO, Response() {
//    override val actionCode = ActionCode.TURN_END
//}
//
///**
// * Response when setup has ended only.
// */
//data class SetupEnded(
//    val coordinate: Coordinate, // Temporary fix!
//    val inventory: ResourceMap
//) : ActionDTO, Response() {
//    override val actionCode = ActionCode.SETUP_END
//}
//






