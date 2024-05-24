package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.dto.ActionDTO
import io.github.petvat.katan.server.game.GameProgress

class ActionResponse(val success: Boolean, val message: String, val data: ActionDTO?)

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

enum class ActionID {
    ROLL, BUILD, BUILD_SETTL_INIT, SETUP, INIT_TRADE, RESPOND_TRADE
}
