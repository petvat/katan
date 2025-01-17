package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.api.BuildAndTradeState
import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.protocol.Payload
import io.github.petvat.katan.shared.protocol.dto.ActionResponse

class StealCard(
    override val game: Game,
    override val playerNumber: Int,
    val stealCardFromPlayerID: Int
) : Action {
    override fun validate(): String? {
        TODO("Not yet implemented")
    }

    override fun execute(): ExecutionResult<ActionResponse.StealCard> {
        game.transitionToState(BuildAndTradeState(game))
        TODO()
    }
}
