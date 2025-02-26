package io.github.petvat.katan.server.api.action


import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.api.GameStates
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response

/**
 * This command
 */
class MoveRobber(
    override val game: Game,
    override val playerNumber: Int,
    private val tileCoordinate: HexCoordinates,
) : Action {
    override fun validate(): Boolean {
        return playerNumber == game.playerInTurn()
    }

    override fun execute(): ExecutionResult<Response.RobberMoved> {
        val responses: MutableMap<Int, Response.RobberMoved> = mutableMapOf()

        if (!validate()) {
            return ExecutionResult.Failure(ErrorCode.DENIED, "Not your turn.")
        }

        if (game.boardManager.moveRobber(tileCoordinate)) {
            return ExecutionResult.Failure(ErrorCode.DENIED, "Cannot move robber to these coordinates..")
        }

        // TODO: Check if nearby players, if true, go to steal state

        val moveRobberDTO = Response.RobberMoved(
            playerNumber,
            tileCoordinate,
            "player $playerNumber moved the robber to" +
                "coordinate $tileCoordinate."
        )
        game.players.forEach { player ->
            if (player.playerNumber == playerNumber) {
                responses[playerNumber] =
                    moveRobberDTO // Diff one, maybe with possible players to steal from?

            } else {
                responses[playerNumber] =
                    moveRobberDTO

            }
        }

        // TODO: Check if can steal
        game.transitionToState(GameStates.STEAL)

        return ExecutionResult.Success(
            responses
        )
    }

}
