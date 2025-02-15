package io.github.petvat.katan.server.api


import io.github.petvat.katan.server.api.action.BuildAction
import io.github.petvat.katan.server.api.action.RollDice
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.protocol.*

/**
 * Responsible for translating client requests into game actions and performing them.
 */
object ActionAPI {


    private val acceptedRequests = mapOf(
        Request.RollDice to GameStates.ROLL_DICE,
        Request.Build to GameStates.BUILD_TRADE
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
            return handleError(
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
            is ExecutionResult.Failure -> handleError(
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
     * Tries to execute an action. If the action throws an exeception, returns a generic failed request messasage.
     */
    private fun performAction(
        playerNumber: Int,
        actionRequest: Request,
        game: Game
    ): ExecutionResult<Response> {

        return try {
            when (actionRequest) {
                is Request.RollDice -> {
                    val command = RollDice(game, playerNumber)
                    // TODO: This is cleaner:
                    // processIf(actionRequest, playerNumber, game, ::command)
                    runCommandIf(actionRequest, game, command::execute)
                }

                is Request.Build -> {
                    val command = BuildAction(game, playerNumber, actionRequest.coordinates, actionRequest.buildkind)
                    runCommandIf(actionRequest, game, command::execute)
                }

                else -> ExecutionResult.Failure(
                    ErrorCode.SERVER_ERROR,
                    "A processor for this request is not implemented."
                )
            }
        } catch (e: RuntimeException) {
            ExecutionResult.Failure(code = ErrorCode.SERVER_ERROR, "Server error. Could not handle request.")
        }
    }


    /**
     * Run the command if the game is in accepting state.
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

