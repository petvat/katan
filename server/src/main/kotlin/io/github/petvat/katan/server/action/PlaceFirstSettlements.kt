package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.board.BuildKind
import io.github.petvat.katan.server.board.Coordinate
import io.github.petvat.katan.server.board.VillageKind
import io.github.petvat.katan.server.game.GameProgress


class PlaceFirstSettlements(
    override val gameProgress: GameProgress,
    override val playerID: Int,
    private val intersectionCoordinate: Coordinate
) : AbstractAction() {

    override fun execute(): Map<Int, ActionResponse> {
        return ActionUtils.executeBuildAction(
            gameProgress,
            playerID,
            intersectionCoordinate,
            BuildKind.Village(VillageKind.SETTLEMENT)
        ) { gp, pid, ic, _ ->
            gp.boardManager.buildSettlementInitial(
                gp.getPlayer(pid),
                ic,
                VillageKind.SETTLEMENT // Not optimal
            )
        }
    }
}
