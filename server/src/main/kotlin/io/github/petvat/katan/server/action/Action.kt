package io.github.petvat.katan.server.action


import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.protocol.dto.ActionResponse

/**
 * This is an implementation of the Command pattern.
 * All actions should implement the Action interface.
 *
 * @property execute Performs the action command. [execute] does not catch any thrown exceptions.
 * @property validatePlayerInTurn Checks if player is allowed to execute the command. Subclasses can extend this function.
 */
interface Action {
    val game: Game
    val playerNumber: Int


    fun validate(): String?

    /**
     * Executes an action command.
     *
     * @return responses for each player.
     */
    fun execute(): ExecutionResult<ActionResponse>

    /**
     * Some actions requires it is the requesting player's turn. For these actions, use the this to validate the player.
     */
    fun validatePlayerInTurn(): Boolean {
        return game.playerInTurn() == playerNumber

//        if (game.playerInTurn() != userId) {
//            throw IllegalArgumentException(
//                "Player $userId denied ${this::class.simpleName} " +
//                    "action.\n Reason: Not your turn!"
//            )
//        }
    }
}


