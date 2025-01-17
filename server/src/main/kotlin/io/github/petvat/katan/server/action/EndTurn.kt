package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.protocol.Payload
import io.github.petvat.katan.shared.protocol.dto.ActionResponse


class EndTurn(
    override val game: Game,
    override val playerNumber: Int
) : Action {
    override fun validate(): String? {
        TODO("Not yet implemented")
    }


    override fun execute(): ExecutionResult<ActionResponse.EndTurn> {
        validate()?.let { return ExecutionResult.Failure(it) }
        val responses: MutableMap<Int, ActionResponse.EndTurn> = mutableMapOf()
        val nextPlayer = game.nextTurn()
        game.players.forEach { player ->
            responses[player.playerNumber] =
                ActionResponse.EndTurn(nextPlayer)
        }
        return ExecutionResult.Success(
            responses, "$playerNumber ended their turn. Next player is $nextPlayer",
        )
    }
}
