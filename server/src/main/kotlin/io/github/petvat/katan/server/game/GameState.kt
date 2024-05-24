package io.github.petvat.katan.server.game

import io.github.petvat.katan.server.action.*
import io.github.petvat.katan.server.api.KatanAPI
import io.github.petvat.katan.server.dto.*

/**
 * Implementation of State Object pattern. GameProgress has a GameState and a state action will
 * throw IllegalStateException unless the function is implemented for the current state.
 *
 * Consider seal interface.
 */
interface GameState {
    // val StateID: Int

    fun startGame(playerID: Int): Map<Int, ActionResponse> {
        throw IllegalStateException("Game has already started.")
    }

    fun rollDice(playerID: Int): Map<Int, ActionResponse> {
        throw IllegalStateException("Cannot roll dice in the current state.")
    }

    fun moveRobber(request: MoveRobberRequest): Map<Int, ActionResponse> {
        throw IllegalStateException("Cannot move robber in the current state.")
    }

    fun initiateTrade(request: InitiateTradeRequest): Map<Int, ActionResponse> {
        throw IllegalStateException("Cannot initiate trade in the current state.")
    }

    fun respondTrade(request: RespondTradeRequest): Map<Int, ActionResponse> {
        throw IllegalStateException("Cannot respond to trade in the current state.")
    }

    fun build(request: BuildRequest): Map<Int, ActionResponse> {
        throw IllegalStateException("Cannot build in the current state.")
    }

    fun stealCard(request: StealCardRequest): Map<Int, ActionResponse> {
        throw IllegalStateException("Cannot steal card in the current state.")
    }

    fun endTurn(playerID: Int): Map<Int, ActionResponse> {
        throw IllegalStateException("Cannot end turn in the current state.")
    }

//    private fun stateExceptionResponse(playerID: Int, message: String): Map<Int, ActionResponse> {
//        val response: MutableMap<Int, ActionResponse> = mutableMapOf()
//        response[playerID] = ActionResponse(false, message, null)
//        return response
//    }
}

/**
 * Represent a game state.
 * Is abstract class necessary here? Could use default functions in interface. But maybe more readable?
 */
//abstract class AbstractGameState(protected val gameProgress: GameProgress) : GameState {
////    override fun setUp(buildRequest: BuildRequest): Map<Int, ActionResponse> {
////        throw IllegalStateException("Cannot set up in current state.")
////    }
//
//    override fun rollDice(playerID: Int): Map<Int, ActionResponse> {
//        throw IllegalStateException("Cannot roll dice in the current state.")
//    }
//
//    override fun moveRobber(request: MoveRobberRequest): Map<Int, ActionResponse> {
//        throw IllegalStateException("Cannot move robber in the current state.")
//    }
//
//    override fun initiateTrade(request: InitiateTradeRequest): Map<Int, ActionResponse> {
//        throw IllegalStateException("Cannot initiate trade in the current state.")
//    }
//
//    override fun respondTrade(request: RespondTradeRequest): Map<Int, ActionResponse> {
//        throw IllegalStateException("Cannot respond to trade in the current state.")
//    }
//
//    override fun build(request: BuildRequest): Map<Int, ActionResponse> {
//        throw IllegalStateException("Cannot build in the current state.")
//    }
//
//    override fun stealCard(request: StealCardRequest): Map<Int, ActionResponse> {
//        throw IllegalArgumentException("Cannot steal card in the current state.")
//    }
//
//    override fun startGame(playerID: Int): Map<Int, ActionResponse> {
//        throw IllegalStateException("Game has already started.")
//    }
//
//    override fun endTurn(playerID: Int): Map<Int, ActionResponse> {
//        throw IllegalStateException("Cannot end turn in the current state.")
//    }
//}

class StartGameState(val gameProgress: GameProgress /*override val StateID: Int = 0*/) : GameState {

    override fun startGame(playerID: Int): Map<Int, ActionResponse> {

        // TODO: Change state SetUp
        gameProgress.gameState = SetUpState(gameProgress)
        return super.startGame(playerID)
    }

    fun addPlayer(playerID: Int): Map<Int, ActionResponse> {
        TODO("Probably not here, but in other API")
    }

    fun removePlayer(playerID: Int): Map<Int, ActionResponse> {
        TODO()
    }
}

/**
 * State to set up game and place initial settlements.
 *
 * Current implementation: If SETUP state, BuildRequest is interpreted as initial settl. request
 */
class SetUpState(val gameProgress: GameProgress) : GameState {

    private var currentTurn: Int = 0
    private val turnOrder: MutableList<Int> = gameProgress.turnOrder.toMutableList()
    private val turnsLeft: Int

    init {
        val reversed = gameProgress.turnOrder.toMutableList()
        reversed.reversed()
        turnOrder.addAll(reversed)
        turnsLeft = turnOrder.size
        gameProgress.setNextTurn(turnOrder[0])
    }

    /**
     * In SETUP State, BuildRequest in interpreted as initial settl. request
     */
    override fun build(request: BuildRequest): Map<Int, ActionResponse> {
        /*
        * NOTE: 2 POSSIBLE APPROACHES
        *  USE INTERNAL TURNS SETUP:
        *  1. Involves creating a custom PlaceFirstSettlement action, not implementing BuildAction
        *  2. Check turn here
        *  USE EXISTING TURNS
        *  1. Manually set the turn, or check SETUP-phase turnorder
        *  2. May keep other implementation as is
        * */

        val responses = PlaceFirstSettlements(gameProgress, request.playerID, request.coordinate)
            .execute()
        if (responses[request.playerID]?.success == true) { // TODO: FIX NULLABLE
            // successful placing of first settlement
            gameProgress.setNextTurn(turnOrder[currentTurn++])
        }
        if (currentTurn == turnOrder.size) {
            gameProgress.gameState = RollDiceState(gameProgress) // Set-up done
            // TODO: Notify players Setup over
            // Either pass in Arg to PlaceFirstSettlement(Boolean last) or
            // Create new response on change state, on RollDiceState
            // Or append to current actionResponse

            // TODO: Harvest initial resources
            // KatanAPI.performAction() action harvest manual ...
        }
        return responses
    }
}

class RollDiceState(val gameProgress: GameProgress) : GameState {
    override fun rollDice(playerID: Int): Map<Int, ActionResponse> {
        val responses = RollDice(gameProgress, playerID).execute()
        // TODO: Move robber status,
        // TODO: CHANGE Status!
        return responses
    }
}

class StealCardState(val gameProgress: GameProgress) : GameState {
    override fun stealCard(request: StealCardRequest): Map<Int, ActionResponse> {
        val responses = StealCard(gameProgress, request.playerID, request.stealFromPlayerID).execute()
        gameProgress.gameState = BuildAndTradeState(gameProgress)
        return responses
    }
}

class MoveRobberState(val gameProgress: GameProgress) : GameState {
    override fun moveRobber(request: MoveRobberRequest): Map<Int, ActionResponse> {
        val responses = MoveRobber(gameProgress, request.playerID, request.newTileCoordinate).execute()
        gameProgress.gameState = StealCardState(gameProgress)
        return responses
    }
}

class BuildAndTradeState(val gameProgress: GameProgress) : GameState {

    override fun build(request: BuildRequest): Map<Int, ActionResponse> {
        return BuildAction(gameProgress, request.playerID, request.coordinate, request.buildKind).execute()
    }

    override fun initiateTrade(request: InitiateTradeRequest): Map<Int, ActionResponse> {
        return InitiateTrade(
            gameProgress, request.playerID, request.tradeID,
            request.targetPlayersID, request.offer, request.request
        ).execute()
    }

    override fun respondTrade(request: RespondTradeRequest): Map<Int, ActionResponse> {
        return RespondTrade(gameProgress, request.playerID, request.tradeID, request.accept).execute()
    }

    override fun endTurn(playerID: Int): Map<Int, ActionResponse> {
        return EndTurn(gameProgress, playerID).execute()
    }
}
