package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.board.Coordinate
import io.github.petvat.katan.server.game.GameProgress
import io.github.petvat.katan.server.game.StealCardState

class MoveRobber(
    override val gameProgress: GameProgress,
    override val playerID: Int,
    private val tileCoordinate: Coordinate,
) : AbstractAction() {

    override fun execute(): Map<Int, ActionResponse> {
        val responses: MutableMap<Int, ActionResponse> = mutableMapOf()

        validatePlayerInTurn()
        gameProgress.boardManager.moveRobber(tileCoordinate)
        gameProgress.players.forEach { player ->
            if (player.ID == playerID) {
                responses[playerID] = ActionResponse(
                    // TODO: State ID? or Response ID? Notify if new State
                    //  Or responseID: Send request for action, get response with corresponding ID
                    //  Because you don't know what the response is responding to
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
        gameProgress.gameState = StealCardState(gameProgress)

        return responses
    }

}
