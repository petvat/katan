package io.github.petvat.katan.server.service.engine.board

import io.github.petvat.katan.server.service.engine.EngineResult
import io.github.petvat.katan.server.service.engine.Player
import io.github.petvat.katan.shared.hexlib.*
import io.github.petvat.katan.shared.model.board.*
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.RuleBook
import java.util.List.copyOf

/**
 *
 * TODO: Generalize and share!
 * BoardManager is responsible for the spatial and positional logic of the board.
 *
 */
class BoardManager(ruleBook: RuleBook) {

    fun countVictoryPoints(board: Board): Map<Int, Int> {
        val vps = mutableMapOf<Int, Int>()
        board.intersections.forEach {
            val village = it.village
            val owner = village.owner
            val vp = village.villageKind.vp
            vps[owner] = (vps[owner] ?: 0) + vp
        }
        return vps
    }

    private fun isValidCoordinate(board: Board, ecoord: EdgeCoord): Boolean {

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
        val adjacentTiles = calculateOffsets(ecoord, offsets).map { (q, r) -> HexCoord(q, r) }

        return board.tiles.map { t -> t.hexCoordinate }.any { adjacentTiles.contains(it) }
    }

    /**
     * Get adjacents intersection coordinates to intersection
     * Usecase: check valid settlement placements
     */
    private fun getAdjacentIntersections(board: Board, coord: NodeCoord): List<NodeCoord> {
        val adjacents: MutableList<NodeCoord> = mutableListOf()
        adjacents.add(NodeCoord(coord.q + 1, coord.r + 1))
        adjacents.add(NodeCoord(coord.q - 1, coord.r - 1))
        if (coord.q % 2 == 0) {
            adjacents.add(NodeCoord(coord.q - 1, coord.r + 1))
        } else {
            adjacents.add(NodeCoord(coord.q + 1, coord.r - 1))
        }
        return adjacents.filter { isValidCoordinate(board, it) }
    }

    private fun isValidCoordinate(board: Board, icoord: NodeCoord): Boolean {
        val offsets = arrayOf(
            0, 1,
            -2, -1,
            0, -1
        )
        val adjacentTiles: MutableList<HexCoord> = mutableListOf()

        for (i in offsets.indices.step(2)) {
            if (icoord.q % 2 == 0) {
                adjacentTiles.add(HexCoord(icoord.q + offsets[i], icoord.r + offsets[i + 1]))
            } else {
                adjacentTiles.add(HexCoord(icoord.q + offsets[i + 1], icoord.r + offsets[i]))
            }
        }
        return board.tiles.map { it.hexCoordinate }.any { adjacentTiles.contains(it) }
    }

    private fun getCoordinatesPathsOwnedBy(board: Board, player: Int): List<EdgeCoord> {
        return board.paths.filter { p -> p.road.owner == player }
            .map { p -> p.coordinate }.toList()
    }

    /**
     * Get adjacent edges to an intersection.
     */
    private fun getAdjacentPaths(board: Board, icoord: NodeCoord): List<EdgeCoord> {
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
        return calculateOffsets(icoord, offsets).map { (q, r) -> EdgeCoord(q, r) }
            .filter { isValidCoordinate(board, it) }
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
    private fun getAdjacentPaths(ecoord: EdgeCoord): List<EdgeCoord> {

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
        return calculateOffsets(ecoord, offsets).map { (q, r) -> EdgeCoord(q, r) }
    }

    /**
     * Check if a building on this intersection would violate distance rule.
     *
     * @param intersectionCoordinate to check
     * @return true if comply distance rule
     */
    fun distanceRule(board: Board, intersectionCoordinate: NodeCoord): Boolean {
        // TODO: Make dynamic
        return board.intersections
            .map { i -> i.coordinate }
            .any { it in getAdjacentIntersections(board, intersectionCoordinate) }
    }

    fun invalidIntersectionsByDistanceRule(board: Board): Set<NodeCoord> {
        return board.intersections
            .flatMap { i ->
                listOf(i.coordinate) + getAdjacentIntersections(board, i.coordinate)
            }.toSet()
    }


    fun canBuildSettlement(
        board: Board,
        player: Int,
        coordinate: NodeCoord
    ): Boolean {
        // road into intersection owned by player and follows distance rule
        return (getCoordinatesPathsOwnedBy(board, player).any {
            getAdjacentPaths(board, coordinate).contains(it)
        } && !invalidIntersectionsByDistanceRule(board).contains(coordinate))
    }

    /**
     * Checks whether player can build a city on coordinate.
     * The intersection needs to have a settlement.
     */
    private fun canBuildCity(
        board: Board,
        player: Int,
        coordinate: NodeCoord
    ): Boolean {
        return board.intersections
            .filter { i -> i.village.owner == player && i.village.villageKind == VillageKind.CITY }
            .map { i -> i.coordinate }
            .contains(coordinate)
    }

    fun canBuildVillage(
        board: Board,
        player: Int,
        coordinate: NodeCoord,
        villageKind: VillageKind
    ): Boolean {

        return when (villageKind) {
            VillageKind.SETTLEMENT -> canBuildSettlement(board, player, coordinate)
            VillageKind.CITY -> canBuildCity(board, player, coordinate)
        }
    }

    /**
     * Road frontier will be different for different roads.
     * TODO: fix later
     */
    fun canBuildRoad(
        board: Board,
        player: Int,
        coordinate: EdgeCoord,
        roadKind: RoadKind
    ): Boolean {
        return (!board.paths.map { p -> p.coordinate }.contains(coordinate) ||
            getCoordinatesPathsOwnedBy(board, player)
                .any { getAdjacentPaths(coordinate).contains(it) }
            )
        // return getRoadFrontier(player).contains(coordinate)
    }

    fun getAdjacentIntersections(tile: Tile): List<NodeCoord> {
        return HexUtils.adjacentIntersections(tile.hexCoordinate)
    }

    fun getAdjacentBuildings(board: Board, tile: Tile): Set<Village> {
        val adjacentintersectionCoordiantes = getAdjacentIntersections(tile)
        return board.intersections
            .filter { i ->
                adjacentintersectionCoordiantes.any { coord -> coord == i.coordinate }
            }
            .map { i -> i.village }.toSet()
    }

    fun getAdjacentTiles(board: Board, intersectionCoordinate: NodeCoord): List<Tile> {
        val hexes = HexUtils.hexesTouchingNode(intersectionCoordinate)
        return board.tiles.filter { h -> h.hexCoordinate in hexes }
    }

    /**
     * Builds settlement at intersection coordinate if valid.
     */
    fun buildInitialVillage(
        board: Board,
        player: Int,
        coordinate: NodeCoord,
    ): EngineResult<Board> {
        if (coordinate in invalidIntersectionsByDistanceRule(board)) {
            return EngineResult.Failure("Invalid build coordinate.")
        }

        return EngineResult.of(
            board.copy(
                intersections = board.intersections + Node(
                    coordinate,
                    Village(VillageKind.SETTLEMENT, player)
                )
            )
        )


    }

    /**
     * Builds settlement at intersection coordinate if valid.
     *
     * @param player builder
     * @param coordinate intersection
     * @param villageKind kind of village
     * @throws IllegalArgumentException
     */
    fun buildVillage(
        board: Board,
        player: Int,
        coordinate: NodeCoord,
        villageKind: VillageKind
    ): EngineResult<Board> {
        if (!canBuildVillage(board, player, coordinate, villageKind))
            return EngineResult.Failure("Invalid build coordinate.")

        return EngineResult.of(
            board.copy(
                intersections = board.intersections + Node(
                    coordinate,
                    Village(villageKind, player)
                )
            )
        )

    }

    fun buildRoad(
        board: Board,
        player: Int,
        coordinate: EdgeCoord,
        roadKind: RoadKind
    ): EngineResult<Board> {
        if (!canBuildRoad(board, player, coordinate, roadKind))
            return EngineResult.Failure("Not valid build place.")

        return EngineResult.Success(
            board.copy(
                paths = board.paths + Edge(
                    coordinate,
                    Road(roadKind, player)
                )
            )
        )
    }

    /**
     * Attempts to move robber to tile coordinate.
     */
    fun moveRobber(board: Board, hexCoordinate: HexCoord): EngineResult<Board> {
        if (board.tiles.none { it.hexCoordinate == hexCoordinate } || board.robberLocation == hexCoordinate) {
            return EngineResult.Failure("Cannot move robber to $hexCoordinate.")
        }
        return EngineResult.Success(board.copy(robberLocation = hexCoordinate))
    }
}


/**
 *
 * Manages all logical operations on board.
 */
class BoardManagerDEPR(
    val board: Board
) {
    /**
     * Contains only the intersections that are occupied. Unoccupied intersections are not tracked.
     */
    val intersections = mutableListOf<Node>()

    private val paths = mutableListOf<Edge>()

    /**
     * Make a copy because we have to change this to use doubled coordinate system.
     */
    private var tiles = copyOf(board.tiles)

    // Transform tiles to doubled. We need to do this to work with edges/intersections.
//    private val tilesDoubled = tiles
//        .map {
//            Tile(
//                HexUtils.transformToDoubled(it.hexCoordinate),
//                it.resource,
//                it.rollListenValue
//            )
//        }.toMutableList()

    var robberLocation = board.robberLocation

    /**
     * Attempts to move robber to tile coordinate.
     *
     * @return true if success
     */
    fun moveRobber(hexCoordinate: HexCoord): Boolean {
        if (tiles.none { it.hexCoordinate == hexCoordinate } || robberLocation == hexCoordinate) {
            return false
        } else {
            robberLocation = hexCoordinate
        }
        return true
    }


    /**
     * Checks if an intersection coordinate exists on the board.
     */
    private fun isValidCoordinate(icoord: NodeCoord): Boolean {
        val offsets = arrayOf(
            0, 1,
            -2, -1,
            0, -1
        )
        val adjacentTiles: MutableList<HexCoord> = mutableListOf()

        for (i in offsets.indices.step(2)) {
            if (icoord.q % 2 == 0) {
                adjacentTiles.add(HexCoord(icoord.q + offsets[i], icoord.r + offsets[i + 1]))
            } else {
                adjacentTiles.add(HexCoord(icoord.q + offsets[i + 1], icoord.r + offsets[i]))
            }
        }
        return tiles.map { it.hexCoordinate }.any { adjacentTiles.contains(it) }
    }

    private fun isValidCoordinate(ecoord: EdgeCoord): Boolean {

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
        val adjacentTiles = calculateOffsets(ecoord, offsets).map { (q, r) -> HexCoord(q, r) }

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

    private fun getAdjacentIntersections(tile: Tile): List<NodeCoord> {
        return HexUtils.adjacentIntersections(tile.hexCoordinate)
    }

    /**
     * Get adjacents intersection coordinates to intersection
     * Usecase: check valid settlement placements
     */
    private fun getAdjacentIntersections(coord: NodeCoord): List<NodeCoord> {
        val adjacents: MutableList<NodeCoord> = mutableListOf()
        adjacents.add(NodeCoord(coord.q + 1, coord.r + 1))
        adjacents.add(NodeCoord(coord.q - 1, coord.r - 1))
        if (coord.q % 2 == 0) {
            adjacents.add(NodeCoord(coord.q - 1, coord.r + 1))
        } else {
            adjacents.add(NodeCoord(coord.q + 1, coord.r - 1))
        }
        return adjacents.filter { isValidCoordinate(it) }
    }

    fun getAdjacentTiles(intersectionCoordinate: NodeCoord): List<Tile> {
        val hexes = HexUtils.hexesTouchingNode(intersectionCoordinate)
        return tiles.filter { h -> h.hexCoordinate in hexes }
    }


//    private fun hasSufficientResources(
//        player: Player,
//        cost: ResourceMap
//    ): Boolean {
//        return player.resources.minus(cost)
//    }

    private fun getCoordinatesPathsOwnedBy(player: Player): List<EdgeCoord> {
        return paths.filter { p -> p.road.owner == player.number }
            .map { p -> p.coordinate }.toList()
    }

    /**
     * Get adjacent edges to an intersection.
     */
    private fun getAdjacentPaths(icoord: NodeCoord): List<EdgeCoord> {

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
        return calculateOffsets(icoord, offsets).map { (q, r) -> EdgeCoord(q, r) }
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
    private fun getAdjacentPaths(ecoord: EdgeCoord): List<EdgeCoord> {

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
        return calculateOffsets(ecoord, offsets).map { (q, r) -> EdgeCoord(q, r) }
    }

    /**
     * Check if a building on this intersection would violate distance rule.
     *
     * @param intersectionCoordinate to check
     * @return true if comply distance rule
     */
    private fun distanceRule(intersectionCoordinate: NodeCoord): Boolean {
        return intersections
            .map { i -> i.coordinate }
            .any { it in getAdjacentIntersections(intersectionCoordinate) }
    }

    private fun invalidIntersectionsByDistanceRule(): Set<NodeCoord> {
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
        coordinate: NodeCoord
    ): Boolean {
        // road into intersection owned player and follows distance rule
        return (getCoordinatesPathsOwnedBy(player).any {
            getAdjacentPaths(coordinate).contains(it)
        } && !invalidIntersectionsByDistanceRule().contains(coordinate))
    }
//
//    /**
//     * Builds settlement at intersection coordinate if valid.
//     *
//     * TODO: Instead of throwing, just return boolean.
//     *
//     * @param player builder
//     * @param coordinate intersection
//     * @param villageKind type of village
//     * @throws IllegalArgumentException
//     */
//    fun buildSettlement(
//        player: Player,
//        coordinate: NodeCoord,
//        villageKind: VillageKind
//    ) {
//        if (!canBuildVillage(player, coordinate, villageKind)) {
//            throw IllegalArgumentException("Invalid build coordinate.")
//        } else if (!hasSufficientResources(player, villageKind.cost)) {
//            throw IllegalArgumentException("Player does not have sufficient resources.")
//        } else {
//            intersections.add(
//                Node(
//                    coordinate,
//                    Village(villageKind, player.number)
//                )
//            )
//            // Update victory points
//            player.victoryPoints += VillageKind.SETTLEMENT.vp
//        }
//    }
//
//    /**
//     * Checks if there is an intersection on an intersection coordinate, i.e. if this intersection is occupied.
//     */
//    fun intersectionAt(coordinate: NodeCoord): Boolean = intersectionAt(coordinate.q, coordinate.r)
//
//    fun intersectionAt(x: Int, y: Int): Boolean {
//        return intersections.map { i -> i.coordinate }.contains(NodeCoord(x, y))
//    }
//
//    // TODO: REMOVE or somethng.
//    private fun getSettlementFrontier(player: Player): Set<NodeCoord> {
//        // cache frontier, update ved ny build event
//        // val playerBuildingCoordinates = intersections.filter { i -> i.hasBuilding && i.building?.owner == player }
//        val frontier: MutableSet<NodeCoord> = mutableSetOf()
//        val playerRoads = paths
//            .filter { p -> p.road.owner == player.number }
//
//        // effektiv trealgoritme, DFS - Seinare
//        // start with random
//        val playerRoadCoordinates = playerRoads.map { pr -> pr.coordinate }
//        playerRoadCoordinates.forEach { c ->
//            val vertical = (c.q + c.r) % 2 == 0
//            val adjacentIntersectionCoordinates =
//                if (vertical)
//                    NodeCoord(
//                        c.q + 1,
//                        c.r
//                    ) to NodeCoord(c.q, c.r + 1)
//                else
//                    NodeCoord(
//                        c.q,
//                        c.r
//                    ) to NodeCoord(c.q + 1, c.r + 1)
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
//     * TODO: Remove
//     *
//     * Get all possible coordinates for placing a road.
//     * @param player With respect to this player
//     */
//    fun getRoadFrontier(player: Player): Set<EdgeCoord> {
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
//            .filter { p -> p.road.owner == player.number }
//            .map { p -> p.coordinate }
//            .toSet()
//
//        val frontier: MutableSet<EdgeCoord> = mutableSetOf()
//
//        ownerCoordinates
//            .forEach { c ->
//                val offset: Array<Int>
//                val x = c.q
//                val y = c.r
//                offset = if ((x + y) % 2 == 0) verticalOffsets else horizontalOffsets
//
//                for (i in offset.indices.step(2)) {
//                    val coordinateOffset = EdgeCoord(x + i, y + i + 1)
//                    if (!frontier.contains(coordinateOffset)
//                        && ownerCoordinates.contains(coordinateOffset)
//                    ) {
//                        frontier.add(coordinateOffset)
//                    }
//                }
//            }
//        return frontier.filter { isValidCoordinate(it) }.toSet()
//    }

    /**
     * Builds settlement at intersection if valid.
     *
     */
    fun buildSettlementInitial(
        player: Player,
        coordinate: NodeCoord,
        villageKind: VillageKind // Not needed
    ) {
        if (!isValidCoordinate(coordinate) || invalidIntersectionsByDistanceRule().contains(coordinate)) {
            throw IllegalArgumentException("Invalid build coordinate.")
        } else {
            intersections.add(
                Node(
                    coordinate,
                    Village(
                        VillageKind.SETTLEMENT,
                        player.number
                    )
                )
            )
        }
    }

//    fun buildRoad(
//        player: Player,
//        coordinate: EdgeCoord,
//        roadKind: RoadKind
//    ) {
//        if (!canBuildRoad(player, coordinate, roadKind)) {
//            throw IllegalArgumentException("Invalid build coordinate for ${roadKind.name}.")
//        } else if (!hasSufficientResources(player, roadKind.cost)) {
//            throw IllegalArgumentException("Not sufficient resources to build ${roadKind.name}.")
//        } else {
//            paths.add(
//                Edge(
//                    coordinate,
//                    Road(RoadKind.ROAD, player.number)
//                )
//            )
//        }
//    }

    /**
     * Checks whether player can build a city on coordinate.
     * The intersection needs to have a settlement.
     */
    private fun canBuildCity(
        player: Player,
        coordinate: NodeCoord
    ): Boolean {
        return intersections
            .filter { i -> i.village.owner == player.number && i.village.villageKind == VillageKind.CITY }
            .map { i -> i.coordinate }
            .contains(coordinate)
    }

    private fun canBuildVillage(
        player: Player,
        coordinate: NodeCoord,
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
        coordinate: EdgeCoord,
        roadKind: RoadKind
    ): Boolean {
        return (!paths.map { p -> p.coordinate }.contains(coordinate) ||
            getCoordinatesPathsOwnedBy(player)
                .any { getAdjacentPaths(coordinate).contains(it) }
            )
        // return getRoadFrontier(player).contains(coordinate)
    }


//    fun harvestInitialResources() {
//        intersections.forEach { intersection ->
//            getAdjacentTiles(intersection.coordinate).forEach { tile ->
//                tile.resource?.let { intersection.village.harvest(it) }
//            }
//        }
//    }
}
