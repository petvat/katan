package io.github.petvat.katan.server.api.action

import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.model.game.Trade
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response

/**
 * Action to respond to an existing trade.
 */
class RespondTrade(
    override val game: Game,
    override val playerNumber: Int,
    private val tradeId: Int,
    private val accept: Boolean,
) : Action {
    override fun validate(): Boolean {
        val trade: Trade = game.getTradeByID(tradeId)
        return (trade.targets.contains(playerNumber))
    }

    override fun execute(): ExecutionResult<Response.TradeResponse> {

        if (validate()) return ExecutionResult.Failure(
            ErrorCode.DENIED,
            "You (Player $playerNumber) are not a target of this trade."
        )

        val responses: MutableMap<Int, Response.TradeResponse> = mutableMapOf()
        // val trade = game.currentTurn.currentTrade
        val trade: Trade = game.getTradeByID(tradeId)

        // if trade = null return ExecutionResult.failure

        if (accept) { // do transaction
            trade.transact(game.getPlayer(playerNumber)!!) // atomic


            val tradeDTO = Response.TradeResponse(
                playerNumber,
                tradeId,
                true,
                "$playerNumber accepted the trade"
            )

            game.players.forEach { player ->
                responses[player.playerNumber] =
                    tradeDTO
            }
            game.currentTurn.currentTrade = null
        } else {
            // decline
            val tradeDTO = Response.TradeResponse(
                playerNumber,
                tradeId,
                true,
                "$playerNumber declined the trade"
                // null No difference to game state.
            )

            responses[playerNumber] =
                tradeDTO
            responses[trade.initiator.playerNumber] =
                tradeDTO
        }
        return ExecutionResult.Success(responses)
    }

}
