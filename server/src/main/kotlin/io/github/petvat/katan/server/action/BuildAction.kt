package io.github.petvat.katan.server.action


import io.github.petvat.katan.server.board.BuildKind
import io.github.petvat.katan.server.board.Coordinate
import io.github.petvat.katan.server.game.GameProgress

class BuildAction(
    override val gameProgress: GameProgress,
    override val playerID: Int,
    val coordinate: Coordinate,
    val buildKind: BuildKind
) : Action {

    /**
     * Build village on intersection
     */
    override fun execute(): Map<Int, ActionResponse> {
        return ActionUtils.executeBuildAction(
            gameProgress,
            playerID,
            coordinate,
            buildKind
        ) { gp, pid, ic, bk ->
            when (bk) {
                is BuildKind.Village -> {
                    gp.boardManager.buildSettlement(
                        gp.getPlayer(pid),
                        ic,
                        bk.kind
                    )
                }

                is BuildKind.Road -> {
                    gp.boardManager.buildRoad(
                        gp.getPlayer(pid),
                        ic,
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
//    override val gameProgress: GameProgress,
//    // override val ID: Int,
//    override val playerID: Int,
//    private val buildKind: io.github.petvat.katan.server.board.BuildKind,
//    private val intersectionCoordinate: io.github.petvat.katan.server.board.Coordinate
//) : io.github.petvat.katan.server.action.AbstractAction() {
//
//    /**
//     * Build village on intersection
//     */
//    override fun execute(): Map<Int, io.github.petvat.katan.server.action.ActionResponse> {
//        return ActionUtils.executeBuildAction(
//            gameProgress,
//            playerID,
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
