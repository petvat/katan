package io.github.petvat.katan.server.api.action


import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.protocol.Response

/**
 * This is an implementation of the Command pattern.
 * All actions should implement the Action interface.
 *
 * @property execute Performs the action command. [execute] does not catch any thrown exceptions.
 * @property validate Checks if player is allowed to execute the command. Subclasses can extend this function.
 */
interface Action {
    val game: Game
    val playerNumber: Int

    /**
     * Checks whether the player is allowed to execute this command.
     */
    fun validate(): Boolean

    /**
     * Executes an action command.
     *
     * @return responses for each player.
     */
    fun execute(): ExecutionResult<Response>

}


