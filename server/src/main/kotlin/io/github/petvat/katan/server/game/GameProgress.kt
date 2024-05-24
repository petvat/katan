package io.github.petvat.katan.server.game

import io.github.petvat.katan.server.action.Action
import io.github.petvat.katan.server.board.BoardManager
import io.github.petvat.katan.server.board.Player

/**
 * Keep track of all that data of a game.
 */
class GameProgress(val ID: Int, val gameSettings: GameSettings) {
    var gameState: GameState = StartGameState(this)
    val boardManager = BoardManager()
    private val _players: MutableList<Player> = mutableListOf()
    private val _turns: MutableList<Turn> = mutableListOf()
    private val _turnOrder: MutableList<Int> = mutableListOf()
    private val _activeTrades: MutableList<Trade> = mutableListOf()
    private val _doneTrades: MutableList<Trade> = mutableListOf()
    val currentTurn = Turn(ID, -1, 0)
    val actionQueue = ArrayDeque<Action>()

    val turnOrder: List<Int> get() = _turnOrder.toList()
    val players: List<Player> get() = _players.toList()

    /**
     * Returns an active trade with TradeID if it exists.
     */
    fun getTradeByID(tradeID: Int): Trade {
        return _activeTrades.find { it.tradeID == tradeID } ?: throw IllegalArgumentException("No such trade exist.")
    }

    fun playerInTurn(): Int {
        return currentTurn.playerID
    }

    fun addPlayer(player: Player) {
        _players.add(player)
    }

    fun enqueue(action: Action) {
        actionQueue.addLast(action);
    }

    fun dequeue(): Action {
        return actionQueue.removeFirst()
    }

    fun initializeRandomTurnOrder(): List<Int> {
        if (gameState is SetUpState) {
            TODO("Implement init turnorder")
        } else {
            throw IllegalStateException("Can only initialize turn order in setup state.")
        }
    }

    fun setNextTurn(nextPlayerID: Int) {
        if (gameState is SetUpState) {
            // TODO: change turn
        }
    }

    fun nextTurn(): Int {
        gameState = RollDiceState(this)
        TODO("Player ID next player")
    }

    fun getPlayer(playerID: Int): Player {
        return _players.find { player -> player.ID == playerID }
            ?: throw IllegalArgumentException("No such player exist in this game.")
    }

}
