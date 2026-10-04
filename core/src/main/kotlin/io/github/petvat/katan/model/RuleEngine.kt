package io.github.petvat.katan.model

import io.github.petvat.katan.shared.hexlib.EdgeCoord
import io.github.petvat.katan.shared.hexlib.HexUtils
import io.github.petvat.katan.shared.hexlib.NodeCoord
import io.github.petvat.katan.shared.model.board.*
import io.github.petvat.katan.shared.model.game.RuleBook


class BoardHelpers(
    ruleBook: RuleBook,
) {
    val manager: BoardManager = BoardManager(ruleBook)

    fun updateSetupFrontier(player: Int, board: Board): Set<NodeCoord> {
        return board.tiles
            .flatMap { HexUtils.adjacentIntersections(it.hexCoordinate) }
            .toSet()
            .minus(
                manager.invalidIntersectionsByDistanceRule(board)
            )
    }

    fun updateCityFrontier(player: Int, board: Board): Set<NodeCoord> {
        return board.intersections
            .filter { it.village.owner == player && it.village.villageKind == VillageKind.SETTLEMENT }
            .map { it.coordinate }
            .toSet()
    }

    fun updateSettlementFrontier(
        player: Int,
        board: Board
    )
        : Set<NodeCoord> {
        // cache frontier, update ved ny build event
        // val playerBuildingCoordinates = intersections.filter { i -> i.hasBuilding && i.building?.owner == player }
        val frontier: MutableSet<NodeCoord> = mutableSetOf()
        val playerRoads = board.paths
            .filter { p -> p.road.owner == player }

        // effektiv trealgoritme, DFS - Seinare
        // start with random
        val playerRoadCoordinates = playerRoads.map { pr -> pr.coordinate }
        playerRoadCoordinates.forEach { c ->
            val vertical = (c.q + c.r) % 2 == 0
            val adjacentIntersectionCoordinates =
                if (vertical)
                    NodeCoord(
                        c.q + 1,
                        c.r
                    ) to NodeCoord(c.q, c.r + 1)
                else
                    NodeCoord(
                        c.q,
                        c.r
                    ) to NodeCoord(c.q + 1, c.r + 1)
            if (manager.intersectionAt(adjacentIntersectionCoordinates.first, board.intersections.toList()) ||
                manager.intersectionAt(adjacentIntersectionCoordinates.second, board.intersections.toList())
            ) {
                return@forEach
            }
            adjacentIntersectionCoordinates.toList()
                .filter {
                    frontier.contains(it) && HexUtils.isValidCoordinate(
                        it,
                        board.tiles.map { t -> t.hexCoordinate })
                }
                .forEach {
                    frontier += it
                }
        }
        return frontier
    }

    /**
     * Get all possible coordinates for placing a road.
     *
     * NOTE: Could be cool to have this in separate file as utils.
     */
    fun updateRoadFrontier(player: Int, board: Board): Set<EdgeCoord> {
        // logic:
        // If sum is even: vertical
        // If sum is odd: horizontal

        // TODO: GEN
        val verticalOffsets = arrayOf(
            1, 0,
            0, -1,
            -1, 0,
            0, 1
        )
        val horizontalOffsets = arrayOf(
            1, 1,
            0, -1,
            -1, -1,
            0, 1
        )
        // set operations
        // TODO: BOUNDS
        val ownerCoordinates = board.paths
            .filter { p -> p.road.owner == player }
            .map { p -> p.coordinate }
            .toSet()

        val frontier: MutableSet<EdgeCoord> = mutableSetOf()

        ownerCoordinates
            .forEach { c ->
                val offset: Array<Int>
                val x = c.q
                val y = c.r
                offset = if ((x + y) % 2 == 0) verticalOffsets else horizontalOffsets

                for (i in offset.indices.step(2)) {
                    val coordinateOffset = EdgeCoord(x + i, y + i + 1)
                    if (!frontier.contains(coordinateOffset)
                        && ownerCoordinates.contains(coordinateOffset)
                    ) {
                        frontier.add(coordinateOffset)
                    }
                }
            }
        return frontier.filter { HexUtils.isValidCoordinate(it, board.tiles.map { t -> t.hexCoordinate }) }.toSet()
    }

}


// FIXME: DUPLICATE IN SERVER. Below updateSetupFrontier
object RuleEngineDEPR {
    /**
     * Checks whether player can build a city on coordinate.
     * The intersection needs to have a settlement.
     */
    private fun canBuildCity(
        player: Int,
        coordinate: NodeCoord,
        board: Board
    ): Boolean {
        return board.intersections
            .filter { i -> i.village.owner == player && i.village.villageKind == VillageKind.CITY }
            .map { i -> i.coordinate }
            .contains(coordinate)
    }

    /**
     * Checks whether coordinate is valid.
     */
    fun canBuildSettlement(
        player: Int,
        coordinate: NodeCoord,
        board: Board
    ): Boolean {
        // road into intersection owned player and follows distance rule
        return (getPathsOwnedBy(player, board.paths.toList()).any {
            HexUtils.getAdjacentPaths(coordinate, board.tiles.map { t -> t.hexCoordinate }).contains(it)
        } && !invalidIntersectionsByDistanceRule(player, board).contains(coordinate))
    }

    /**
     * Road frontier will be different for different roads.
     * TODO: fix later
     */
    private fun canBuildRoad(
        player: Int,
        coordinate: EdgeCoord,
        roadKind: RoadKind,
        board: Board
    ): Boolean {
        return (!board.paths.map { p -> p.coordinate }.contains(coordinate)
            ||
            (getPathsOwnedBy(player, board.paths.toList())
                .any { HexUtils.getAdjacentPaths(coordinate).contains(it) }
                )) // FIXME: && hasSufficientResources(player, roadKind.cost)
        // return getRoadFrontier(player).contains(coordinate)
    }


    fun getPathsOwnedBy(player: Int, paths: List<Edge>): List<EdgeCoord> {
        return paths.filter { p -> p.road.owner == player }
            .map { p -> p.coordinate }.toList()
    }

    /**
     * Check if a building on this intersection would violate distance rule.
     *
     * @param intersectionCoordinate to check
     * @return true if comply distance rule
     */
    fun distanceRule(intersectionCoordinate: NodeCoord, board: Board): Boolean {
        return board.intersections
            .map { i -> i.coordinate }
            .any {
                it in HexUtils.getAdjacentIntersections(
                    intersectionCoordinate,
                    board.tiles.map { t -> t.hexCoordinate })
            }
    }

    fun invalidIntersectionsByDistanceRule(player: Int, board: Board): Set<NodeCoord> {
        return board.intersections
            .flatMap { i ->
                listOf(i.coordinate) + HexUtils.getAdjacentIntersections(
                    i.coordinate,
                    board.tiles.map { it.hexCoordinate })
            }.toSet()
    }

    /**
     * Checks if there is an intersection on an intersection coordinate, i.e. if this intersection is occupied.
     */
    fun intersectionAt(coordinate: NodeCoord, intersections: List<Node>): Boolean =
        intersectionAt(coordinate.q, coordinate.r, intersections)


    fun intersectionAt(x: Int, y: Int, intersections: List<Node>): Boolean {
        return intersections.map { i -> i.coordinate }.contains(NodeCoord(x, y))
    }


}
