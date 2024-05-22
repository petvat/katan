package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.board.Coordinate
import io.github.petvat.katan.server.game.GameProgress

class MoveRobber(
    override val gameProgress: GameProgress,
    override val playerID: Int,
    val tileCoordinate: Coordinate,
) : AbstractAction() {

    override fun execute(): Map<Int, ActionResponse> {
        validatePlayerInTurn()
        val responses: MutableMap<Int, ActionResponse> = mutableMapOf()
        if (gameProgress.boardManager.moveRobber(tileCoordinate)) {
            gameProgress.players.forEach { player ->
                if (player.ID == playerID) {
                    responses[playerID] = ActionResponse(
                        true, "You moved the robber to" +
                            "coordinate $tileCoordinate.", null
                    )
                } else {
                    responses[playerID] = ActionResponse(
                        true, "player $playerID moved the robber to" +
                            "coordinate $tileCoordinate.", null
                    )
                }
            }
        } else {
            responses.clear()
            responses[playerID] = ActionResponse(false, "Unsuccessful moverobber", null)
        }
        return responses
    }

}
