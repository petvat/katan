package io.github.petvat.katan.server.api.action

import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response

class ClaimVictory(
    override val game: Game,
    override val playerNumber: Int
) : Action {
    override fun validate(): Boolean {
        return playerNumber != game.playerInTurn()
    }

    override fun execute(): ExecutionResult<Response> {

        if (!validate()) {
            return ExecutionResult.Failure(ErrorCode.DENIED, "Not your turn.")
        }

        TODO()
    }

}
