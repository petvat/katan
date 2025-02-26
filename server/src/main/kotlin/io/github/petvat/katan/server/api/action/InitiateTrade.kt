package io.github.petvat.katan.server.api.action

import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response

/**
 * Command to initiate a new trade.
 * Currently only the player in tun can initiate a trade.
 *
 * FUTURE: Turn player and anyone *to* turn player.
 */
class InitiateTrade(
    override val game: Game,
    override val playerNumber: Int,
    private val targetPlayers: Set<Int>,
    private val offer: ResourceMap,
    private val inReturn: ResourceMap

) : Action {
    override fun validate(): Boolean {
        return playerNumber != game.playerInTurn()
    }

    override fun execute(): ExecutionResult<Response.InitTrade> {
        if (validate()) {
            return ExecutionResult.Failure(ErrorCode.DENIED, "Not your turn.")
        }
        val responses = mutableMapOf<Int, Response.InitTrade>()
        // Add trade to game
        val trade = game.addTrade(playerNumber, targetPlayers, offer, inReturn)

        game.players.forEach { player ->
            val initTradeDTO = Response.InitTrade(playerNumber, trade.id, targetPlayers, offer, inReturn, null)
            responses[player.playerNumber] = initTradeDTO
        }
        return ExecutionResult.Success(responses)
    }
}
