package io.github.petvat.katan.server.api

import io.github.petvat.katan.server.action.ActionResponse
import io.github.petvat.katan.server.board.Player
import io.github.petvat.katan.server.dto.*
import io.github.petvat.katan.server.game.GameProgress

/**
 * All game actions go through the KatanAPI for processing.
 * KatanAPI is responsible for translating client requests into game actions and performing them,
 * then prompt the Network handler.
 */
object KatanAPI {

    val ongoingGames: MutableList<GameProgress> = mutableListOf()

    fun performAction(actionRequest: ActionRequest) {

        // ActionFactory.createAction(actionRequest, getGameByID(actionRequest.gameID)).execute()

        var responses: Map<Int, ActionResponse> = mutableMapOf()

        try {
            if (actionRequest is NewGameRequest) {
                responses = createNewGame(actionRequest);
            } else {
                val game = getGameByID(actionRequest.gameID)
                responses = when (actionRequest) {
                    is RollDiceRequest -> game.gameState.rollDice(actionRequest.playerID)
                    is InitiateTradeRequest -> game.gameState.initiateTrade(actionRequest)
                    is RespondTradeRequest -> game.gameState.respondTrade(actionRequest)
                    is BuildRequest -> game.gameState.build(actionRequest)
                    is MoveRobberRequest -> game.gameState.moveRobber(actionRequest)
                    is EndTurnRequest -> game.gameState.endTurn(actionRequest.playerID)
                    else -> throw IllegalArgumentException("Unknown action request.")
                }
            }
        } catch (e: RuntimeException) {
            responses as MutableMap
            responses.clear() // Ensure empty
            responses[actionRequest.playerID] = ActionResponse(
                false, e.message ?: ("Unknown exception" +
                    "occured."), null
            )
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
