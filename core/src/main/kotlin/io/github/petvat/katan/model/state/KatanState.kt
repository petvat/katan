package io.github.petvat.katan.model.state

import io.github.petvat.katan.shared.hexlib.HexCoord
import io.github.petvat.katan.shared.model.game.GameMode
import io.github.petvat.katan.shared.model.game.Phase
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.Response


data class AuthState(
    val clientId: String? = null,
    val name: String? = null,
    val resumeToken: String? = null,
    val lobbyChannel: String? = null
) {
    val isRegistered: Boolean get() = clientId != null
}

/** Lobby listing, delta-updated from GroupUpdate broadcasts. */
data class GroupExternal(
    val id: String,
    val memberCount: Int,
    val capacity: Int,
    val mode: GameMode? = null
)

data class GroupSession(
    val id: String,
    val members: Map<String, String>, // clientId -> name
    val settings: Settings,
    val chatId: String? = null
)

data class ChatSession(
    val id: String,
    val log: List<Pair<String, String>> = emptyList() // (fromId, text)
)


/**
 * ALL client state lives here.
 *
 * THREADING CONTRACT: every mutation happens on the inbound pump thread
 * (via InboundRouter).
 */
class ClientState {
    @Volatile
    var auth: AuthState = AuthState(); private set

    @Volatile
    var lobby: Map<String, GroupExternal> = emptyMap(); private set

    @Volatile
    var group: GroupSession? = null; private set

    @Volatile
    var chat: ChatSession? = null; private set

    @Volatile
    var game: GameSession? = null; private set

    fun onRegistered(r: Response.Registered) {
        auth = AuthState(r.clientId, r.name, r.resumeToken, lobbyChannel = r.lobby)
    }

    fun onResumed(r: Response.Resumed) {
        auth = auth.copy(resumeToken = r.resumeToken)
    }

    fun onGroupUpdate(r: Response.GroupUpdate) {
        lobby = lobby + (r.groupId to GroupExternal(r.groupId, r.memberCount, r.capacity))
    }

    fun onGroupCreated(r: Response.GroupCreated) {
        val self = auth.clientId?.let { it to (auth.name ?: "") }
        group = GroupSession(
            id = r.groupId,
            members = if (self != null) mapOf(self) else emptyMap(),
            settings = r.settings,
            chatId = r.chatId
        )
        chat = r.chatId?.let { ChatSession(it) }
    }

    fun onJoined(r: Response.Joined) {
        group = GroupSession(r.groupId, r.members, r.settings, chat?.takeIf { it.id == r.groupId }?.id)
    }

    fun onUserJoined(r: Response.UserJoined) {
        group = group
            ?.takeIf { it.id == r.groupId }
            ?.let { it.copy(members = it.members + (r.userId to r.name)) }
    }

    fun onUserLeft(r: Response.Left) {
        group = group
            ?.takeIf { it.id == r.groupId }
            ?.let { it.copy(members = it.members - r.userId) }
    }

    fun onLeft() {
        group = null
        chat = null
        game = null
    }

    fun onChat(r: Response.Chat) {
        chat = chat?.copy(log = chat!!.log + (r.from to r.message))
    }

    fun onChatResync(r: Response.ChatResync) {
        chat = chat?.copy(log = r.history)
    }

    fun onGameInit(game: GameSession) {
        this.game = game
    }

    fun onDiceRolled(r: Response.DiceRolled) {
        game = game?.rolledDice(r.resources, r.othersResources, r.moveRobber)
    }

    fun onBuild(r: Response.Build) {
        game = game?.addBuilding(r.builder, r.buildkind, r.coordinates, r.victoryPoints)
    }

    fun onInitBuildPlaced(r: Response.InitBuildingPlaced) {
        game = game?.addBuilding(r.builder, r.buildkind, r.coordinates, r.victoryPoints)
    }

    fun onSetupEnded(r: Response.SetupEnded) {
        game = game?.addBuilding(r.builder, r.buildkind, r.coordinates, r.victoryPoints)
        game = game?.copy(phase = Phase.SETUP)
    }

    fun onEndTurn(r: Response.EndTurn) {
        game = game?.advanceTurn()
    }

    fun onRobberMoved(r: Response.RobberMoved) {
        game = game?.copy(board = game!!.board.copy(robberLocation = r.coordinates as HexCoord))
    }

    fun onVictoryClaimed(r: Response.VictoryClaimed) {
        game = game?.copy(
            winner = r.winner
        )
    }

    fun onTradeInited(r: Response.InitTrade) {
        game = game?.copy(
            onGoingTrades = game!!.onGoingTrades + r.trade
        )
    }

    fun onTradeExecuted(r: Response.TradeExecuted) {
        game = game?.copy(
            resources = r.resources,
            otherResources = r.othersResources,
            onGoingTrades = game!!.onGoingTrades - game!!.onGoingTrades.single { it.id == r.tradeId }
        )
    }

    fun onTradeDeclined(r: Response.TradeDeclined) {
        game = game?.tradeDeclined(r.tradeId, r.declinedBy)
    }

    fun reset() {
        auth = AuthState()
        lobby = emptyMap()
        group = null
        chat = null
        game = null
    }
}
