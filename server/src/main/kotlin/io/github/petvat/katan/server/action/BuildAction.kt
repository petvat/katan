package io.github.petvat.katan.server.action


import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.protocol.dto.ActionResponse


class BuildAction(
    override val game: Game,
    override val playerNumber: Int,
    val coordinate: Coordinates,
    private val buildKind: BuildKind
) : Action {

    override fun validate(): String? {
        if (playerNumber != game.playerInTurn()) {
            return "Not your turn!"
        } else return null
    }


    /**
     * Builds [BuildKind.Village] on intersection or [BuildKind.Road] on edge.
     */
    override fun execute(): ExecutionResult<ActionResponse.Build> {

        validate()?.let { return ExecutionResult.Failure(it) }

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

///**
// * Player action build building
// * TODO: RETIRED
// */
//class BuildVillage(
//    override val game: GameProgress,
//    // override val ID: Int,
//    override val userId: Int,
//    private val buildKind: io.github.petvat.katan.server.board.BuildKind,
//    private val intersectionCoordinate: io.github.petvat.katan.server.board.Coordinate
//) : io.github.petvat.katan.server.action.AbstractAction() {
//
//    /**
//     * Build village on intersection
//     */
//    override fun execute(): Map<Int, io.github.petvat.katan.server.action.ActionResponse> {
//        return ActionUtils.executeBuildAction(
//            game,
//            userId,
//            intersectionCoordinate,
//            buildKind
//        ) { gp, pid, ic, bk ->
//            if (bk is io.github.petvat.katan.server.board.BuildKind.Village) {
//                gp.boardManager.buildSettlement(
//                    gp.getPlayer(pid),
//                    ic,
//                    bk.kind
//                )
//            }
//        }
//    }
//}
