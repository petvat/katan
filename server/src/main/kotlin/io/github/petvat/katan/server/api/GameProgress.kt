package io.github.petvat.katan.server.api


/**
 * Keep track of all data of a game.
 *
 *
 *
 */
//class GameProgress(
//    val id: String,
//    val settings: Settings,
//    private val _players: List<Player>
//) {
//    private var _gameState: GameState =
//        SetUpState(this)
//    var currentTurn: Turn
//
//    private val _turns = mutableListOf<Turn>()
//    private val _turnOrder = mutableListOf<Int>()
//    private val _activeTrades = mutableListOf<Trade>()
//    private val _unactiveTrades = mutableListOf<Trade>()
//    private var turnIndex = 0
//
//    val boardManager: BoardManager = BoardManager(BoardGenerator.generateBoard(settings))
//
//    val turns = _turns.toList()
//    val players get() = _players.toList()
//    val gameState get() = _gameState
//    val turnOrder: List<Int>
//
//    // Initialize _turnOrder before setting gameState
//    init {
//        _turnOrder.addAll(initializeTurnOrder()) // Assuming initializeTurnOrder() populates this list
//        currentTurn = Turn(id, playerInTurn(), 0)
//        turnOrder = _turnOrder.toList()
//        _gameState = SetUpState(this)
//    }
//
//    /**
//     * The [GameState] can only be changed through calling this function.
//     * This ensures that we always perform valid state transitions.
//     */
//    fun transitionToState(gameState: GameState) {
////        if (stateTransitionMap[_gameState::class]?.contains(gameState::class) == true) {
////            _gameState = gameState
////        } else throw IllegalStateException("")
//        _gameState = gameState // We trust the impl.
//    }
//
//
//    private fun initializeTurnOrder(): List<Int> {
//        return players.map { p -> p.id }.shuffled().toList()
//    }
//
//    /**
//     * Adds a new trade to active trades
//     */
//    fun addTrade(playerNumber: Int, targets: Set<Int>, offer: ResourceMap, inReturn: ResourceMap): Trade {
//        val trade = Trade(
//            -1, // TODO: assign id
//            getPlayer(playerNumber),
//            targets,
//            null,
//            offer,
//            inReturn
//        )
//        _activeTrades.add(trade)
//        return trade
//    }
//
//    // TODO: Depr
//    fun withdrawTrade(tradeId: Int) {
//        val trade = getTradeByID(tradeId)
//        _activeTrades.remove(trade)
//        _unactiveTrades.add(trade)
//    }
//
//    /**
//     * Returns an active trade with TradeID if it exists.
//     */
//    fun getTradeByID(tradeID: Int): Trade {
//        return _activeTrades.find { it.id == tradeID } ?: throw IllegalArgumentException("No such trade exist.")
//    }
//
//    fun playerInTurn(): Int {
//        return _turnOrder[turnIndex]
//    }
//
//    fun setNextTurn(nextPlayer: Int) {
//        turnIndex = nextPlayer
//    }
//
//    /**
//     * Change to next player turn.
//     * Change to RollDiceState.
//     */
//    fun nextTurn(): Int {
//        _gameState = RollDiceState(this)
//
//        return turnOrder[turnIndex++ % turnOrder.size]
//    }
//
//    fun getPlayer(playerID: CID): Player {
//        return players.find { player -> player.id == playerID }
//            ?: throw IllegalArgumentException("No such player exist in this game.")
//    }
//
//
//    fun toPrivate(player: Player): PrivateGameState {
//        return PrivateGameState(
//            gameId = id,
//            player = player,
//            otherPlayers = players.map { it.toPublic() },
//            turnOrder = turnOrder,
//            board = boardManager.board.fromDomain()
//        )
//    }


//}

