package io.github.petvat.katan.server.api

import io.github.petvat.katan.server.action.ActionResponse
import io.github.petvat.katan.server.action.InitiateTrade
import io.github.petvat.katan.server.action.MoveRobber
import io.github.petvat.katan.server.board.Player
import io.github.petvat.katan.server.dto.*
import io.github.petvat.katan.server.game.GameProgress

object KatanAPI {

    val ongoingGames: MutableList<GameProgress> = mutableListOf()

    fun performAction(actionRequest: ActionRequest) {

        // ActionFactory.createAction(actionRequest, getGameByID(actionRequest.gameID)).execute()
        val game = getGameByID(actionRequest.gameID)
        val responses = when (actionRequest) {
            is NewGameRequest -> createNewGame(actionRequest)
            is RollDiceRequest -> game.gameState.rollDice(actionRequest.playerID)
            is InitiateTradeRequest -> game.gameState.initiateTrade(actionRequest)
            is RespondTradeRequest -> game.gameState.respondTrade(actionRequest)
            is BuildRequest -> game.gameState.build(actionRequest)
            is MoveRobberRequest -> game.gameState.moveRobber(actionRequest)
            else -> throw IllegalArgumentException("Unknown action request.")
        }

        // TODO: response to network OUT
    }

    private fun createNewGame(newGameRequest: NewGameRequest): Map<Int, ActionResponse> {
        // TODO: find free game ID
        val newGame = GameProgress(1, newGameRequest.gameSettings)
        // GameState = CREATED?
        // TODO: Data base fetch or other to get player
        newGame.addPlayer(Player(0, "PLACEHOLDER", 0))
        ongoingGames.add(newGame)
        // Notify players
        return mutableMapOf() // TODO: Create responses
    }

    fun getGameByID(ID: Int): GameProgress {
        return ongoingGames.find { g -> g.ID == ID } ?: throw IllegalArgumentException("No such game exist.")
    }
}
