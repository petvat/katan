package io.github.petvat.katan.server.group

import io.github.petvat.katan.server.api.GameStates
import io.github.petvat.katan.shared.model.PermissionLevel
import io.github.petvat.katan.shared.model.SessionId
import io.github.petvat.katan.shared.model.board.BoardGenerator
import io.github.petvat.katan.shared.model.board.BoardManager
import io.github.petvat.katan.shared.model.board.Player
import io.github.petvat.katan.shared.model.game.*
import io.github.petvat.katan.shared.protocol.dto.GameStateDTO
import io.github.petvat.katan.shared.protocol.dto.fromDomain
import kotlinx.coroutines.sync.Mutex


@JvmInline
value class GroupId(val value: String)


/**
 * Represents a group member, an interface for the [Group].
 *
 * TODO: WEIRD!
 */
data class GroupMember(
    val id: SessionId, // String or something
    val name: String
)

// DON'T LIKE THIS!

/**
 * Represents a group of users/guests/players.
 */
interface Group {
    val id: GroupId
    val clients: MutableMap<SessionId, GroupMember>
    val host: SessionId
    val mutex: Mutex
    val level: PermissionLevel
    val chatLog: MutableList<Pair<String, String>>
    val settings: Settings

    suspend fun add(client: GroupMember) {
        clients[client.id] = client
    }

    suspend fun remove(clientId: SessionId) {
        clients.remove(clientId)
    }

    fun isFull() = clients.keys.size >= settings.maxPlayers
}

data class UserGroup(
    override val id: GroupId,
    override val clients: MutableMap<SessionId, GroupMember>, // TODO: Make user
    override val host: SessionId,
    override val level: PermissionLevel = PermissionLevel.USER,
    override val settings: Settings
) : Group {
    override val mutex = Mutex()
    override val chatLog = mutableListOf<Pair<String, String>>()
}

data class GuestGroup(
    override val id: GroupId,
    override val clients: MutableMap<SessionId, GroupMember>, // TODO: Make guest
    override val host: SessionId,
    override val level: PermissionLevel = PermissionLevel.GUEST,
    override val settings: Settings
) : Group {
    override val mutex = Mutex()

    override val chatLog = mutableListOf<Pair<String, String>>()
}


/**
 * A Group elevated to a game.
 *
 * Should be different from game!
 */
data class Game(
    val base: Group, // FIX: readonly!
) : Group by base {
    private val _players = mutableListOf<Player>()

    var state = GameStates.SETUP

    private val _turns = mutableListOf<Turn>()

    private val _turnOrder = mutableListOf<Int>()

    private val _activeTrades = mutableListOf<Trade>()

    private val _unactiveTrades = mutableListOf<Trade>()

    var turnIndex = 0
        private set

    private var _tradeCounter = 0

    val boardManager: BoardManager = BoardManager(BoardGenerator.generateBoard(base.settings))

    val turns get() = _turns.toList()

    val players get() = _players.toList()

    val tradeCounter get() = _tradeCounter

    val turnOrder: List<Int>

    var currentTurn: Turn

    var setupTurnOrder: MutableList<Int>

    // Initialize _turnOrder before setting gameState
    init {


        clients.values.forEachIndexed { index, groupMember ->
            _players.add(
                Player(groupMember.id.value, index, PlayerColor.entries[index])
            )
        }
        _turnOrder.addAll(initializeTurnOrder())
        currentTurn = Turn(id.value, playerInTurn(), turnIndex)
        turnOrder = _turnOrder.toList()
        val reversed = turnOrder.toMutableList().reversed()
        setupTurnOrder = turnOrder.toMutableList()
        setupTurnOrder.addAll(reversed)
    }

    /**
     * The [GameState] can only be changed through calling this function.
     * This ensures that we always perform valid state transitions.
     */
    fun transitionToState(state: GameStates) {
        this.state = state // We trust the impl.
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

    /**
     * Change to next player turn.
     */
    fun nextTurn(): Int {
        if (state == GameStates.SETUP) {
            return setupTurnOrder[turnIndex++ % setupTurnOrder.size]
        }
        return turnOrder[turnIndex++ % turnOrder.size]
    }

    fun viewGame(sessionId: SessionId): GameStateDTO {
        return GameStateDTO(
            player = players.find { it.id == sessionId.value }!!.fromDomain(),
            otherPlayers = players.map { it.fromDomain() },
            turnOrder = turnOrder,
            turnPlayer = playerInTurn(),
            board = boardManager.board.fromDomain()
        )
    }

    fun getPlayer(playerNumber: Int): Player? {
        return _players.find { it.playerNumber == playerNumber }
    }

    fun getPlayerNumber(sessionId: SessionId): Int {
        return _players.find { it.id == sessionId.value }?.playerNumber
            ?: throw IllegalArgumentException("No player with this ID in game.")
    }

    fun getSessionId(playerNumber: Int): SessionId {
        return SessionId(players.find { it.playerNumber == playerNumber }!!.id)
    }


}

fun main() {
    val group = UserGroup(
        GroupId(""),
        mutableMapOf(),
        SessionId(""),
        PermissionLevel.USER,
        Settings()
    )
    val game = Game(group)
}



