package io.github.petvat.katan.server.group

import io.github.petvat.katan.server.api.GameState
import io.github.petvat.katan.server.api.RollDiceState
import io.github.petvat.katan.server.api.SetUpState
import io.github.petvat.katan.shared.protocol.PermissionLevel
import io.github.petvat.katan.shared.protocol.SessionId
import io.github.petvat.katan.shared.model.board.BoardGenerator
import io.github.petvat.katan.shared.model.board.AuthBoardManager
import io.github.petvat.katan.shared.model.board.Player
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.game.Trade
import io.github.petvat.katan.shared.model.game.Turn
import io.github.petvat.katan.shared.model.session.PrivateGameState
import io.github.petvat.katan.shared.protocol.dto.PrivateGroupView
import kotlinx.coroutines.sync.Mutex


@JvmInline
value class GroupId(val value: String)


/**
 * Represents a group member, an interface for the [Group].
 */
// interface GroupMember<T>
data class GroupMember(
    val id: SessionId, // String or something
    val name: String
)


/**
 * Represents a group of users/guests/players.
 */
interface Group {
    val id: GroupId
    val clients: MutableMap<SessionId, GroupMember>
    val host: SessionId
    val mutex: Mutex
    val level: PermissionLevel
    val chatLog: MutableMap<String, String>
    val settings: Settings

    suspend fun add(client: GroupMember) {
        clients[client.id] = client
    }

    suspend fun remove(client: GroupMember) {
        clients.remove(client.id)
    }

    suspend fun view(excluding: SessionId): PrivateGroupView
}

data class UserGroup(
    override val id: GroupId,
    override val clients: MutableMap<SessionId, GroupMember>, // TODO: Make user
    override val host: SessionId,
    override val level: PermissionLevel = PermissionLevel.USER,
    override val settings: Settings
) : Group {
    override val mutex = Mutex()
    override val chatLog = mutableMapOf<String, String>()
    override suspend fun view(excluding: SessionId): PrivateGroupView {
        return PrivateGroupView(
            id.value,
            clients.filter { (s, _) -> s != excluding }.map { (sid, mem) -> sid.value to mem.name }.toMap()
                .toMutableMap(),
            level,
            chatLog,
            settings
        )
    }
}

data class GuestGroup(
    override val id: GroupId,
    override val clients: MutableMap<SessionId, GroupMember>, // TODO: Make guest
    override val host: SessionId,
    override val level: PermissionLevel = PermissionLevel.GUEST,
    override val settings: Settings
) : Group {
    override val mutex = Mutex()

    override val chatLog = mutableMapOf<String, String>()

    override suspend fun view(excluding: SessionId): PrivateGroupView {
        return PrivateGroupView(
            id.value,
            clients.filter { (s, _) -> s != excluding }.map { (sid, mem) -> sid.value to mem.name }.toMap()
                .toMutableMap(),
            level,
            chatLog,
            settings
        )
    }
}


/**
 * A Group elevated to a game.
 */
data class Game(
    val base: Group, // FIX: readonly!
) : Group by base {
    private val _players = mutableListOf<Player>()
    var gameState: GameState =
        SetUpState(this)

    private val _turns = mutableListOf<Turn>()
    private val _turnOrder = mutableListOf<Int>()
    private val _activeTrades = mutableListOf<Trade>()
    private val _unactiveTrades = mutableListOf<Trade>()
    var turnIndex = 0
    private var _tradeCounter = 0

    val boardManager: AuthBoardManager = AuthBoardManager(BoardGenerator.generateBoard(base.settings))

    val turns get() = _turns.toList()
    val players get() = _players.toList()
    val tradeCounter get() = _tradeCounter

    val turnOrder: List<Int>
    var currentTurn: Turn
    var setupTurnOrder: MutableList<Int>

    // Initialize _turnOrder before setting gameState
    init {
        clients.values.forEachIndexed { index, groupMember ->
            _players.add(Player(groupMember.id.value, index, base.settings))
        }
        _turnOrder.addAll(initializeTurnOrder())
        turnIndex = 0
        currentTurn = Turn(id.value, playerInTurn(), turnIndex)
        turnOrder = _turnOrder.toList()
        gameState = SetUpState(this)
        val reversed = turnOrder.toMutableList().reversed()
        setupTurnOrder = turnOrder.toMutableList()
        setupTurnOrder.addAll(reversed)
    }

    /**
     * The [GameState] can only be changed through calling this function.
     * This ensures that we always perform valid state transitions.
     */
    fun transitionToState(gameState: GameState) {
        this.gameState = gameState // We trust the impl.
    }


    private fun initializeTurnOrder(): List<Int> {
        return players.map { p -> p.playerNumber }.shuffled().toList()
    }

    /**
     * Adds a new trade to active trades
     */
    fun addTrade(playerNumber: Int, targets: Set<Int>, offer: ResourceMap, inReturn: ResourceMap): Trade {
        val trade = Trade(
            _tradeCounter++, // TODO: assign id
            getPlayer(playerNumber)!!,
            targets,
            null,
            offer,
            inReturn
        )
        _activeTrades.add(trade)
        return trade
    }

    // TODO: Depr
    fun withdrawTrade(tradeId: Int) {
        val trade = getTradeByID(tradeId)
        _activeTrades.remove(trade)
        _unactiveTrades.add(trade)
    }

    /**
     * Returns an active trade with TradeID if it exists.
     */
    fun getTradeByID(tradeID: Int): Trade {
        return _activeTrades.find { it.id == tradeID } ?: throw IllegalArgumentException("No such trade exist.")
    }

    fun playerInTurn(): Int {
        return _turnOrder[turnIndex]
    }

    fun setNextTurn(nextPlayer: Int) {
        turnIndex = nextPlayer
    }

    /**
     * Change to next player turn.
     * Change to RollDiceState.
     */
    fun nextTurn(): Int {
        gameState = RollDiceState(this)

        return turnOrder[turnIndex++ % turnOrder.size]
    }

    fun viewGame(sessionId: SessionId): PrivateGameState {
        return PrivateGameState(
            player = getPlayer(getPlayerId(sessionId))!!,
            otherPlayers = players.map { it.toPublic() },
            turnOrder = turnOrder,
            board = boardManager.board.fromDomain()
        )
    }


    fun getPlayer(playerNumber: Int): Player? {
        return _players.find { it.playerNumber == playerNumber }
    }

    fun getPlayerId(sessionId: SessionId): Int {
        return _players.find { it.id == sessionId.value }?.playerNumber
            ?: throw IllegalArgumentException("No player with this ID in game.")
    }

    fun getSessionId(playerNumber: Int): SessionId {
        return SessionId(players.find { it.playerNumber == playerNumber }!!.id)
    }


}



