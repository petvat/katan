package io.github.petvat.katan.shared.model.board

import io.github.petvat.katan.shared.hexlib.*
import io.github.petvat.katan.shared.model.game.ResourceMap
import java.util.List.copyOf

/**
 * Manages all logical operations on board.
 */
class AuthBoardManager(
    val board: InternalBoard
) {
    /**
     * Contains only the intersections that are occupied. Unoccupied intersections are not tracked.
     */
    private val intersections = mutableListOf<Intersection>()
    private val paths = mutableListOf<Edge>()

    /**
     * Make a copy because we have to change this to use doubled coordinate system.
     */
    private var tiles = copyOf(board.tiles)
    //val hexes = tiles.map { it.hexCoordinate }


    var robberLocation = board.robberLocation

    init {

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

    /**
     * Attempts to move robber to tile coordinate.
     * @return true if success
     */
    fun moveRobber(hexCoordinate: HexCoordinates) {
        if (tiles.none { it.hexCoordinate == hexCoordinate } || robberLocation == hexCoordinate) {
            throw IllegalArgumentException("Cannot move robber to coordinate $hexCoordinate")
        } else {
            robberLocation = hexCoordinate
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
     */
    fun getAdjacentBuildings(tile: Tile): Set<Village> {
        val adjacentintersectionCoordiantes = getAdjacentIntersections(tile)
        return intersections
            .filter { i ->
                adjacentintersectionCoordiantes.any { coord -> coord == i.coordinate }
            }
            .map { i -> i.village }.toSet()
    }

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
        return player.inventory.minus(cost)
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
     * Builds settlement at intersection coordinate if valid.
     *
     * @param player builder
     * @param coordinate intersection
     * @param villageKind type of village
     * @throws IllegalArgumentException
     */
    fun buildSettlement(
        player: Player,
        coordinate: ICoordinates,
        villageKind: VillageKind
    ) {
        if (!canBuildVillage(player, coordinate, villageKind)) {
            throw IllegalArgumentException("Invalid build coordinate.")
        } else if (!hasSufficientResources(player, villageKind.cost)) {
            throw IllegalArgumentException("Player does not have sufficient resources.")
        } else {
            intersections.add(
                Intersection(
                    coordinate,
                    Village(villageKind, player)
                )
            )
        }
    }

    /**
     * Checks if there is an intersection on an intersection coordinate, i.e. if this intersection is occupied.
     */
    fun intersectionAt(coordinate: ICoordinates): Boolean = intersectionAt(coordinate.q, coordinate.r)


    fun intersectionAt(x: Int, y: Int): Boolean {
        return intersections.map { i -> i.coordinate }.contains(ICoordinates(x, y))
    }

    private fun getSettlementFrontier(player: Player): Set<ICoordinates> {
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
     * Builds settlement at intersection if valid.
     *
     */
    fun buildSettlementInitial(
        player: Player,
        coordinate: ICoordinates,
        villageKind: VillageKind // Not needed
    ) {
        if (!isValidCoordinate(coordinate) || invalidIntersectionsByDistanceRule().contains(coordinate)) {
            throw IllegalArgumentException("Invalid build coordinate.")
        } else {
            intersections.add(
                Intersection(
                    coordinate,
                    Village(
                        VillageKind.SETTLEMENT,
                        player
                    )
                )
            )
        }
    }

    fun buildRoad(
        player: Player,
        coordinate: EdgeCoordinates,
        roadKind: RoadKind
    ) {
        if (!canBuildRoad(player, coordinate, roadKind)) {
            throw IllegalArgumentException("Invalid build coordinate for ${roadKind.name}.")
        } else if (!hasSufficientResources(player, roadKind.cost)) {
            throw IllegalArgumentException("Not sufficient resources to build ${roadKind.name}.")
        } else {
            paths.add(
                Edge(
                    coordinate,
                    Road(RoadKind.ROAD, player)
                )
            )
        }
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
     * Road frontier will be different for different roads.
     * TODO: fix later
     */
    private fun canBuildRoad(
        player: Player,
        coordinate: EdgeCoordinates,
        roadKind: RoadKind
    ): Boolean {
        return (!paths.map { p -> p.coordinate }.contains(coordinate) ||
            getCoordinatesPathsOwnedBy(player)
                .any { getAdjacentPaths(coordinate).contains(it) }
            )
        // return getRoadFrontier(player).contains(coordinate)
    }


    fun harvestInitialResources() {
        intersections.forEach { intersection ->
            getAdjacentTiles(intersection.coordinate).forEach { tile ->
                tile.resource?.let { intersection.village.harvest(it) }
            }
        }
    }
}


//
//import io.github.petvat.katan.ResourceMap
//import io.github.petvat.katan.board.*
//
//class BoardManager {
//
//    var tiles: MutableList<io.github.petvat.katan.board3.Tile> = mutableListOf()
//    var robberLocation: io.github.petvat.katan.board3.Coordinate = io.github.petvat.katan.board3.Coordinate(0, 0)
//    private var intersections: MutableList<Intersection> = mutableListOf()
//    private var paths: MutableList<io.github.petvat.katan.board3.Path> = mutableListOf()
//
//    /**
//     * Get intersections adjacent to a tile. Will return atmost return 6 intersections.
//     *
//     */
//    fun getAdjacentIntersections(tile: io.github.petvat.katan.board3.Tile): List<io.github.petvat.katan.board3.Coordinate> {
//        val y = tile.coordinate.y
//        val x = tile.coordinate.x
//
//        var yOffset: Int
//        var xOffset: Int
//
//        val offsets = arrayOf(
//            0, 1,
//            2, 3,
//            3, 2,
//            1, 0,
//            0, -1,
//            -1, 0,
//        )
//        return offsets.indices
//            .step(2)
//            .mapNotNull { i ->
//                xOffset = x + i
//                yOffset = y + i + 1
//                io.github.petvat.katan.board3.Coordinate(xOffset, yOffset)
//            }
//    }
//
//    /**
//     * Get adjacent intersections with a building.
//     */
//    fun getAdjacentBuildings(tile: io.github.petvat.katan.board3.Tile): Set<Village> {
//        val adjacentintersectionCoordiantes = getAdjacentIntersections(tile)
//        return intersections
//            .filter { i ->
//                adjacentintersectionCoordiantes.any { coord -> coord == i.coordinate }
//            }
//            .map { i -> i.village }.toSet()
//    }
//
//    /**
//     * Attempts to move robber to tile coordinate.
//     * @return true if success
//     */
//    fun moveRobber(tileCoordinate: io.github.petvat.katan.board3.Coordinate) {
//        if (tiles.none { it.coordinate == tileCoordinate } || robberLocation == tileCoordinate) {
//            throw IllegalArgumentException("Cannot move robber to coordinate $tileCoordinate")
//        } else {
//            robberLocation = tileCoordinate
//        }
//    }
//
//    /**
//     * Get adjacents intersection coordinates to intersection
//     * Usecase: check valid settlement placements
//     */
//    fun getAdjacentIntersections(coordinate: io.github.petvat.katan.board3.Coordinate): List<io.github.petvat.katan.board3.Coordinate> {
//        val adjacents: MutableList<io.github.petvat.katan.board3.Coordinate> = mutableListOf()
//        adjacents.add(io.github.petvat.katan.board3.Coordinate(coordinate.x + 1, coordinate.y + 1))
//        adjacents.add(io.github.petvat.katan.board3.Coordinate(coordinate.x - 1, coordinate.y - 1))
//        if (coordinate.x % 2 == 0) {
//            adjacents.add(io.github.petvat.katan.board3.Coordinate(coordinate.x - 1, coordinate.y + 1))
//        } else {
//            adjacents.add(io.github.petvat.katan.board3.Coordinate(coordinate.x + 1, coordinate.y - 1))
//        }
//        return adjacents.filter { isValidCoordinate(it) }
//    }
//
//    fun getAdjacentTiles(intersectionCoordinate: io.github.petvat.katan.board3.IntersectionCoordinate): List<io.github.petvat.katan.board3.Tile> {
//        val offsets = if (intersectionCoordinate.x % 2 == 0) {
//            arrayOf(
//                -2, -1,
//                0, 1,
//                0, -1
//            )
//        } else {
//            arrayOf(
//                -1, 0,
//                -1, -2,
//                1, 0
//            )
//        }
//        val offsetCoordinates = calculateOffsets(intersectionCoordinate, offsets)
//        return this.tiles.filter { offsetCoordinates.contains(it.coordinate) }
//
//    }
//
//
//    /**
//     * Eller 2d-liste med offset
//     *
//     * Should return something if null
//     *
//     * Caching can be useful.
//     */
//    fun getIntersection(x: Int, y: Int): Intersection? {
//        return getIntersection(io.github.petvat.katan.board3.Coordinate(x, y))
//    }
//
//
//    fun getAdjacentPaths(intersectionCoordinate: io.github.petvat.katan.board3.IntersectionCoordinate): List<io.github.petvat.katan.board3.Coordinate> {
//        val adjacentPathCoordinates: MutableList<io.github.petvat.katan.board3.PathCoordinate> = mutableListOf()
//
//        val offsets = if (intersectionCoordinate.x % 2 == 0) arrayOf(
//            // top
//            0, 0,
//            -1, -1,
//            -1, 0
//        ) else {
//            // bottom
//            arrayOf(
//                0, 0,
//                0, -1,
//                -1, -1
//            )
//        }
//        return calculateOffsets(intersectionCoordinate, offsets).filter { isValidCoordinate(it) }
//    }
//
//    /**
//     * Get Adjacent paths to a path.
//     *
//     * There are 3 offsets. ...
//     */
//    fun getAdjacentPaths(pathCoordinate: io.github.petvat.katan.board3.PathCoordinate): List<io.github.petvat.katan.board3.Coordinate> {
//        // |
//        val horizontalOffsets = arrayOf(
//            -1, 0,
//            0, 1,
//            0, -1,
//            1, 0
//        )
//        // \
//        val downOffsets = arrayOf(
//            -1, 0,
//            -1, -1,
//            1, 0,
//            1, 1
//        )
//        // /
//        val upOffsets = arrayOf(
//            0, 1,
//            1, 1,
//            -1, -1,
//            0, -1
//        )
//
//        val offsets: Array<Int> = if (pathCoordinate.x + pathCoordinate.y % 2 == 0) {
//            // Path is horizontal
//            horizontalOffsets
//        } else if (pathCoordinate.x % 2 == 0) {
//            // Path is downwards
//            downOffsets
//        } else {
//            // path is upwards
//            upOffsets
//        }
//        return calculateOffsets(pathCoordinate, offsets)
//    }
//
//    /**
//     * @param offsets expects array to iterate over in pairs of 2
//     */
//    private fun calculateOffsets(
//        coordinate: io.github.petvat.katan.board3.Coordinate,
//        offsets: Array<Int>
//    ): List<io.github.petvat.katan.board3.Coordinate> {
//        val coordinateOffsets: MutableList<io.github.petvat.katan.board3.Coordinate> = mutableListOf()
//        for (i in offsets.indices.step(2)) {
//            coordinateOffsets.add(
//                io.github.petvat.katan.board3.Coordinate(
//                    coordinate.x + offsets[i],
//                    coordinate.y + offsets[i + 1]
//                )
//            )
//        }
//        return coordinateOffsets
//    }
//
//    // Samtidig kan bruke cache, treng ikkje å oppdatere ofte
//    private fun getSettlementFrontier(player: Player): Set<io.github.petvat.katan.board3.Coordinate> {
//        // cache frontier, update ved ny build event
//        // val playerBuildingCoordinates = intersections.filter { i -> i.hasBuilding && i.building?.owner == player }
//        val frontier: MutableSet<io.github.petvat.katan.board3.Coordinate> = mutableSetOf()
//        val playerRoads = paths
//            .filter { p -> p.road.owner == player }
//
//        // effektiv trealgoritme, DFS - Seinare
//        // start with random
//        val playerRoadCoordinates = playerRoads.map { pr -> pr.coordinate }
//        playerRoadCoordinates.forEach { c ->
//            val vertical = (c.x + c.y) % 2 == 0
//            val adjacentIntersectionCoordinates =
//                if (vertical)
//                    io.github.petvat.katan.board3.Coordinate(
//                        c.x + 1,
//                        c.y
//                    ) to io.github.petvat.katan.board3.Coordinate(c.x, c.y + 1)
//                else
//                    io.github.petvat.katan.board3.Coordinate(
//                        c.x,
//                        c.y
//                    ) to io.github.petvat.katan.board3.Coordinate(c.y + 1, c.y + 1)
//            if (intersectionAt(adjacentIntersectionCoordinates.first) ||
//                intersectionAt(adjacentIntersectionCoordinates.second)
//            ) {
//                return@forEach
//            }
//            adjacentIntersectionCoordinates.toList()
//                .filter { aic -> !frontier.contains(aic) }
//                .forEach { aic ->
//                    if (isValidCoordinate(aic)) {
//                        frontier.add(aic)
//                    }
//                }
//        }
//        return frontier
//    }
//
//    /**
//     * Check intersection coordinate is valid, i.e. within bounds
//     */
//    private fun isValidCoordinate(x: Int, y: Int): Boolean {
//        val offsets = arrayOf(
//            0, 1,
//            -2, -1,
//            0, -1
//        )
//        val adjacentTiles: MutableList<io.github.petvat.katan.board3.Coordinate> = mutableListOf()
//
//        for (i in offsets.indices.step(2)) {
//            if (x % 2 == 0) {
//                adjacentTiles.add(io.github.petvat.katan.board3.Coordinate(x + offsets[i], y + offsets[i + 1]))
//            } else {
//                adjacentTiles.add(io.github.petvat.katan.board3.Coordinate(x + offsets[i + 1], y + offsets[i]))
//            }
//        }
//        return tiles.map { it.coordinate }.any { adjacentTiles.contains(it) }
//    }
//
//    // TODO: Intersection coordinate
//    private fun isValidCoordinate(coordinate: io.github.petvat.katan.board3.Coordinate): Boolean {
//        return isValidCoordinate(io.github.petvat.katan.board3.Coordinate(coordinate.x, coordinate.y))
//    }
//
//    private fun isValidCoordinate(pathCoordinate: io.github.petvat.katan.board3.PathCoordinate): Boolean {
//
//        // |
//        val horizontalOffsets = arrayOf(
//            -1, -1,
//            1, 1
//        )
//        // \
//        val downOffsets = arrayOf(
//            0, -1,
//            0, 1
//        )
//        // upOffsets
//        val upOffsets = arrayOf(
//            -1, 0,
//            1, 0
//        )
//
//
//        val offsets = if (pathCoordinate.x + pathCoordinate.y % 2 == 0) {
//            horizontalOffsets
//        } else if (pathCoordinate.x % 2 == 0) {
//            downOffsets
//        } else {
//            upOffsets
//        }
//        val adjacentTiles = calculateOffsets(pathCoordinate, offsets)
//
//        return tiles.map { t -> t.coordinate }.any { adjacentTiles.contains(it) }
//    }
//
//    fun intersectionAt(coordinate: io.github.petvat.katan.board3.Coordinate): Boolean {
//        return intersectionAt(coordinate.x, coordinate.y)
//    }
//
//    fun intersectionAt(x: Int, y: Int): Boolean {
//        return intersections.map { i -> i.coordinate }.contains(io.github.petvat.katan.board3.Coordinate(x, y))
//    }
//
//    fun getIntersection(coordinate: io.github.petvat.katan.board3.Coordinate): Intersection? {
//        return intersections.find { i -> i.coordinate == coordinate }
//    }
//
//    /**
//     * Get all possible coordinates for placing a road.
//     * @param player Possible coordinates for this player
//     */
//    fun getRoadFrontier(player: Player): Set<io.github.petvat.katan.board3.Coordinate> {
//        // logic:
//        // If sum is even: vertical
//        // If sum is odd: horizontal
//
//        val verticalOffsets = arrayOf(
//            1, 0,
//            0, -1,
//            -1, 0,
//            0, 1
//        )
//        val horizontalOffsets = arrayOf(
//            1, 1,
//            0, -1,
//            -1, -1,
//            0, 1
//        )
//        // set operations
//        // TODO: BOUNDS
//        val ownerCoordinates = paths
//            .filter { p -> p.road.owner == player }
//            .map { p -> p.coordinate }
//            .toSet()
//
//        val frontier: MutableSet<io.github.petvat.katan.board3.Coordinate> = mutableSetOf()
//
//        ownerCoordinates
//            .forEach { c ->
//                val offset: Array<Int>
//                val x = c.x
//                val y = c.y
//                offset = if ((x + y) % 2 == 0) verticalOffsets else horizontalOffsets
//
//                for (i in offset.indices.step(2)) {
//                    val coordinateOffset = io.github.petvat.katan.board3.Coordinate(x + i, y + i + 1)
//                    if (!frontier.contains(coordinateOffset)
//                        && ownerCoordinates.contains(coordinateOffset)
//                    ) {
//                        frontier.add(coordinateOffset)
//                    }
//                }
//            }
//        return frontier.filter { isValidCoordinate(it.x, it.y) }.toSet()
//    }
//
//    /**
//     * Check that intersection coordinate is valid build spot, by checking bounds
//     * occupied and distance rule.
//     */
////    private fun canBuildSettlement(player: Player, coordinate: Coordinate): Boolean {
////        return isValidCoordinate(coordinate.x, coordinate.y) &&
////            !intersectionAt(coordinate) &&
////            intersections.map { it.coordinate }
////                .any {
////                    it in getAdjacentIntersections(coordinate)
////                }
////    }
//
//
//    private fun hasSufficientResources(
//        player: Player,
//        cost: ResourceMap
//    ): Boolean {
//        return player.inventory.minus(cost)
//    }
//
//    private fun getCoordinatesPathsOwnedBy(player: Player): List<io.github.petvat.katan.board3.PathCoordinate> {
//        return paths.filter { p -> p.road.owner == player }
//            .map { p -> p.coordinate as io.github.petvat.katan.board3.PathCoordinate }.toList()
//    }
//
//    /**
//     * Check if a building on this intersection would violate distance rule.
//     *
//     * @param intersectionCoordinate to check
//     * @return true if comply distance rule
//     */
//    private fun distanceRule(intersectionCoordinate: io.github.petvat.katan.board3.IntersectionCoordinate): Boolean {
//        return intersections
//            .map { i -> i.coordinate }
//            .any { it in getAdjacentIntersections(intersectionCoordinate) }
//    }
//
//    private fun invalidIntersectionsByDistanceRule(): Set<io.github.petvat.katan.board3.Coordinate> {
//        return intersections
//            .flatMap { i ->
//                listOf(i.coordinate) + getAdjacentIntersections(i.coordinate)
//            }.toSet()
//    }
//
//    /**
//     * Checks whether coordinate is valid.
//     */
//    private fun canBuildSettlement(
//        player: Player,
//        coordinate: io.github.petvat.katan.board3.Coordinate
//    ): Boolean {
//        // road into intersection owned player and follows distance rule
//        return (getCoordinatesPathsOwnedBy(player).any {
//            getAdjacentPaths(coordinate as io.github.petvat.katan.board3.IntersectionCoordinate).contains(it)
//        } && !invalidIntersectionsByDistanceRule().contains(coordinate))
//    }
//
//    /**
//     * Builds settlement at intersection coordinate if valid.
//     *
//     * @param player builder
//     * @param coordinate intersection
//     * @param villageKind type of village
//     * @throws IllegalArgumentException
//     */
//    fun buildSettlement(
//        player: Player,
//        coordinate: io.github.petvat.katan.board3.Coordinate,
//        villageKind: VillageKind
//    ) {
//        if (!canBuildVillage(player, coordinate, villageKind)) {
//            throw IllegalArgumentException("Invalid build coordinate.")
//        } else if (!hasSufficientResources(player, villageKind.cost)) {
//            throw IllegalArgumentException("Player does not have sufficient resources.")
//        } else {
//            intersections.add(
//                Intersection(
//                    coordinate,
//                    Village(villageKind, player)
//                )
//            )
//        }
//    }
//
//    /**
//     * Builds settlement at intersection if valid.
//     *
//     */
//    fun buildSettlementInitial(
//        player: Player,
//        coordinate: io.github.petvat.katan.board3.Coordinate,
//        villageKind: VillageKind
//    ) {
//        if (!isValidCoordinate(coordinate) || invalidIntersectionsByDistanceRule().contains(coordinate)) {
//            throw IllegalArgumentException("Invalid build coordinate.")
//        } else {
//            intersections.add(
//                Intersection(
//                    coordinate,
//                    Village(
//                        VillageKind.SETTLEMENT,
//                        player
//                    )
//                )
//            )
//        }
//    }
//
//    fun buildRoad(
//        player: Player,
//        coordinate: io.github.petvat.katan.board3.Coordinate,
//        roadKind: RoadKind
//    ) {
//        if (!canBuildRoad(player, coordinate, roadKind)) {
//            throw IllegalArgumentException("Invalid build coordinate for ${roadKind.name}.")
//        } else if (!hasSufficientResources(player, roadKind.cost)) {
//            throw IllegalArgumentException("Not sufficient resources to build ${roadKind.name}.")
//        } else {
//            paths.add(
//                io.github.petvat.katan.board3.Path(
//                    coordinate,
//                    Road(RoadKind.ROAD, player)
//                )
//            )
//        }
//    }
//
//    /**
//     * Checks whether player can build a city on coordinate.
//     * The intersection needs to have a settlement.
//     */
//    private fun canBuildCity(
//        player: Player,
//        coordinate: io.github.petvat.katan.board3.Coordinate
//    ): Boolean {
//        return intersections
//            .filter { i -> i.village.owner == player && i.village.villageKind == VillageKind.CITY }
//            .map { i -> i.coordinate }
//            .contains(coordinate)
//    }
//
//    private fun canBuildVillage(
//        player: Player,
//        coordinate: io.github.petvat.katan.board3.Coordinate,
//        villageKind: VillageKind
//    ): Boolean {
//        return when (villageKind) {
//            VillageKind.SETTLEMENT -> canBuildSettlement(player, coordinate)
//            VillageKind.CITY -> canBuildCity(player, coordinate)
//            else -> false
//        }
//    }
//
//    /**
//     * Road frontier will be different for different roads.
//     * TODO: fix later
//     */
//    private fun canBuildRoad(
//        player: Player,
//        coordinate: io.github.petvat.katan.board3.Coordinate,
//        roadKind: RoadKind
//    ): Boolean {
//        return (!paths.map { p -> p.coordinate }.contains(coordinate) ||
//            getCoordinatesPathsOwnedBy(player)
//                .any { getAdjacentPaths(coordinate as io.github.petvat.katan.board3.PathCoordinate).contains(it) }
//            )
//        // return getRoadFrontier(player).contains(coordinate)
//    }
//
//
//    fun harvestInitialResources() {
//        intersections.forEach { intersection ->
//            getAdjacentTiles(intersection as io.github.petvat.katan.board3.IntersectionCoordinate).forEach { tile ->
//                tile.resource?.let { intersection.village.harvest(it) }
//            }
//        }
//    }
//}
