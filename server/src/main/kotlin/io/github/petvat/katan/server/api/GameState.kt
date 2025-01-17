package io.github.petvat.katan.server.api

import io.github.petvat.katan.server.action.*
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.protocol.Payload
import io.github.petvat.katan.shared.protocol.dto.ActionRequest
import io.github.petvat.katan.shared.protocol.dto.ActionResponse


/**
 * Implementation of State Object pattern (is it?). GameProgress has a GameState and a state action will
 * throw IllegalStateException unless the function is implemented for the current state.
 *
 */
interface GameState {

    fun placeInitialSettlements(
        actorId: Int,
        request: ActionRequest.Build
    ): ExecutionResult<ActionResponse.PlaceInitSettlment> {
        return ExecutionResult.Failure("Cannot place initial settlement in current state.")
    }

    fun rollDice(actorId: Int): ExecutionResult<ActionResponse.RollDice> {
        return ExecutionResult.Failure("Cannot roll dice in the current state.")
    }

    fun moveRobber(actorId: Int, request: ActionRequest.MoveRobber): ExecutionResult<ActionResponse.MoveRobber> {
        return ExecutionResult.Failure("Cannot roll dice in the current state.")
    }

    fun initiateTrade(
        actorId: Int,
        request: ActionRequest.InitiateTrade
    ): ExecutionResult<ActionResponse.InitiateTrade> {
        return ExecutionResult.Failure("Cannot roll dice in the current state.")
    }

    fun respondTrade(
        actorId: Int,
        request: ActionRequest.RespondTrade
    ): ExecutionResult<ActionResponse.RespondTrade> {
        return ExecutionResult.Failure("Cannot roll dice in the current state.")
    }

    fun build(actorId: Int, request: ActionRequest.Build): ExecutionResult<ActionResponse.Build> {
        return ExecutionResult.Failure("Cannot roll dice in the current state.")
    }

    fun stealCard(
        actorId: Int,
        request: ActionRequest.StealCard
    ): ExecutionResult<ActionResponse.StealCard> {
        return ExecutionResult.Failure("Cannot steal in the current state.")
    }

    fun endTurn(actorId: Int): ExecutionResult<ActionResponse.EndTurn> {
        return ExecutionResult.Failure("Cannot end turn in the current state.")
    }

    fun claimVictory(actorId: Int): ExecutionResult<ActionResponse.VictoryClaimed> {
        return ExecutionResult.Failure("Cannot roll dice in the current state.")
    }
}

/**
 * State to set up game and place initial settlements.
 *
 * Current implementation: If SETUP state, BuildRequest is interpreted as initial settl. request
 */
class SetUpState(val game: Game) :
    GameState {

//    private var currentTurn: Int = 0
//    private val setupTurnOrder: MutableList<Int> = game.turnOrder.toMutableList()
//
//    init {
//        val reversed = game.turnOrder.toMutableList().reversed()
//        setupTurnOrder.addAll(reversed)
//        game.setNextTurn(setupTurnOrder[currentTurn])
//    }

    override fun placeInitialSettlements(
        actorId: Int,
        request: ActionRequest.Build
    ): ExecutionResult<ActionResponse.PlaceInitSettlment> {
        return PlaceFirstSettlements(game, actorId, request.coordinate).execute()
    }
}

class RollDiceState(val game: Game) :
    GameState {
    override fun rollDice(actorId: Int): ExecutionResult<ActionResponse.RollDice> {
        val responses = RollDice(game, actorId).execute()
        return responses
    }
}

class StealCardState(val game: Game) :
    GameState {
    override fun stealCard(
        actorId: Int,
        request: ActionRequest.StealCard
    ): ExecutionResult<ActionResponse.StealCard> {
        val responses = StealCard(game, actorId, request.stealFromPlayerID).execute()
        return responses
    }
}

class MoveRobberState(val game: Game) :
    GameState {
    override fun moveRobber(
        actorId: Int,
        request: ActionRequest.MoveRobber
    ): ExecutionResult<ActionResponse.MoveRobber> {
        val responses = MoveRobber(game, actorId, request.newTileCoordinate as HexCoordinates).execute()
        return responses
    }
}

class BuildAndTradeState(val game: Game) :
    GameState {

    override fun build(actorId: Int, request: ActionRequest.Build): ExecutionResult<ActionResponse.Build> {
        return BuildAction(game, actorId, request.coordinate, request.buildKind).execute()
    }

    override fun initiateTrade(
        actorId: Int,
        request: ActionRequest.InitiateTrade
    ): ExecutionResult<ActionResponse.InitiateTrade> {
        return InitiateTrade(
            game,
            actorId,
            request.targetPlayersID,
            request.tradeOffer,
            request.tradeInReturn
        ).execute()
    }

    override fun respondTrade(
        actorId: Int,
        request: ActionRequest.RespondTrade
    ): ExecutionResult<ActionResponse.RespondTrade> {
        return RespondTrade(game, actorId, request.tradeID, request.accept).execute()
    }

    override fun endTurn(actorId: Int): ExecutionResult<ActionResponse.EndTurn> {
        return EndTurn(game, actorId).execute()
    }

    override fun claimVictory(actorId: Int): ExecutionResult<ActionResponse.VictoryClaimed> {
        return ClaimVictory(game, actorId).execute()
    }


}
