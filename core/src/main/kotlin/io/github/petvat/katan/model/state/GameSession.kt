package io.github.petvat.katan.model.state

import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.EdgeCoord
import io.github.petvat.katan.shared.hexlib.NodeCoord
import io.github.petvat.katan.shared.model.board.Board
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.Edge
import io.github.petvat.katan.shared.model.board.Node
import io.github.petvat.katan.shared.model.board.Road
import io.github.petvat.katan.shared.model.board.Village
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.Trade
import io.github.petvat.katan.shared.model.game.Phase
import io.github.petvat.katan.shared.model.game.PlayerColor
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.RuleBook


data class GameSession(
    val id: String,
    val player: Int,
    val otherPlayers: List<Int>,
    val colors: Map<Int, PlayerColor>,
    val turnOrder: List<Int>,
    val turnIndex: Int,
    val resources: ResourceMap,
    val otherResources: Map<Int, Int>,
    val victoryPoints: Map<Int, Int>,
    val onGoingTrades: List<Trade>,
    val phase: Phase,
    val winner: Int? = null,
    val rules: RuleBook,
    var board: Board
) {
    val turnPlayer: Int get() = turnOrder[turnIndex % turnOrder.size]
}


fun GameSession.rolledDice(resources: ResourceMap, othersResources: Map<Int, Int>, moveRobber: Boolean): GameSession {
    return this.copy(
        resources = resources,
        otherResources = otherResources.mapValues { (k, _) ->
            othersResources[k]!! // TODO: Check
        },
        phase = if (moveRobber) Phase.MOVE_ROBBER else this.phase
    )
}

fun GameSession.advanceTurn(): GameSession {
    return this.copy(
        onGoingTrades = emptyList(),
        turnIndex = turnIndex + 1 % turnOrder.size
    )
}


fun GameSession.tradeDeclined(tradeId: Int, by: Int): GameSession {
    val trade = onGoingTrades.singleOrNull { it.id == tradeId } ?: return this
    if (by !in trade.pending) return this
    val updated = onGoingTrades.map {
        if (it.id == tradeId) it.copy(declinedBy = it.declinedBy + by) else it
    }
    return this.copy(onGoingTrades = updated.filterNot { it.id == tradeId && it.pending.isEmpty() }) // Remove the old one
}

fun GameSession.addBuilding(
    builder: Int,
    building: BuildKind,
    coordinates: Coordinates,
    victoryPoints: Map<Int, Int>
): GameSession {

    val newBoard = when (building) {
        is BuildKind.Road ->
            board.copy(
                paths = board.paths + Edge(
                    coordinates as EdgeCoord,
                    Road(building.kind, builder)
                )
            )

        is BuildKind.Village ->
            when (building.kind) {
                VillageKind.SETTLEMENT ->
                    board.copy(
                        intersections = board.intersections + Node(
                            coordinates as NodeCoord,
                            Village(VillageKind.SETTLEMENT, builder)
                        )
                    )

                VillageKind.CITY ->
                    board.copy(
                        intersections = board.intersections.map { intersection ->
                            if (intersection.coordinate == coordinates) {
                                intersection.copy(
                                    village = intersection.village.copy(
                                        villageKind = VillageKind.CITY
                                    )
                                )
                            } else intersection
                        }
                    )
            }
    }

    return copy(
        board = newBoard,
        victoryPoints = victoryPoints
    )
}
