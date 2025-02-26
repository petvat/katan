package io.github.petvat.katan.server.api.action

import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.api.GameStates
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.protocol.Response

class StealCard(
    override val game: Game,
    override val playerNumber: Int,
    val stealCardFromPlayerID: Int
) : Action {
    override fun validate(): Boolean {
        TODO("Not yet implemented")
    }

    override fun execute(): ExecutionResult<Response> {
        game.transitionToState(GameStates.ROLL_DICE)
        TODO()
    }
}
