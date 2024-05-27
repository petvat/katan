package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.dto.ActionDTO
import io.github.petvat.katan.server.game.GameProgress

class ActionResponse(
    val actionCode: ActionCode,
    val success: Boolean,
    val message: String,
    vararg data: ActionDTO?
)

interface Action {
    val gameProgress: GameProgress
    val playerID: Int

    /**
     * @return responses for each player
     */
    fun execute(): Map<Int, ActionResponse>

    fun validatePlayerInTurn() {
        if (gameProgress.playerInTurn() != playerID) {
            throw IllegalArgumentException(
                "Player $playerID denied ${this::class.simpleName} " +
                    "action.\n Reason: Not your turn!"
            )
        }
    }
}

abstract class AbstractAction : Action {

    override fun validatePlayerInTurn() {
        if (gameProgress.playerInTurn() != playerID) {
            throw IllegalArgumentException(
                "Player $playerID denied ${this::class.simpleName} " +
                    "action.\n Reason: Not your turn!"
            )
        }
    }
}

// Not really used
enum class ActionCode {
    GAME_CREATE, GAME_START, SETUP_END, ROLL_DICE, MOVE_ROBBER, STEAL_CARD,
    BUILD, INIT_TRADE, RESPOND_TRADE, TURN_END
}
