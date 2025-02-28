package io.github.petvat.katan.server.api.action


import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response


class BuildAction(
    override val game: Game,
    override val playerNumber: Int,
    private val coordinate: Coordinates,
    private val buildKind: BuildKind
) : Action {

    override fun validate(): Boolean {
        return (playerNumber != game.playerInTurn())
    }

    /**
     * Builds [BuildKind.Village] on intersection or [BuildKind.Road] on edge.
     */
    override fun execute(): ExecutionResult<Response.Build> {

        if (!validate()) {
            return ExecutionResult.Failure(ErrorCode.DENIED, "Not your turn.")
        }

        return ActionUtils.executeBuildAction(
            game,
            playerNumber,
            coordinate,
            buildKind
        ) { gp, pid, ic, bk ->
            when (bk) {
                is BuildKind.Village -> {
                    gp.boardManager.buildSettlement(
                        gp.getPlayer(pid)!!,
                        ic as ICoordinates,
                        bk.kind
                    )
                }

                is BuildKind.Road -> {
                    gp.boardManager.buildRoad(
                        gp.getPlayer(pid)!!,
                        ic as EdgeCoordinates,
                        bk.kind
                    )
                }
            }
        }
    }

}
