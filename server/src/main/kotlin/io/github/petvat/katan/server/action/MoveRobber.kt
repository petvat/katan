package io.github.petvat.katan.server.action


import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.api.StealCardState
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.protocol.dto.ActionResponse


class MoveRobber(
    override val game: Game,
    override val playerNumber: Int,
    private val tileCoordinate: HexCoordinates,
) : Action {
    override fun validate(): String? {
        TODO("Not yet implemented")
    }

    override fun execute(): ExecutionResult<ActionResponse.MoveRobber> {
        val responses: MutableMap<Int, ActionResponse.MoveRobber> = mutableMapOf()

        validate()?.let { return ExecutionResult.Failure(it) }

        game.boardManager.moveRobber(tileCoordinate)

        // Check if nearby players, if true, go to steal state

        val moveRobberDTO = ActionResponse.MoveRobber(tileCoordinate, true)
        game.players.forEach { player ->
            if (player.playerNumber == playerNumber) {
                responses[playerNumber] =
                    moveRobberDTO

            } else {
                responses[playerNumber] =
                    moveRobberDTO

            }
        }

        game.transitionToState(StealCardState(game))

        return ExecutionResult.Success(
            responses, "player $playerNumber moved the robber to" +
                "coordinate $tileCoordinate."
        )
    }

}
