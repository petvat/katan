package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.dto.ActionDTO
import io.github.petvat.katan.server.dto.GameStartedDTO
import io.github.petvat.katan.server.game.GameProgress

class StartGame(
    override val gameProgress: GameProgress,
    override val playerID: Int
) : Action {

    /**
     * NOTE: start with simple solution
     * Anyone can start the game as long as they are a player in this game
     */
    override fun execute(): Map<Int, ActionResponse> {
        gameProgress.getPlayer(playerID)
        val responses: MutableMap<Int, ActionResponse> = mutableMapOf()
        // gameProgress.initializeRandomTurnOrder() HACK: Assume random, should be chosen in some way
        gameProgress.players.forEach { player ->
            val actionDTO = GameStartedDTO(playerID, gameProgress.initializeRandomTurnOrder())
            responses[player.ID] = ActionResponse(
                ActionCode.GAME_START, true, "Game has now started!" +
                    "First player to place a settlement is ${gameProgress.turnOrder[0]}", actionDTO
            )
        }
        return responses
    }

}
