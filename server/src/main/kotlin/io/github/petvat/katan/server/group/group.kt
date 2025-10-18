package io.github.petvat.katan.server.group

import io.github.petvat.katan.server.api.GameStates
import io.github.petvat.katan.server.service.client.Auth
import io.github.petvat.katan.server.service.client.ClientId
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.shared.User
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.model.SessionId
import io.github.petvat.katan.shared.model.board.BoardGenerator
import io.github.petvat.katan.shared.model.game.*
import io.github.petvat.katan.shared.protocol.dto.GameStateDTO
import io.github.petvat.katan.shared.protocol.dto.fromDomain
import java.util.*

//
//data class ChatMessage(
//    val from: UserId,
//    val message: String,
//    val timestamp: Long = System.currentTimeMillis()
//)
//
///**
// * Represents a group of users/guests/userToPlayerId.
// *
// * TODO: Use UserId instead of ClientId
// *
// */
//interface Group {
//    val host: Auth
//    val members: MutableMap<UserId, Auth> // Should be user
//
//    fun add(client: Auth) {
//        members[client.id] = client
//    }
//
//    fun getMembers() = members.values.toList()
//
//    fun remove(client: UserId) {
//        members.remove(client)
//    }
//}
//
//
//class StandardGroup(
//    override val host: Auth,
//    settings: Settings,
//    clients: List<Auth>
//) : Group {
//    override val members = mutableMapOf<UserId, Auth>()
//    val capacity = settings.maxPlayers
//
//    init {
//        add(host)
//        clients.forEach { if (it !in members.values) add(it) }
//    }
//}
//
///**
// * A Group elevated to a game.
// *
// */
//data class Game(
//    val base: StandardGroup
//) : Group by base {
//    private val _players = mutableListOf<Player>()
//
//    private val activeTrades = mutableListOf<Trade>()
//
//    private val unactiveTrades = mutableListOf<Trade>()
//
//    var turnIndex = 0
//        private set
//
//    private var tradeCounter = 0
//
//    private val turnOrder: List<Int>
//
//    val setupTurnOrder: List<Int>
//
//    var state = GameStates.SETUP
//        private set
//
//    val boardManager = BoardManager(BoardGenerator.generateBoard(settings))
//
//    val players get() = _players.toList()
//
//    var currentTurn: Turn
//        private set
//
//    // Initialize _turnOrder before setting gameState
//    init {
//
//        members.values.forEachIndexed { index, groupMember ->
//            _players.add(
//                Player(groupMember.id.value, index, PlayerColor.entries[index])
//            )
//        }
//        turnOrder = initializeTurnOrder()
//        currentTurn = Turn(id.value, playerInTurn(), turnIndex)
//        setupTurnOrder = turnOrder + turnOrder.reversed()
//    }
//
//    /**
//     * Ehh. Maybe not have Game inherit Group.
//     */
//    override fun add(client: ConnectedClient): Boolean {
//        throw UnsupportedOperationException("Cannot add new client when game in progress.")
//    }
//
//    /**
//     * The [GameState] can only be changed through calling this function.
//     * This ensures that we always perform valid state transitions.
//     */
//    fun transitionToState(state: GameStates) {
//        this.state = state
//    }
//
//    private fun initializeTurnOrder(): List<Int> {
//        return players
//            .map { p -> p.playerNumber }
//            .shuffled().toList()
//    }
//
//    /**
//     * Adds a new trade to active trades
//     */
//    fun addTrade(playerNumber: Int, targets: Set<Int>, offer: ResourceMap, inReturn: ResourceMap): Trade {
//        val trade = Trade(
//            tradeCounter++, // TODO: assign id
//            getPlayer(playerNumber)!!,
//            targets,
//            null,
//            offer,
//            inReturn
//        )
//        activeTrades += trade
//        return trade
//    }
//
//    fun withdrawTrade(tradeId: Int) {
//        val trade = getTradeByID(tradeId)
//        activeTrades -= trade
//        unactiveTrades += trade
//    }
//
//    /**
//     * Returns an active trade with TradeID if it exists.
//     */
//    fun getTradeByID(tradeID: Int): Trade {
//        return activeTrades.find { it.id == tradeID } ?: throw IllegalArgumentException("No such trade exist.")
//    }
//
//    fun playerInTurn(): Int {
//        return turnOrder[turnIndex]
//    }
//
//    /**
//     * Change to next player turn.
//     */
//    fun nextTurn(): Int {
//        if (state == GameStates.SETUP) {
//            return setupTurnOrder[turnIndex++ % setupTurnOrder.size]
//        }
//
//        unactiveTrades += activeTrades // TODO: Check if breaks.
//        activeTrades.clear()
//
//        return turnOrder[turnIndex++ % turnOrder.size]
//    }
//
//    fun viewGame(sessionId: SessionId): GameStateDTO {
//        return GameStateDTO(
//            player = players.find { it.id == sessionId.value }!!.fromDomain(),
//            otherPlayers = players.map { it.fromDomain() },
//            turnOrder = turnOrder,
//            turnPlayer = playerInTurn(),
//            board = boardManager.board.fromDomain()
//        )
//    }
//
//    fun getPlayer(playerNumber: Int): Player? {
//        return _players.find { it.playerNumber == playerNumber }
//    }
//
//    fun getPlayerNumber(sessionId: SessionId): Int {
//        return _players.find { it.id == sessionId.value }?.playerNumber
//            ?: throw IllegalArgumentException("No player with this ID in game.")
//    }
//
//    fun getSessionId(playerNumber: Int): SessionId {
//        return SessionId(players.find { it.playerNumber == playerNumber }!!.id)
//    }
//}

