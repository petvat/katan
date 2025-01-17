package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.protocol.Payload
import io.github.petvat.katan.shared.protocol.dto.ActionResponse

class ClaimVictory(
    override val game: Game,
    override val playerNumber: Int
) : Action {
    override fun validate(): String? {
        if (playerNumber != game.playerInTurn()) {
            return "Not your turn."
        }
        return null
    }

    override fun execute(): ExecutionResult<ActionResponse.VictoryClaimed> {

        validate()?.let { ExecutionResult.Failure(it) }

        TODO()
    }

}
