package io.github.petvat.katan.shared.protocol

import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.HexCoord
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.ResourceMapData
import io.github.petvat.katan.shared.model.game.Settings
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Inbound request payloads. These carry ONLY action-specific data.
 */
@Serializable
sealed interface Request {

    // ---- Pre-auth requests (no channel exists yet for these) ----

    @Serializable
    @SerialName("reg_guest")
    data class GuestRegister(val name: String) : Request

    @Serializable
    @SerialName("reg")
    data class Register(val username: String, val psw: String) : Request

    @Serializable
    @SerialName("resume")
    data class Resume(val token: String) : Request

    // ---- Lobby requests ----

    @Serializable
    @SerialName("join")
    data class Join(val channel: String) : Request

    @Serializable
    @SerialName("create")
    data class Create(val settings: Settings) : Request

    // ---- Group requests ----

    @Serializable
    @SerialName("leave")
    data object Leave : Request

    @Serializable
    @SerialName("chat")
    data class Chat(val message: String) : Request

    @Serializable
    @SerialName("init")
    data object Init : Request

    // ---- Game requests ----

    @Serializable
    @SerialName("roll_dice")
    data object RollDice : Request

    @Serializable
    @SerialName("move_robber")
    data class MoveRobber(val coordinates: HexCoord) : Request

    @Serializable
    @SerialName("build")
    data class Build(val buildkind: BuildKind, val coordinates: Coordinates) : Request

    @Serializable
    @SerialName("init_build")
    data class BuildInitSettl(val coordinates: Coordinates) : Request

    @Serializable
    @SerialName("steal")
    data class Steal(val playerNumber: Int) : Request

    @Serializable
    @SerialName("init_trade")
    data class InitTrade(
        val targetPlayers: Set<Int>,
        val offer: ResourceMap,
        val inReturn: ResourceMap
    ) : Request

    @Serializable
    @SerialName("res_trade")
    data class RespondTrade(
        val tradeId: Int,
        val accept: Boolean
    ) : Request

    @Serializable
    @SerialName("end_turn")
    data object EndTurn : Request

    @Serializable
    @SerialName("claim_vict")
    data object ClaimVictory : Request
}

/**
 * The envelope every inbound message is wrapped in.
 *
 * `seq` is assigned by the CLIENT, purely as a correlation id. Echoed back via [OutMessage.replyTo].
 */
@Serializable
data class InMessage(
    val seq: Int,
    val channel: String?, // Default to "lobby:main"
    val payload: Request
)
