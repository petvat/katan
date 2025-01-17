package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.protocol.Payload
import io.github.petvat.katan.shared.protocol.dto.ActionResponse

/**
 * Command to initiate a new trade.
 * Currently only the player in tun can initiate a trade.
 *
 */
class InitiateTrade(
    override val game: Game,
    override val playerNumber: Int,
    private val targetPlayers: Set<Int>,
    private val offer: ResourceMap,
    private val inReturn: ResourceMap

) : Action {
    override fun validate(): String? {
        if (playerNumber != game.playerInTurn()) {
            return "Not you turn"
        }
        return null
    }

    override fun execute(): ExecutionResult<ActionResponse.InitiateTrade> {
        validate()?.let { ExecutionResult.Failure(it) }
        val responses = mutableMapOf<Int, ActionResponse.InitiateTrade>()
        // Add trade to game
        val trade = game.addTrade(playerNumber, targetPlayers, offer, inReturn)

        game.players.forEach { player ->
            val initTradeDTO = ActionResponse.InitiateTrade(trade.id, targetPlayers, offer, inReturn)
            responses[player.playerNumber] = initTradeDTO
        }

        return ExecutionResult.Success(responses, "$playerNumber initiated a trade with players $targetPlayers.")
    }
}
