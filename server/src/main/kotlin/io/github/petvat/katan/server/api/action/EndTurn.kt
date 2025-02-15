package io.github.petvat.katan.server.api.action

import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response


/**
 * This command ends the turn of the current player.
 */
class EndTurn(
    override val game: Game,
    override val playerNumber: Int
) : Action {
    override fun validate(): Boolean {
        return playerNumber != game.playerInTurn()
    }

    override fun execute(): ExecutionResult<Response.EndTurn> {
        if (validate()) {
            return ExecutionResult.Failure(ErrorCode.DENIED, "No your turn.")
        }

        val responses: MutableMap<Int, Response.EndTurn> = mutableMapOf()
        val nextPlayer = game.nextTurn()
        game.players.forEach { player ->
            responses[player.playerNumber] =
                Response.EndTurn("$playerNumber ended their turn. Next player is $nextPlayer\"")
        }
        return ExecutionResult.Success(
            responses
        )
    }
}
