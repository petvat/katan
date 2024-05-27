package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.dto.TradeDTO
import io.github.petvat.katan.server.game.GameProgress

/**
 * Action to respond to an existing trade.
 */
class RespondTrade(
    override val gameProgress: GameProgress,
    override val playerID: Int,
    val tradeID: Int,
    val accept: Boolean,
) : Action {

    /**
     * Note the subtle difference between accept and success; response is successful if it sucessfully
     * responds to a existing request. So a response can be successful even though it was declined by the target.
     * If false, something wrong happened during the processing of the request, causing the success flag to return false.
     */
    override fun execute(): Map<Int, ActionResponse> {
        val responses: MutableMap<Int, ActionResponse> = mutableMapOf()
        val trade = gameProgress.currentTurn.currentTrade
        val tradeDTO = TradeDTO(
            playerID, tradeID, accept
        )

        // TODO: Use TradeID instead of currentTrade!!!
        if (trade == null) {
            responses[playerID] = ActionResponse(ActionCode.RESPOND_TRADE, false, "Trade does not exist.", null)
        } else if (!trade.targets.contains(playerID)) {
            responses[playerID] = ActionResponse(
                ActionCode.RESPOND_TRADE,
                false,
                "You (Player $playerID) are not a target of this trade.",
                null
            )
        } else if (accept) {
            trade.transact() // do transaction
            responses[playerID] = ActionResponse(ActionCode.RESPOND_TRADE, true, "You accepted the offer", null)
            gameProgress.players.forEach { player ->
                responses[player.ID] =
                    ActionResponse(ActionCode.RESPOND_TRADE, true, "$playerID accepted the offer.", tradeDTO)
            }
            gameProgress.currentTurn.currentTrade = null // TODO: REemove ID
        } else {
            // decline
            responses[playerID] = ActionResponse(ActionCode.RESPOND_TRADE, true, "You declined the offer", tradeDTO)
            responses[trade.initiator] =
                ActionResponse(ActionCode.RESPOND_TRADE, true, "$playerID declined the offer.", tradeDTO)
        }
        return responses
    }
}
