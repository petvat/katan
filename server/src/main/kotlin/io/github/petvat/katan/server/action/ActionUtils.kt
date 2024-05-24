package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.board.BuildKind
import io.github.petvat.katan.server.board.Coordinate
import io.github.petvat.katan.server.dto.BuildDTO
import io.github.petvat.katan.server.game.GameProgress

object ActionUtils {

    fun executeBuildAction(
        gameProgress: GameProgress,
        playerID: Int,
        coordinate: Coordinate,
        buildKind: BuildKind,
        buildAction: (GameProgress, Int, Coordinate, BuildKind) -> Unit
    ): Map<Int, ActionResponse> {
        val responses: MutableMap<Int, ActionResponse> = mutableMapOf()

        // Validate player in turn
        if (gameProgress.playerInTurn() != playerID) {
            throw IllegalArgumentException("Player $playerID is not in turn.") // TODO: Use validate
        }
        // Execute the specific build action
        buildAction(gameProgress, playerID, coordinate, buildKind)

        // Alert players
        gameProgress.players.forEach { player ->
            if (player.ID == playerID) {
                responses[player.ID] =
                    ActionResponse(true, "You built a ${buildKind}.", null)
            } else {
                // Broadcast
                val buildDTO = BuildDTO(playerID, buildKind, coordinate)
                responses[player.ID] =
                    ActionResponse(
                        true,
                        "${player.playerName} built a $buildKind.",
                        buildDTO
                    )
            }
        }

        return responses
    }
}
