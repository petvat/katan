package io.github.petvat.katan.server.board

class BoardManager {

    var tiles: MutableList<Tile> = mutableListOf()
    var robberLocation: Coordinate = Coordinate(0, 0)
    private var intersections: MutableList<Intersection> = mutableListOf()
    private var paths: MutableList<Path> = mutableListOf()

    /**
     * Get intersections adjacent to a tile. Will return atmost return 6 intersections.
     *
     */
    fun getAdjacentIntersections(tile: Tile): List<Coordinate> {
        val y = tile.coordinate.y
        val x = tile.coordinate.x

        var yOffset: Int
        var xOffset: Int

        val offsets = arrayOf(
            0, 1,
            2, 3,
            3, 2,
            1, 0,
            0, -1,
            -1, 0,
        )
        return offsets.indices
            .step(2)
            .mapNotNull { i ->
                xOffset = x + i
                yOffset = y + i + 1
                Coordinate(xOffset, yOffset)
            }
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

    /**
     * Attempts to move robber to tile coordinate.
     * @return true if success
     */
    fun moveRobber(tileCoordinate: Coordinate) {
        if (tiles.none { it.coordinate == tileCoordinate } || robberLocation == tileCoordinate) {
            throw IllegalArgumentException("Cannot move robber to coordinate $tileCoordinate")
        } else {
            robberLocation = tileCoordinate
        }
    }

    /**
     * Get adjacents intersection coordinates to intersection
     * Usecase: check valid settlement placements
     */
    fun getAdjacentIntersections(coordinate: Coordinate): List<Coordinate> {
        val adjacents: MutableList<Coordinate> = mutableListOf()
        adjacents.add(Coordinate(coordinate.x + 1, coordinate.y + 1))
        adjacents.add(Coordinate(coordinate.x - 1, coordinate.y - 1))
        if (coordinate.x % 2 == 0) {
            adjacents.add(Coordinate(coordinate.x - 1, coordinate.y + 1))
        } else {
            adjacents.add(Coordinate(coordinate.x + 1, coordinate.y - 1))
        }
        return adjacents.filter { isValidCoordinate(it) }
    }


    /**
     * Eller 2d-liste med offset
     *
     * Should return something if null
     *
     * Caching can be useful.
     */
    fun getIntersection(x: Int, y: Int): Intersection? {
        return getIntersection(Coordinate(x, y))
    }


    fun getAdjacentPaths(intersectionCoordinate: IntersectionCoordinate): List<Coordinate> {
        val adjacentPathCoordinates: MutableList<PathCoordinate> = mutableListOf()

        val offsets = if (intersectionCoordinate.x % 2 == 0) arrayOf(
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
        return calculateOffsets(intersectionCoordinate, offsets).filter { isValidCoordinate(it) }
    }

    /**
     * Get Adjacent paths to a path.
     *
     * There are 3 offsets. ...
     */
    fun getAdjacentPaths(pathCoordinate: PathCoordinate): List<Coordinate> {
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

        val offsets: Array<Int> = if (pathCoordinate.x + pathCoordinate.y % 2 == 0) {
            // Path is horizontal
            horizontalOffsets
        } else if (pathCoordinate.x % 2 == 0) {
            // Path is downwards
            downOffsets
        } else {
            // path is upwards
            upOffsets
        }
        return calculateOffsets(pathCoordinate, offsets)
    }

    /**
     * @param offsets expects array to iterate over in pairs of 2
     */
    private fun calculateOffsets(coordinate: Coordinate, offsets: Array<Int>): List<Coordinate> {
        val coordinateOffsets: MutableList<Coordinate> = mutableListOf()
        for (i in offsets.indices.step(2)) {
            coordinateOffsets.add(Coordinate(coordinate.x + offsets[i], coordinate.y + offsets[i + 1]))
        }
        return coordinateOffsets
    }

    // Samtidig kan bruke cache, treng ikkje å oppdatere ofte
    private fun getSettlementFrontier(player: Player): Set<Coordinate> {
        // cache frontier, update ved ny build event
        // val playerBuildingCoordinates = intersections.filter { i -> i.hasBuilding && i.building?.owner == player }
        val frontier: MutableSet<Coordinate> = mutableSetOf()
        val playerRoads = paths
            .filter { p -> p.road.owner == player }

        // effektiv trealgoritme, DFS - Seinare
        // start with random
        val playerRoadCoordinates = playerRoads.map { pr -> pr.coordinate }
        playerRoadCoordinates.forEach { c ->
            val vertical = (c.x + c.y) % 2 == 0
            val adjacentIntersectionCoordinates =
                if (vertical)
                    Coordinate(
                        c.x + 1,
                        c.y
                    ) to Coordinate(c.x, c.y + 1)
                else
                    Coordinate(
                        c.x,
                        c.y
                    ) to Coordinate(c.y + 1, c.y + 1)
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
     * Check intersection coordinate is valid, i.e. within bounds
     */
    private fun isValidCoordinate(x: Int, y: Int): Boolean {
        val offsets = arrayOf(
            0, 1,
            -2, -1,
            0, -1
        )
        val adjacentTiles: MutableList<Coordinate> = mutableListOf()

        for (i in offsets.indices.step(2)) {
            if (x % 2 == 0) {
                adjacentTiles.add(Coordinate(x + offsets[i], y + offsets[i + 1]))
            } else {
                adjacentTiles.add(Coordinate(x + offsets[i + 1], y + offsets[i]))
            }
        }
        return tiles.map { it.coordinate }.any { adjacentTiles.contains(it) }
    }

    // TODO: Intersection coordinate
    private fun isValidCoordinate(coordinate: Coordinate): Boolean {
        return isValidCoordinate(Coordinate(coordinate.x, coordinate.y))
    }

    private fun isValidCoordinate(pathCoordinate: PathCoordinate): Boolean {

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


        val offsets = if (pathCoordinate.x + pathCoordinate.y % 2 == 0) {
            horizontalOffsets
        } else if (pathCoordinate.x % 2 == 0) {
            downOffsets
        } else {
            upOffsets
        }
        val adjacentTiles = calculateOffsets(pathCoordinate, offsets)

        return tiles.map { t -> t.coordinate }.any { adjacentTiles.contains(it) }
    }

    fun intersectionAt(coordinate: Coordinate): Boolean {
        return intersectionAt(coordinate.x, coordinate.y)
    }

    fun intersectionAt(x: Int, y: Int): Boolean {
        return intersections.map { i -> i.coordinate }.contains(Coordinate(x, y))
    }

    fun getIntersection(coordinate: Coordinate): Intersection? {
        return intersections.find { i -> i.coordinate == coordinate }
    }

    /**
     * Get all possible coordinates for placing a road.
     * @param player Possible coordinates for this player
     */
    fun getRoadFrontier(player: Player): Set<Coordinate> {
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

        val frontier: MutableSet<Coordinate> = mutableSetOf()

        ownerCoordinates
            .forEach { c ->
                val offset: Array<Int>
                val x = c.x
                val y = c.y
                offset = if ((x + y) % 2 == 0) verticalOffsets else horizontalOffsets

                for (i in offset.indices.step(2)) {
                    val coordinateOffset = Coordinate(x + i, y + i + 1)
                    if (!frontier.contains(coordinateOffset)
                        && ownerCoordinates.contains(coordinateOffset)
                    ) {
                        frontier.add(coordinateOffset)
                    }
                }
            }
        return frontier.filter { isValidCoordinate(it.x, it.y) }.toSet()
    }

    /**
     * Check that intersection coordinate is valid build spot, by checking bounds
     * occupied and distance rule.
     */
//    private fun canBuildSettlement(player: Player, coordinate: Coordinate): Boolean {
//        return isValidCoordinate(coordinate.x, coordinate.y) &&
//            !intersectionAt(coordinate) &&
//            intersections.map { it.coordinate }
//                .any {
//                    it in getAdjacentIntersections(coordinate)
//                }
//    }


    private fun hasSufficientResources(
        player: Player,
        cost: ResourceMap
    ): Boolean {
        return player.inventory.minus(cost)
    }

    private fun getCoordinatesPathsOwnedBy(player: Player): List<PathCoordinate> {
        return paths.filter { p -> p.road.owner == player }.map { p -> p.coordinate as PathCoordinate }.toList()
    }

    /**
     * Check if a building on this intersection would violate distance rule.
     *
     * @param intersectionCoordinate to check
     * @return true if comply distance rule
     */
    private fun distanceRule(intersectionCoordinate: IntersectionCoordinate): Boolean {
        return intersections
            .map { i -> i.coordinate }
            .any { it in getAdjacentIntersections(intersectionCoordinate) }
    }

    private fun invalidIntersectionsByDistanceRule(): Set<Coordinate> {
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
        coordinate: Coordinate
    ): Boolean {
        // road into intersection owned player and follows distance rule
        return (getCoordinatesPathsOwnedBy(player).any {
            getAdjacentPaths(coordinate as IntersectionCoordinate).contains(it)
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
        coordinate: Coordinate,
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
     * Builds settlement at intersection if valid.
     *
     */
    fun buildSettlementInitial(
        player: Player,
        coordinate: Coordinate,
        villageKind: VillageKind
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
        coordinate: Coordinate,
        roadKind: RoadKind
    ) {
        if (!canBuildRoad(player, coordinate, roadKind)) {
            throw IllegalArgumentException("Invalid build coordinate for ${roadKind.name}.")
        } else if (!hasSufficientResources(player, roadKind.cost)) {
            throw IllegalArgumentException("Not sufficient resources to build ${roadKind.name}.")
        } else {
            paths.add(
                Path(
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
        coordinate: Coordinate
    ): Boolean {
        return intersections
            .filter { i -> i.village.owner == player && i.village.villageKind == VillageKind.CITY }
            .map { i -> i.coordinate }
            .contains(coordinate)
    }

    private fun canBuildVillage(
        player: Player,
        coordinate: Coordinate,
        villageKind: VillageKind
    ): Boolean {
        return when (villageKind) {
            VillageKind.SETTLEMENT -> canBuildSettlement(player, coordinate)
            VillageKind.CITY -> canBuildCity(player, coordinate)
            else -> false
        }
    }

    /**
     * Road frontier will be different for different roads.
     * TODO: fix later
     */
    private fun canBuildRoad(
        player: Player,
        coordinate: Coordinate,
        roadKind: RoadKind
    ): Boolean {
        return (!paths.map { p -> p.coordinate }.contains(coordinate) ||
            getCoordinatesPathsOwnedBy(player)
                .any { getAdjacentPaths(coordinate as PathCoordinate).contains(it) }
            )
        // return getRoadFrontier(player).contains(coordinate)
    }


}
