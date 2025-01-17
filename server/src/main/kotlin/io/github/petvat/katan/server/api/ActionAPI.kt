package io.github.petvat.katan.server.api


import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.protocol.ActionCode
import io.github.petvat.katan.shared.protocol.SessionId
import io.github.petvat.katan.shared.protocol.Payload
import io.github.petvat.katan.shared.protocol.dto.ActionRequest
import io.github.petvat.katan.shared.protocol.dto.ActionResponse

/**
 * Responsible for translating client requests into game actions and performing them.
 */
object ActionAPI {


    /**
     * Services request by performing the action, then returning the response payload.
     */
    fun serviceRequest(
        sessionId: SessionId,
        request: ActionRequest,
        game: Game
    ): Map<SessionId, Payload<ActionResponse>> {
        val actorId = game.getPlayerId(sessionId)
        val responses: Map<Int, Payload<ActionResponse>> =
            performAction(actorId, request, game) // TODO: add Id

        return responses.mapKeys { (pid, _) ->
            game.getSessionId(pid)
        }
    }

    /**
     * Tries to execute an action. If the action throws an exeception, returns a generic failed request messasage.
     */
    private fun performAction(
        actorId: Int,
        actionRequest: ActionRequest,
        game: Game
    ): Map<Int, Payload<ActionResponse>> {

        // TODO: Use ExecutionResult

        // Success: Map<Int, ActionResponse>
        // Failure: reason, actionCode or a nonce

        val response: ExecutionResult<ActionResponse>
        try {
            response = when (actionRequest.actionCode) {
                ActionCode.ROLL_DICE -> game.gameState.rollDice(actorId)

                ActionCode.INIT_TRADE -> game.gameState.initiateTrade(
                    actorId,
                    actionRequest as ActionRequest.InitiateTrade
                )

                ActionCode.RESPOND_TRADE -> game.gameState.respondTrade(
                    actorId,
                    actionRequest as ActionRequest.RespondTrade
                )

                ActionCode.BUILD -> game.gameState.build(
                    actorId,
                    actionRequest as ActionRequest.Build
                )

                ActionCode.MOVE_ROBBER -> game.gameState.moveRobber(
                    actorId,
                    actionRequest as ActionRequest.MoveRobber
                )

                ActionCode.TURN_END -> game.gameState.endTurn(actorId)

                ActionCode.SETUP_END -> TODO("???")

                ActionCode.STEAL_CARD -> game.gameState.stealCard(
                    actorId,
                    actionRequest as ActionRequest.StealCard
                )

                ActionCode.CLAIM_VICTORY -> TODO()

                ActionCode.INIT_SETTL -> TODO()
            }
            return when (response) {
                is ExecutionResult.Failure -> mapOf(actorId to Payload(false, response.description, null))
                is ExecutionResult.Success -> response.data
                    .mapValues { (_, res) -> Payload(true, response.description, res) }
            }
        } catch (e: RuntimeException) {
            return mapOf(
                actorId to Payload<ActionResponse>(
                    false,
                    "Unsuccessful request. Error occured of type ${e::class}. ${e.message ?: "Unknown exception occurred."} ",
                    null
                )
            )
        }
    }

}

