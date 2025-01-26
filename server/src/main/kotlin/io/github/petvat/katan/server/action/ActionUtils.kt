package io.github.petvat.katan.server.action


import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.protocol.Payload
import io.github.petvat.katan.shared.protocol.dto.ActionResponse

object ActionUtils {

    /**
     * Generic execution procedure for build actions.
     * HACK: diff PlaceFirstSettlements
     */
    fun executeBuildAction(
        game: Game,
        playerNumber: Int,
        coordinate: Coordinates,
        buildKind: BuildKind,
        buildAction: (Game, Int, Coordinates, BuildKind) -> Unit
    ): ExecutionResult<ActionResponse.Build> {
        val responses: MutableMap<Int, ActionResponse.Build> = mutableMapOf()

        // Execute the specific build action
        buildAction(game, playerNumber, coordinate, buildKind)


        // TODO: CHECK LONGEST ROAD!
        //  Both because build settlement can ruin longest road!

        val dto =
            ActionResponse.Build(
                playerNumber,
                buildKind,
                coordinate,
                game.getPlayer(playerNumber)!!.victoryPoints,
                false,
                null
            )

        // Alert players
        game.players.forEach { player ->
            responses[player.playerNumber] = dto
        }

        return ExecutionResult.Success(
            responses,
            "$playerNumber built a $buildKind.",
        )
    }
}
