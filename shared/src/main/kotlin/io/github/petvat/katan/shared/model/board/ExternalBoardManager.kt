package io.github.petvat.katan.shared.model.board

import io.github.petvat.katan.shared.hexlib.*
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.protocol.dto.BoardView

open class ExternalBoardManager(
    val board: BoardView
) {
    /**
     * Contains only the intersections that are occupied. Unoccupied intersections are not tracked.
     */
    private val intersections = mutableListOf<Intersection>()
    private val paths = mutableListOf<Edge>()
    private var tiles = board.tiles

    var robberLocation = board.robberLocation

    init {
        val hexes = tiles.map { it.hexCoordinate }

        // Transform tiles to doubled. We need to do this to work with edges/intersections.
        tiles = tiles
            .map {
                Tile(
                    HexUtils.transformToDoubled(it.hexCoordinate),
                    it.resource,
                    it.rollListenValue
                )
            }.toMutableList()
    }


    fun getSettlementFrontier(player: Player): Set<ICoordinates> {
        // cache frontier, update ved ny build event
        // val playerBuildingCoordinates = intersections.filter { i -> i.hasBuilding && i.building?.owner == player }
        val frontier: MutableSet<ICoordinates> = mutableSetOf()
        val playerRoads = paths
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
            if (intersectionAt(adjacentIntersectionCoordinates.first) ||
                intersectionAt(adjacentIntersectionCoordinates.second)
            ) {
                return@forEach
            }
            adjacentIntersectionCoordinates.toList()
                .filter { aic -> !frontier.contains(aic) }
                .forEach { aic ->
                    if (isValidCoordinate(aic)) {
                        frontier.add(aic)
                    }
                }
        }
        return frontier
    }

    /**
     * Get all possible coordinates for placing a road.
     * @param player With respect to this player
     */
    fun getRoadFrontier(player: Player): Set<EdgeCoordinates> {
        // logic:
        // If sum is even: vertical
        // If sum is odd: horizontal

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
        val ownerCoordinates = paths
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
        return frontier.filter { isValidCoordinate(it) }.toSet()
    }

    /**
     * Checks whether player can build a city on coordinate.
     * The intersection needs to have a settlement.
     */
    private fun canBuildCity(
        player: Player,
        coordinate: ICoordinates
    ): Boolean {
        return intersections
            .filter { i -> i.village.owner == player && i.village.villageKind == VillageKind.CITY }
            .map { i -> i.coordinate }
            .contains(coordinate)
    }

    private fun canBuildVillage(
        player: Player,
        coordinate: ICoordinates,
        villageKind: VillageKind
    ): Boolean {
        return when (villageKind) {
            VillageKind.SETTLEMENT -> canBuildSettlement(player, coordinate)
            VillageKind.CITY -> canBuildCity(player, coordinate)
        }
    }

    /**
     * Checks if an intersection coordinate exists on the board.
     */
    private fun isValidCoordinate(icoord: ICoordinates): Boolean {
        val offsets = arrayOf(
            0, 1,
            -2, -1,
            0, -1
        )
        val adjacentTiles: MutableList<HexCoordinates> = mutableListOf()

        for (i in offsets.indices.step(2)) {
            if (icoord.q % 2 == 0) {
                adjacentTiles.add(HexCoordinates(icoord.q + offsets[i], icoord.r + offsets[i + 1]))
            } else {
                adjacentTiles.add(HexCoordinates(icoord.q + offsets[i + 1], icoord.r + offsets[i]))
            }
        }
        return tiles.map { it.hexCoordinate }.any { adjacentTiles.contains(it) }
    }

    private fun isValidCoordinate(ecoord: EdgeCoordinates): Boolean {

        // |
        val horizontalOffsets = arrayOf(
            -1, -1,
            1, 1
        )
        // \
        val downOffsets = arrayOf(
            0, -1,
            0, 1
        )
        // upOffsets
        val upOffsets = arrayOf(
            -1, 0,
            1, 0
        )


        val offsets = if (ecoord.q + ecoord.r % 2 == 0) {
            horizontalOffsets
        } else if (ecoord.q % 2 == 0) {
            downOffsets
        } else {
            upOffsets
        }
        val adjacentTiles = calculateOffsets(ecoord, offsets).map { (q, r) -> HexCoordinates(q, r) }

        return tiles.map { t -> t.hexCoordinate }.any { adjacentTiles.contains(it) }
    }


    /**
     * Get adjacent intersections with a building.
     *
     * TODO: REMOVE
     */
//    fun getAdjacentBuildings(tile: Tile): Set<Village> {
//        val adjacentintersectionCoordiantes = getAdjacentIntersections(tile)
//        return intersections
//            .filter { i ->
//                adjacentintersectionCoordiantes.any { coord -> coord == i.coordinate }
//            }
//            .map { i -> i.village }.toSet()
//    }

    private fun getAdjacentIntersections(tile: Tile): List<ICoordinates> {
        return HexUtils.adjacentIntersections(tile.hexCoordinate)
    }

    /**
     * Get adjacents intersection coordinates to intersection
     * Usecase: check valid settlement placements
     */
    private fun getAdjacentIntersections(coord: ICoordinates): List<ICoordinates> {
        val adjacents: MutableList<ICoordinates> = mutableListOf()
        adjacents.add(ICoordinates(coord.q + 1, coord.r + 1))
        adjacents.add(ICoordinates(coord.q - 1, coord.r - 1))
        if (coord.q % 2 == 0) {
            adjacents.add(ICoordinates(coord.q - 1, coord.r + 1))
        } else {
            adjacents.add(ICoordinates(coord.q + 1, coord.r - 1))
        }
        return adjacents.filter { isValidCoordinate(it) }
    }

    private fun getAdjacentTiles(intersectionCoordinate: ICoordinates): List<Tile> {
        val hexes = HexUtils.adjacentHexes(intersectionCoordinate)
        return tiles.filter { h -> h.hexCoordinate in hexes }
    }


    private fun hasSufficientResources(
        player: Player,
        cost: ResourceMap
    ): Boolean {
        return player.inventory.difference(cost).get()
            .any { (_, x) -> x < 0 }
    }

    private fun getCoordinatesPathsOwnedBy(player: Player): List<EdgeCoordinates> {
        return paths.filter { p -> p.road.owner == player }
            .map { p -> p.coordinate }.toList()
    }

    /**
     * Get adjacent edges to an intersection.
     */
    private fun getAdjacentPaths(icoord: ICoordinates): List<EdgeCoordinates> {

        val offsets = if (icoord.q % 2 == 0) arrayOf(
            // top
            0, 0,
            -1, -1,
            -1, 0
        ) else {
            // bottom
            arrayOf(
                0, 0,
                0, -1,
                -1, -1
            )
        }
        return calculateOffsets(icoord, offsets).map { (q, r) -> EdgeCoordinates(q, r) }
            .filter { isValidCoordinate(it) }
    }

    /**
     * @param offsets expects array to iterate over in pairs of 2
     */
    private fun calculateOffsets(
        coordinate: Coordinates,
        offsets: Array<Int>,

        ): List<Pair<Int, Int>> {
        val coordinateOffsets: MutableList<Pair<Int, Int>> = mutableListOf()
        for (i in offsets.indices.step(2)) {
            coordinateOffsets.add(
                Pair(
                    coordinate.q + offsets[i],
                    coordinate.r + offsets[i + 1]
                )
            )
        }
        return coordinateOffsets
    }


    /**
     * Get adjacent edges to an edge.
     */
    private fun getAdjacentPaths(ecoord: EdgeCoordinates): List<EdgeCoordinates> {

        // |
        val horizontalOffsets = arrayOf(
            -1, 0,
            0, 1,
            0, -1,
            1, 0
        )
        // \
        val downOffsets = arrayOf(
            -1, 0,
            -1, -1,
            1, 0,
            1, 1
        )
        // /
        val upOffsets = arrayOf(
            0, 1,
            1, 1,
            -1, -1,
            0, -1
        )

        val offsets: Array<Int> = if (ecoord.q + ecoord.r % 2 == 0) {
            // Path is horizontal
            horizontalOffsets
        } else if (ecoord.q % 2 == 0) {
            // Path is downwards
            downOffsets
        } else {
            // path is upwards
            upOffsets
        }
        return calculateOffsets(ecoord, offsets).map { (q, r) -> EdgeCoordinates(q, r) }
    }

    /**
     * Check if a building on this intersection would violate distance rule.
     *
     * @param intersectionCoordinate to check
     * @return true if comply distance rule
     */
    private fun distanceRule(intersectionCoordinate: ICoordinates): Boolean {
        return intersections
            .map { i -> i.coordinate }
            .any { it in getAdjacentIntersections(intersectionCoordinate) }
    }

    private fun invalidIntersectionsByDistanceRule(): Set<ICoordinates> {
        return intersections
            .flatMap { i ->
                listOf(i.coordinate) + getAdjacentIntersections(i.coordinate)
            }.toSet()
    }

    /**
     * Checks whether coordinate is valid.
     */
    private fun canBuildSettlement(
        player: Player,
        coordinate: ICoordinates
    ): Boolean {
        // road into intersection owned player and follows distance rule
        return (getCoordinatesPathsOwnedBy(player).any {
            getAdjacentPaths(coordinate).contains(it)
        } && !invalidIntersectionsByDistanceRule().contains(coordinate))
    }

    /**
     * Checks if there is an intersection on an intersection coordinate, i.e. if this intersection is occupied.
     */
    fun intersectionAt(coordinate: ICoordinates): Boolean = intersectionAt(coordinate.q, coordinate.r)


    fun intersectionAt(x: Int, y: Int): Boolean {
        return intersections.map { i -> i.coordinate }.contains(ICoordinates(x, y))
    }


    /**
     * Road frontier will be different for different roads.
     * TODO: fix later
     */
    private fun canBuildRoad(
        player: Player,
        coordinate: EdgeCoordinates,
        roadKind: RoadKind
    ): Boolean {
        return (!paths.map { p -> p.coordinate }.contains(coordinate)
            ||
            (getCoordinatesPathsOwnedBy(player)
                .any { getAdjacentPaths(coordinate).contains(it) }
                ) && hasSufficientResources(player, roadKind.cost))
        // return getRoadFrontier(player).contains(coordinate)
    }
}
