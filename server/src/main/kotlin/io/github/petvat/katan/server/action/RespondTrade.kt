package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.game.GameProgress

class RespondTrade(
    override val gameProgress: GameProgress,
    override val playerID: Int,
    val tradeID: Int,
    val accept: Boolean,
) : Action {

    override fun execute(): Map<Int, ActionResponse> {
        val responses: MutableMap<Int, ActionResponse> = mutableMapOf()
        val trade = gameProgress.currentTurn.currentTrade

        if (trade == null) {
            responses[playerID] = ActionResponse(false, "Trade does not exist.", null)
        } else if (!trade.targets.contains(playerID)) {
            throw IllegalArgumentException("Player $playerID is not a target of this trade.")
        } else if (accept) {
            trade.transact() // do transaction
            responses[playerID] = ActionResponse(true, "You accepted the offer", null)
            gameProgress.players.forEach { player ->
                responses[player.ID] = ActionResponse(true, "$playerID accepted the offer.", null)
            }
            gameProgress.currentTurn.currentTrade = null

        } else {
            // decline
            responses[playerID] = ActionResponse(true, "You declined the offer", null)
            responses[trade.initiator] = ActionResponse(false, "$playerID declined the offer.", null)
        }
        return responses
    }
}
