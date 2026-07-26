package io.github.petvat.katan.model

import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.HexUtils
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.*


// FIXME: DUPLICATE IN SERVER. Below updateSetupFrontier
object RuleEngine {


    fun updateSetupFrontier(player: Int, board: Board): Set<ICoordinates> {
        return board.tiles
            .flatMap { HexUtils.adjacentIntersections(it.hexCoordinate) }
            .toSet()
            .minus(
                invalidIntersectionsByDistanceRule(player, board)
            )
    }

    fun updateCityFrontier(player: Int, board: Board): Set<ICoordinates> {
        return board.intersections
            .filter { it.village.owner == player && it.village.villageKind == VillageKind.SETTLEMENT }
            .map { it.coordinate }
            .toSet()
    }

    fun updateSettlementFrontier(
        player: Int,
        board: Board
    )
        : Set<ICoordinates> {
        // cache frontier, update ved ny build event
        // val playerBuildingCoordinates = intersections.filter { i -> i.hasBuilding && i.building?.owner == player }
        val frontier: MutableSet<ICoordinates> = mutableSetOf()
        val playerRoads = board.paths
            .filter { p -> p.road.owner == player }

        // effektiv trealgoritme, DFS - Seinare
        // start with random
        val playerRoadCoordinates = playerRoads.map { pr -> pr.coordinate }
        playerRoadCoordinates.forEach { c ->
            val vertical = (c.q + c.r) % 2 == 0
            val adjacentIntersectionCoordinates =
                if (vertical)
                    ICoordinates(
                        c.q + 1,
                        c.r
                    ) to ICoordinates(c.q, c.r + 1)
                else
                    ICoordinates(
                        c.q,
                        c.r
                    ) to ICoordinates(c.q + 1, c.r + 1)
            if (intersectionAt(adjacentIntersectionCoordinates.first, board.intersections.toList()) ||
                intersectionAt(adjacentIntersectionCoordinates.second, board.intersections.toList())
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
    fun updateRoadFrontier(player: Int, board: Board): Set<EdgeCoordinates> {
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

        val frontier: MutableSet<EdgeCoordinates> = mutableSetOf()

        ownerCoordinates
            .forEach { c ->
                val offset: Array<Int>
                val x = c.q
                val y = c.r
                offset = if ((x + y) % 2 == 0) verticalOffsets else horizontalOffsets

                for (i in offset.indices.step(2)) {
                    val coordinateOffset = EdgeCoordinates(x + i, y + i + 1)
                    if (!frontier.contains(coordinateOffset)
                        && ownerCoordinates.contains(coordinateOffset)
                    ) {
                        frontier.add(coordinateOffset)
                    }
                }
            }
        return frontier.filter { HexUtils.isValidCoordinate(it, board.tiles.map { t -> t.hexCoordinate }) }.toSet()
    }


    /**
     * Checks whether player can build a city on coordinate.
     * The intersection needs to have a settlement.
     */
    private fun canBuildCity(
        player: Int,
        coordinate: ICoordinates,
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
        coordinate: ICoordinates,
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
        coordinate: EdgeCoordinates,
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


    fun getPathsOwnedBy(player: Int, paths: List<Edge>): List<EdgeCoordinates> {
        return paths.filter { p -> p.road.owner == player }
            .map { p -> p.coordinate }.toList()
    }

    /**
     * Check if a building on this intersection would violate distance rule.
     *
     * @param intersectionCoordinate to check
     * @return true if comply distance rule
     */
    fun distanceRule(intersectionCoordinate: ICoordinates, board: Board): Boolean {
        return board.intersections
            .map { i -> i.coordinate }
            .any {
                it in HexUtils.getAdjacentIntersections(
                    intersectionCoordinate,
                    board.tiles.map { t -> t.hexCoordinate })
            }
    }

    fun invalidIntersectionsByDistanceRule(player: Int, board: Board): Set<ICoordinates> {
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
    fun intersectionAt(coordinate: ICoordinates, intersections: List<Intersection>): Boolean =
        intersectionAt(coordinate.q, coordinate.r, intersections)


    fun intersectionAt(x: Int, y: Int, intersections: List<Intersection>): Boolean {
        return intersections.map { i -> i.coordinate }.contains(ICoordinates(x, y))
    }


}
