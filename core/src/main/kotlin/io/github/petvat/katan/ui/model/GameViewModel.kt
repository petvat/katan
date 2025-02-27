package io.github.petvat.katan.ui.model

import io.github.petvat.katan.controller.RequestController
import io.github.petvat.katan.event.*
import io.github.petvat.katan.shared.hexlib.*
import io.github.petvat.katan.shared.model.board.*
import io.github.petvat.katan.shared.model.game.PlayerColor
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.protocol.dto.GameStateDTO
import io.github.petvat.katan.shared.protocol.dto.PrivateGroupDTO

data class OtherPlayerViewModel(
    val playerNumber: Int,
    val name: String,
    val color: PlayerColor,
    val victoryPoints: Int,
    val cardCount: Int
)

data class ThisPlayerViewModel(
    val inventory: ResourceMap,
    val victoryPoints: Int,
)

/**
 *
 * Implementers: BoardRenderer, GameView
 *
 * Interface between Model and view. Holds data that is updated on events.
 */
class GameViewModel(
    private val outController: RequestController,
    group: PrivateGroupDTO,
    game: GameStateDTO,
) : ViewModel() {

    val tiles = game.board.tiles.toList()

    /**
     * Transform tiles to doubled. We need to do this to work with edges/intersections.
     */
    var tilesDoubled = tiles
        .map {
            Tile(
                HexUtils.transformToDoubled(it.hexCoordinate),
                it.resource,
                it.rollListenValue
            )
        }.toList()

    // evtListener -> evtListener

    var playerColors = game.otherPlayers.associate {
        it.playerNumber to PlayerColor.RED // TODO: Fix
    }

    var intersections = game.board.intersections

    var roadFrontier: Set<EdgeCoordinates> = emptySet()

    var settlementFrontier: Set<ICoordinates> = emptySet()

    var cityFrontier: Set<ICoordinates> = emptySet()

    var setUpFrontier: Set<ICoordinates> = emptySet()

    val thisPlayerNumber = game.player.playerNumber

    /**
     * TODO: Update such that have colors.
     */
    var paths = game.board.paths

    // private val turnOrder = game.turnOrder

    // private var turnIdx = 0
    var currentTurnPlayer: Int by propertyNotify(game.turnPlayer)

    /**
     * True if it's this player's turn.
     */
    var thisPlayerTurn = currentTurnPlayer == thisPlayerNumber

    // TODO: Chat log view model.
    var chatLogProperty: List<Pair<String, String>> by propertyNotify(group.chatLog)

    var rollDiceModeProperty: Boolean by propertyNotify(false)
        private set

    var buildModeProperty: Boolean by propertyNotify(false)
        private set

    var diceRollProperty: Pair<Int, Int> by propertyNotify(Pair(-1, -1))

    var thisPlayerViewModelProperty: ThisPlayerViewModel by propertyNotify(
        ThisPlayerViewModel(
            game.player.resources, game.player.victoryPoints
        )
    )

    var otherPlayersViewModelProperty: List<OtherPlayerViewModel> by propertyNotify(
        game.otherPlayers.map {
            OtherPlayerViewModel(
                playerNumber = it.playerNumber,
                name = "NAME",
                color = PlayerColor.RED,
                cardCount = it.cityCount,
                victoryPoints = it.victoryPoints
            )
        }
    )


    // For card animation
    var playerResourceDiff: ResourceMap = ResourceMap(0, 0, 0, 0, 0)

    var roadPlacingMode: Boolean = false
        private set

    var settlementPlacingMode: Boolean = false
        private set

    var cityPlacingMode: Boolean = false
        private set

    var robberLocation: HexCoordinates = (game.board.robberLocation)
        private set

    var setupPhase: Boolean = false
        private set

    private val setupTurnOrder: MutableList<Int>


    init {
        val reversed = game.turnOrder.reversed()
        setupTurnOrder = game.turnOrder.toMutableList()
        setupTurnOrder.addAll(reversed)

    }

    override fun onEvent(event: Event) {
        when (event) {
            is NextTurnEvent -> {
                currentTurnPlayer = event.playerNumber
            }

            is PlaceInitialSettlementEvent -> {
                settlementFrontier = updateSetupFrontier()
            }

            is RolledDiceEvent -> {
                diceRollProperty = event.roll1 to event.roll2

                // Update the resources of this player
                thisPlayerViewModelProperty = thisPlayerViewModelProperty.copy(
                    inventory = event.playerResources,
                )

                // Update the resources of other players
                otherPlayersViewModelProperty.map {
                    it.copy(
                        cardCount = event.otherPlayersCardCounts[it.playerNumber]!!
                    )
                }
            }

            is BuildEvent -> {
                when (event.buildKind) {
                    is BuildKind.Road -> {
                        roadFrontier = updateRoadFrontier()
                    }

                    is BuildKind.Village -> {
                        if (event.buildKind.kind == VillageKind.SETTLEMENT) {
                            cityFrontier = updateCityFrontier()
                        }
                        if (event.buildKind.kind == VillageKind.CITY) {
                            settlementFrontier = updateSettlementFrontier()
                        }
                        // Updates the victory points
                        if (event.playerNumber == thisPlayerNumber) {
                            thisPlayerViewModelProperty =
                                thisPlayerViewModelProperty.copy(victoryPoints = thisPlayerViewModelProperty.victoryPoints + 1)

//                            _thisPlayerModel.value =
//                                _thisPlayerModel.value.copy(victoryPoints = _thisPlayerModel.value.victoryPoints + 1)
                        } else {
//                            _otherPlayerViewModels.forEach {
//                                if (it.value.playerNumber == event.playerNumber) {
//                                    it.value = it.value.copy(
//                                        victoryPoints = it.value.victoryPoints + 1,
//                                    )
//                                }
//                            }
                            otherPlayersViewModelProperty.map {
                                if (it.playerNumber == event.playerNumber) {
                                    it.copy(victoryPoints = it.victoryPoints + 1)
                                } else {
                                    it
                                }
                            }
                        }
                    }
                }
            }

            is PlaceBuildingCommand<*> -> {
                // This will tell the renderer to include the highlights when rendering.
                // the renderer is responsible for these fields.
                when (event.buildKind) {
                    is BuildKind.Village -> {
                        if (event.buildKind.kind == VillageKind.SETTLEMENT) {
                            settlementPlacingMode = true
                        } else if (event.buildKind.kind == VillageKind.CITY) {
                            cityPlacingMode = true
                        }
                    }

                    is BuildKind.Road -> {
                        roadPlacingMode = true
                        // TODO: Toggle Highlight,
                    }
                }
            }

            else -> Unit
        }
    }

//    fun getChatLogProperty(): List<Pair<String, String>> {
//        return group.chatLogProperty
//    }

    fun handleChat(message: String) = outController.handleChat(message, null) // TODO: EH

    fun handleRollDice() = outController.handleRollDice()

    fun handleBuild(buildKind: BuildKind, coordinates: Coordinates) = outController.handleBuild(buildKind, coordinates)

    private fun updateCityFrontier(): Set<ICoordinates> {
        return intersections
            .filter { it.village.owner == thisPlayerNumber && it.village.villageKind == VillageKind.SETTLEMENT }
            .map { it.coordinate }
            .toSet()
    }

    private fun updateSetupFrontier(): Set<ICoordinates> {
        // All minus (existing + distance rule)

        TODO("BIG FIX NEEDED")

    }

    private fun updateSettlementFrontier(): Set<ICoordinates> {
        // cache frontier, update ved ny build event
        // val playerBuildingCoordinates = intersections.filter { i -> i.hasBuilding && i.building?.owner == player }
        val frontier: MutableSet<ICoordinates> = mutableSetOf()
        val playerRoads = paths
            .filter { p -> p.road.owner == thisPlayerNumber }

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
    private fun updateRoadFrontier(): Set<EdgeCoordinates> {
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
            .filter { p -> p.road.owner == thisPlayerNumber }
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
        coordinate: ICoordinates
    ): Boolean {
        return intersections
            .filter { i -> i.village.owner == thisPlayerNumber && i.village.villageKind == VillageKind.CITY }
            .map { i -> i.coordinate }
            .contains(coordinate)
    }

    private fun canBuildVillage(
        coordinate: ICoordinates,
        villageKind: VillageKind
    ): Boolean {
        return when (villageKind) {
            VillageKind.SETTLEMENT -> canBuildSettlement(coordinate)
            VillageKind.CITY -> canBuildCity(coordinate)
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

//    private fun getAdjacentIntersections(tile: Tile): List<ICoordinates> {
//        return HexUtils.adjacentIntersections(tile.hexCoordinate)
//    }

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
        return player.inventory.difference(cost).getMap()
            .any { (_, x) -> x < 0 }
    }

    private fun getCoordinatesPathsOwnedBy(): List<EdgeCoordinates> {
        return paths.filter { p -> p.road.owner == thisPlayerNumber }
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
        coordinate: ICoordinates
    ): Boolean {
        // road into intersection owned player and follows distance rule
        return (getCoordinatesPathsOwnedBy().any {
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
            (getCoordinatesPathsOwnedBy()
                .any { getAdjacentPaths(coordinate).contains(it) }
                ) && hasSufficientResources(player, roadKind.cost))
        // return getRoadFrontier(player).contains(coordinate)
    }


}


// var currentTurnPlayer = turnOrder[0]

// var roadPlacingMode = false
//   private set


// val setupTurnOrder = TODO()
//val board = game.board

// NOTE: NOT NEEDED.
// NOTE: REMEMBER TO IMPL!
//    fun registerPropertyChanges() {
//        model.onPropertyChange(KatanModel::game) {
//            paths = it.board.paths
//            intersections = it.board.intersections
//            currentTurnPlayer = it.turnPlayer
//            // settlementFrontier = getSettlementFrontier()
//            // cityFrontier = getCityFrontier()
//            // roadFrontier = getRoadFrontier()
//            thisPlayerModel = ThisPlayerViewModel(
//                it.player.inventory,
//                it.player.victoryPoints
//            )
//            otherPlayersModel = it.otherPlayers.map { op ->
//                OtherPlayerViewModel(
//                    op.playerNumber,
//                    "Name", // TODO: This
//                    PlayerColor.RED, // TODO: This
//                    victoryPoints = op.victoryPoints,
//                    cardCount = op.cardCount
//                )
//            }
//        }
//        model.onPropertyChange(KatanModel::group) {
//            chatLogProperty = it.chatLogProperty
//        }
//    }

