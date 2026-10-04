package io.github.petvat.katan.shared.protocol

import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Trade
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.dto.ParticipantGameSnapshot
import io.github.petvat.katan.shared.protocol.dto.SpectatorGameSnapshot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ErrorCode {
    UNTRACED_ERR,
    GROUP_FULL,
    DENIED,
    FMT,
    NOT_FOUND,
    SERVER_ERROR,
    ALREADY_AUTHENTICATED,
    UNAUTHENTICATED,
    UNKNOWN_COMMAND,
    COULD_NOT_CONNECT
}

/**
 * Outbound response payloads. Like [Request], these carry only
 * action-specific data.
 */
@Serializable
sealed interface Response {

    // ---- Auth responses ----

    /**
     * TODO: ClientData object. For now: name
     */
    @Serializable
    @SerialName("registered")
    data class Registered(val clientId: String, val name: String, val lobby: String, val resumeToken: String) : Response

    @Serializable
    @SerialName("resumed")
    /**
     * Client must request resync
     */
    data class Resumed(val resumeToken: String, val gameState: String? = null) : Response

    @Serializable
    @SerialName("resume_failed")
    data class ResumeFailed(val reason: String) : Response

    // ---- Lobby responses ----

    @Serializable
    @SerialName("lobby_update")
    data class GroupUpdate(val groupId: String, val memberCount: Int, val capacity: Int) : Response

    @Serializable
    @SerialName("group_created")
    data class GroupCreated(val groupId: String, val chatId: String?, val settings: Settings) : Response

    /** TODO: Use UserData instead of raw name/id once that type exists. */
    @Serializable
    @SerialName("user_joined")
    data class UserJoined(val groupId: String, val userId: String, val name: String) : Response

    @Serializable
    @SerialName("joined_ok")
    data class Joined(
        val groupId: String,
        val members: Map<String, String>, // clientId -> name, for now
        val settings: Settings
    ) : Response


    @Serializable
    @SerialName("left_ok")
    data object LeftOk : Response

    @Serializable
    @SerialName("user_left")
    data class Left(val groupId: String, val userId: String) : Response


    /** NOTE: currently assumes gameId == groupId. */
    @Serializable
    @SerialName("game_init")
    data class Init(val privateGameState: ParticipantGameSnapshot) : Response

    @Serializable
    @SerialName("game_init_spec")
    data class InitSpectator(val publicGameState: SpectatorGameSnapshot) : Response

    @Serializable
    @SerialName("dice_rolled")
    data class DiceRolled(
        val gameId: String,
        val playerNumber: Int,
        val roll1: Int,
        val roll2: Int,
        val resources: ResourceMap,
        val othersResources: Map<Int, Int>,
        val moveRobber: Boolean
    ) : Response

    @Serializable
    @SerialName("new_build")
    data class Build(
        val gameId: String,
        val builder: Int,
        val buildkind: BuildKind,
        val coordinates: Coordinates,
        val victoryPoints: Map<Int, Int>
    ) : Response

    @Serializable
    @SerialName("init_build_placed")
    data class InitBuildingPlaced(
        val gameId: String,
        val builder: Int,
        val nextPlayer: Int,
        val buildkind: BuildKind,
        val coordinates: Coordinates,
        val victoryPoints: Map<Int, Int>
    ) : Response

    @Serializable
    @SerialName("setup_ended")
    data class SetupEnded(
        val gameId: String,
        val builder: Int,
        val buildkind: BuildKind,
        val coordinates: Coordinates,
        val victoryPoints: Map<Int, Int>,
        val thisPlayer: ResourceMap,
        val otherPlayers: Map<Int, Int>
    ) : Response

    /** @property from userId of the sender. */
    @Serializable
    @SerialName("new_chat")
    data class Chat(val from: String, val message: String) : Response

    @Serializable
    @SerialName("chat_resync")
    data class ChatResync(val history: List<Pair<String, String>>) : Response

    // ---- Game responses ----


    @Serializable
    @SerialName("game_resync")
    data class GameResync(val privateGameState: ParticipantGameSnapshot) : Response

    @Serializable
    @SerialName("victory_claimed")
    data class VictoryClaimed(val gameId: String, val winner: Int) : Response

    @Serializable
    @SerialName("end_turn")
    data class EndTurn(val gameId: String) : Response

    @Serializable
    @SerialName("trade_executed")
    data class TradeExecuted(
        val gameId: String,
        val tradeId: Int,
        val acceptor: Int,
        val resources: ResourceMap,          // recipient's own hand (personalized)
        val othersResources: Map<Int, Int>   // public counts of the affected pair
    ) : Response

    @Serializable
    @SerialName("trade_declined")
    data class TradeDeclined(
        val gameId: String,
        val tradeId: Int,
        val declinedBy: Int,
    ) : Response

    @Serializable
    @SerialName("robber_moved")
    data class RobberMoved(val gameId: String, val playerNumber: Int, val coordinates: Coordinates) : Response

    @Serializable
    @SerialName("trade_inited")
    data class InitTrade(
        val gameId: String,
        val trade: Trade
    ) : Response

//


    // ---- Universal ----

    @Serializable
    @SerialName("error")
    data class Error(val code: ErrorCode, val detail: String) : Response

    /** Simple acknowledgement with no other payload. */
    @Serializable
    @SerialName("ok")
    data object OK : Response
}

/**
 * Every outbound message is wrapped in this.
 *
 * @property replyTo Echoes the seq of the request this is a direct reply
 *   to, for the ORIGINATING client only. Null for every other recipient
 *   of a broadcast event, and null for server-initiated pushes that
 *   weren't triggered by a request at all.
 * @property channelSeq This event's position in the channel's history.
 *   Null for failures (an Error never became part of channel history,
 *   so it doesn't consume a sequence number) and for pre-auth responses
 * @property payload The message content.
 */
@Serializable
data class OutMessage(
    val replyTo: Int? = null,
    val channelSeq: Int? = null,
    val payload: Response
)
