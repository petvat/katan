package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.game.GameProgress

class EndTurn(
    override val gameProgress: GameProgress,
    override val playerID: Int
) : AbstractAction() {

    override fun execute(): Map<Int, ActionResponse> {
        val responses: MutableMap<Int, ActionResponse> = mutableMapOf()
        validatePlayerInTurn()
        val nextPlayer = gameProgress.nextTurn()
        gameProgress.players.forEach { player ->
            if (player.ID == playerID) {
                // Maybe send next player ID, but server already sends turnOrder at start
                responses[playerID] = ActionResponse(true, "You ended your turn. Next player is $nextPlayer", null)
            } else {
                responses[player.ID] =
                    ActionResponse(true, "$playerID ended their turn. Next player is $nextPlayer", null)
            }
        }
        return responses
    }
}
