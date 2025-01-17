package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.model.game.Trade
import io.github.petvat.katan.shared.protocol.Payload
import io.github.petvat.katan.shared.protocol.dto.ActionResponse

/**
 * Action to respond to an existing trade.
 */
class RespondTrade(
    override val game: Game,
    override val playerNumber: Int,
    private val tradeId: Int,
    private val accept: Boolean,
) : Action {
    override fun validate(): String? {
        val trade: Trade = game.getTradeByID(tradeId)

        if (!trade.targets.contains(playerNumber)) {
            return "You (Player $playerNumber) are not a target of this trade."
        }
        return null
    }

    /**
     * Note the subtle difference between accept and success; response is successful if it sucessfully
     * responds to a existing request. So a response can be successful even though it was declined by the target.
     * If false, something wrong happened during the processing of the request (indicating bad request), causing the success flag to return false.
     */
    override fun execute(): ExecutionResult<ActionResponse.RespondTrade> {
        validate()?.let { ExecutionResult.Failure(it) }

        val responses: MutableMap<Int, ActionResponse.RespondTrade> = mutableMapOf()
        // val trade = game.currentTurn.currentTrade
        val trade: Trade = game.getTradeByID(tradeId)

        // if trade = null return ExecutionResult.failure

        validate()?.let { ExecutionResult.Failure(it) }

        if (accept) { // do transaction
            trade.transact(game.getPlayer(playerNumber)!!) // atomic


            val tradeDTO = ActionResponse.RespondTrade(
                playerNumber,
                tradeId,
                true,
                TODO("Private game state")
            )

            game.players.forEach { player ->
                responses[player.playerNumber] =
                    tradeDTO
            }
            game.currentTurn.currentTrade = null
        } else {
            // decline
            val tradeDTO = ActionResponse.RespondTrade(
                playerNumber,
                tradeId,
                true,
                null // No difference to game state.
            )

            responses[playerNumber] =
                tradeDTO
            responses[trade.initiator.playerNumber] =
                tradeDTO
        }
        return ExecutionResult.Success(responses, "$playerNumber responded to the offer.")
    }

}
