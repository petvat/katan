package io.github.petvat.katan.server.api.action


import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.protocol.Response

object ActionUtils {

    /**
     * Generic execution procedure for build actions.
     * @param buildAction Function for the specific build action.
     */
    fun executeBuildAction(
        game: Game,
        playerNumber: Int,
        coordinate: Coordinates,
        buildKind: BuildKind,
        buildAction: (Game, Int, Coordinates, BuildKind) -> Unit
    ): ExecutionResult<Response.Build> {
        val responses: MutableMap<Int, Response.Build> = mutableMapOf()

        // Execute the specific build action
        buildAction(game, playerNumber, coordinate, buildKind)


        // TODO: CHECK LONGEST ROAD!
        //  Both because build settlement and road can affect longest road!

        val dto =
            Response.Build(
                playerNumber,
                buildKind,
                coordinate,
                game.getPlayer(playerNumber)!!.victoryPoints,
                "$playerNumber built a $buildKind.",
            )

        // Alert players
        game.players.forEach { player ->
            responses[player.playerNumber] = dto
        }

        return ExecutionResult.Success(
            responses,
        )
    }
}
