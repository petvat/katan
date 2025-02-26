package io.github.petvat.katan.server.api


import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.server.api.action.*
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.model.SessionId
import io.github.petvat.katan.shared.protocol.*

/**
 * Responsible for translating client requests into game actions and performing them.
 */
object ActionAPI {

    private val logger = KotlinLogging.logger { }

    /**
     * Map of what requests are allowed for each game state.
     */
    private val acceptedRequests = mapOf(
        Request.BuildInitSettl to GameStates.SETUP,
        Request.RollDice to GameStates.ROLL_DICE,
        Request.Build to GameStates.BUILD_TRADE,
        Request.MoveRobber to GameStates.MOVE_ROBBER,
        Request.Steal to GameStates.STEAL,
        Request.EndTurn to GameStates.BUILD_TRADE
    )

    /**
     * Services request by performing the action, then returning the response payload.
     */
    suspend fun serviceRequest(
        sessionId: SessionId,
        request: Request, // NOTE: Maybe back to ActionRequest
        game: Game
    ): Map<SessionId, Response> {

        if (request.type != MTypes.REQ_GAMEACTION) {
            return KatanApi.handleError(
                sessionId,
                requestId = request.requestId,
                code = ErrorCode.DENIED,
                "Server error. DEBUG: Tried to handle non-game action with game API."
            )
        }

        val playerNumber = game.getPlayerNumber(sessionId)

        val result =
            performAction(playerNumber, request, game)

        return when (result) {
            is ExecutionResult.Failure -> KatanApi.handleError(
                sid = sessionId,
                requestId = request.requestId,
                code = result.code,
                description = result.description
            )

            is ExecutionResult.Success -> {
                result.data
                    .mapKeys { (pid, _) -> game.getSessionId(pid) }
            }
        }
    }

    /**
     * Tries to execute an action. If the action throws an exception, returns a generic failed request messasage.
     *
     * @param playerNumber The player number of the requesting player ref. game
     */
    private fun performAction(
        playerNumber: Int,
        actionRequest: Request,
        game: Game
    ): ExecutionResult<Response> {

        return try {
            val command = when (actionRequest) {
                is Request.BuildInitSettl -> {
                    PlaceFirstSettlements(game, playerNumber, actionRequest.coordinates)
                }

                is Request.RollDice -> {
                    RollDice(game, playerNumber)
                }

                is Request.Build -> {
                    BuildAction(game, playerNumber, actionRequest.coordinates, actionRequest.buildkind)
                }

                is Request.MoveRobber -> {
                    MoveRobber(game, playerNumber, actionRequest.coordinates)
                }

                is Request.Steal -> {
                    StealCard(game, playerNumber, actionRequest.playerNumber)
                }

                is Request.InitTrade -> {
                    InitiateTrade(
                        game,
                        playerNumber,
                        actionRequest.targetPlayers,
                        actionRequest.offer,
                        actionRequest.inReturn
                    )
                }

                is Request.EndTurn -> {
                    EndTurn(game, playerNumber)
                }

                is Request.ClaimVictory -> {
                    ClaimVictory(game, playerNumber)
                }

                else -> {
                    // NOTE: Consider again to have distinction between game and non-game requests.
                    logger.error { "A non-game request was routed to game API." }
                    throw IllegalArgumentException("Internal error.")
                }
            }
            return runCommandIf(actionRequest, game) { command.execute() }
        } catch (e: RuntimeException) {
            // NOTE: Could be interesting to have more fine-grained error codes.
            logger.error { "Unhandled exception." }
            ExecutionResult.Failure(
                code = ErrorCode.SERVER_ERROR,
                "Server error. Could not handle request. ${e.message}"
            )
        }
    }

    /**
     * Run the command if the game is in accepting state.
     *
     */
    private fun runCommandIf(
        request: Request,
        game: Game,
        command: () -> ExecutionResult<Response>
    ): ExecutionResult<Response> {
        return if (game.state != acceptedRequests[request]) {
            ExecutionResult.Failure(ErrorCode.DENIED, "Cannot perform request in the current state.")
        } else {
            command()
        }
    }

}

