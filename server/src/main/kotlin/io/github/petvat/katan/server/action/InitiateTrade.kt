package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.board.ResourceMap
import io.github.petvat.katan.server.dto.InitiateTradeDTO
import io.github.petvat.katan.server.game.GameProgress

class InitiateTrade(
    override val gameProgress: GameProgress,
    override val playerID: Int,
    private val tradeID: Int,
    private val targetPlayers: Set<Int>,
    private val offer: ResourceMap,
    private val request: ResourceMap

) : AbstractAction() {
    override fun execute(): Map<Int, ActionResponse> {
        validatePlayerInTurn() // Currently only the player in turn can initiate trades
        val responses: MutableMap<Int, ActionResponse> = mutableMapOf()
        gameProgress.players.forEach { player ->
            if (player.ID == playerID) {
                responses[playerID] = ActionResponse(true, "You sent a trade offer", null)
            } else if (targetPlayers.contains(player.ID)) {
                val actionDTO = InitiateTradeDTO(playerID, tradeID, targetPlayers, offer, request)
                responses[player.ID] = ActionResponse(
                    true, "Trade offer sent to you." +
                        "Respond to trade ID $tradeID", actionDTO
                )
            }
        }
        return responses
    }
}
