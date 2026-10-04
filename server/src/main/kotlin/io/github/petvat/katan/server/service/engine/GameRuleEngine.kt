package io.github.petvat.katan.server.service.engine

import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.EdgeCoord
import io.github.petvat.katan.shared.hexlib.HexCoord
import io.github.petvat.katan.shared.hexlib.NodeCoord
import io.github.petvat.katan.shared.model.board.Board
import io.github.petvat.katan.shared.model.board.BoardManager
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.Edge
import io.github.petvat.katan.shared.model.board.Node
import io.github.petvat.katan.shared.model.board.Road
import io.github.petvat.katan.shared.model.board.RoadKind
import io.github.petvat.katan.shared.model.board.Tile
import io.github.petvat.katan.shared.model.board.Village
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.RuleBook
import kotlin.random.Random

// TODO: This could ust be an Object
// FIXME: Shared
class GameRuleEngine(
    ruleBook: RuleBook,
) {
    private val boardManager = BoardManager(ruleBook)


    /**
     * Builds settlement at intersection coordinate if valid.
     */
    fun buildInitialVillage(
        board: Board,
        player: Int,
        coordinate: NodeCoord,
    ): EngineResult<Board> {
        if (coordinate in boardManager.invalidIntersectionsByDistanceRule(board)) {
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
        if (!boardManager.canBuildVillage(board, player, coordinate, villageKind))
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

    // TODO: Also update Longest Road,
    fun buildRoad(
        board: Board,
        player: Int,
        coordinate: EdgeCoord,
        roadKind: RoadKind
    ): EngineResult<Board> {
        if (!boardManager.canBuildRoad(board, player, coordinate, roadKind))
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

    /** Pays every building adjacent to each tile passing [tileFilter]. */
    private fun harvest(board: Board, tileFilter: (Tile) -> Boolean): Map<Int, ResourceMap> {
        val harvested = mutableMapOf<Int, ResourceMap>()
        board.tiles.asSequence()
            .filter(tileFilter)
            .forEach { tile ->
                boardManager.getAdjacentBuildings(board, tile).forEach { building ->
                    tile.resource?.let { res ->
                        harvested.grant(building.owner, res, building.villageKind.productionNumber)
                    }
                }
            }
        return harvested
    }

    /** Read-combine-write-back for immutable [ResourceMap] values in a mutable map. */
    private fun MutableMap<Int, ResourceMap>.grant(owner: Int, resource: Resource, amount: Int) {
        this[owner] = (this[owner] ?: ResourceMap.EMPTY).plus(resource, amount)
    }


    fun harvestInitialResources(board: Board) = harvest(board) { true }

    fun harvestResources(board: Board, eyes: Int, robberLocation: HexCoord) =
        harvest(board) { it.rollListenValue == eyes && it.hexCoordinate != robberLocation }

//    fun harvestResources(game: GameState, eyes: Int): Map<Int, ResourceMap> {
//
//        return game.board.tiles
//            .asSequence()
//            .filter { it.rollListenValue == eyes }
//            .filter { it.hexCoordinate != game.board.robberLocation }
//            .flatMap {
//                boardManager
//                    .getAdjacentBuildings(game.board, it)
//                    .map { b -> b to it }
//            }
//            .groupBy { (b, _) -> b.owner }
//            .mapValues { (_, pairs) ->
//                val resources = ResourceMap()
//                pairs.forEach { (b, t) ->
//                    t.resource?.let { resources.transaction(it, b.villageKind.productionNumber) }
//                }
//                resources
//            }
//    }


//    fun harvestInitialResources(board: Board): Map<Int, ResourceMap> {
//        val resources = mutableMapOf<Int, ResourceMap>()
//
//        board.intersections.forEach { intersection ->
//            val owner = intersection.village.owner
//            val production = intersection.village.villageKind.productionNumber
//
//            boardManager.getAdjacentTiles(board, intersection.coordinate).forEach { tile ->
//                tile.resource?.let { resource ->
//                    resources[owner] = (resources[owner] ?: ResourceMap.EMPTY)
//                        .plus(
//                            resource,
//                            production
//                        )      // read-combine-write-back
//                }
//            }
//        }
//        return resources
//    }


    /**
     * Returns the player number of the next player in turn.
     */
    fun advanceTurn(game: GameState): Int {
        val next = game.turnOrder.indexOf(game.turnPlayer) % game.turnOrder.size
        return game.turnOrder[next]
    }

    fun discardResources(game: GameState): Map<Int, ResourceMap> =
        game.players.associate { player ->
            val hand = player.resources
            val excess = hand.total - game.rules.cardLimit
            // Returns the KEPT hand (handleRollDice assigns this as the new hand)
            var kept = hand
            if (excess > 0) repeat(excess / 2) {
                val pick = Resource.entries.filter { kept[it] > 0 }.random()
                kept = kept.minus(pick, 1)
            }
            player.number to kept
        }

//    fun discardResources(game: GameState): Map<Int, ResourceMap> =
//        game.players.associate { player: Player ->
//            val cards = player.resources.count()
//            val limit = game.rules.cardLimit
//
//            if (cards <= limit)
//                return@associate player.number to ResourceMap()
//
//            val toRemove = cards.floorDiv(2)
//
//            val discarded = ResourceMap()
//
//            repeat(toRemove) {
//                val randomResource = player.resources.getMap()
//                    .filter { it.value > 0 }.keys.random()
//                discarded - randomResource //!!
//            }
//            player.number to discarded
//        }


    // TODO: Move main logic to handler, keep harvest and discard functions
//    fun handleRollDice(game: GameState, playerNum: Int): EngineResult<GameState> {
//        if (playerNum != game.turnPlayer) {
//            return EngineResult.Failure("Cannot roll dice - not your turn.")
//        }
//
//        val (roll1, roll2) = rollDice()
//        val eyes = roll1 + roll2
//
//        val (resources, phase) = if (game.rules.moveRobberOn != eyes) {
//            harvestResources(game.board, eyes, game.board.robberLocation) to Phase.BUILD_N_TRADE
//        } else {
//            discardResources(game) to Phase.MOVE_ROBBER
//        }
//
//        return EngineResult.of(
//            game.copy(
//                players = game.players.map {
//                    it.copy(
//                        resources = resources[it.number]!!
//                    )
//                }
//            ) to
//                GameEvent.DiceRolledSummary(
//                    roll1 = roll1,
//                    roll2 = roll2,
//                    resources = resources,
//                    nextPhase = phase
//                )
//        )
//    }

    fun countVictoryPoints(board: Board) =
        boardManager.countVictoryPoints(board)


    fun rollDice(): Pair<Int, Int> {
        val roll1 = Random.nextInt(1, 7)
        val roll2 = Random.nextInt(1, 7)
        return roll1 to roll2
    }

    fun hasSufficientResources(player: Player, cost: ResourceMap): Boolean {
        return player.resources.affords(cost)
    }


    fun build(
        player: Player,
        building: BuildKind,
        coordinates: Coordinates,
        game: GameState
    ): EngineResult<GameState> {
        val cost = game.rules.getCost(building)
        if (!player.resources.affords(cost)) {
            return EngineResult.Failure("Not sufficient resources to build")
        }
        return applyBuild(player, building, coordinates, game, cost)
    }

    /** Setup-phase build: placement rules only, no cost charged. */
    fun buildInitial(
        player: Player,
        building: BuildKind,
        coordinates: Coordinates,
        game: GameState
    ): EngineResult<GameState> = applyBuild(player, building, coordinates, game, cost = ResourceMap.EMPTY)

    private fun applyBuild(
        player: Player,
        building: BuildKind,
        coordinates: Coordinates,
        game: GameState,
        cost: ResourceMap
    ): EngineResult<GameState> {
        val newBoard = when (building) {
            is BuildKind.Village ->
                buildVillage(game.board, player.number, coordinates as NodeCoord, building.kind)

            is BuildKind.Road ->
                buildRoad(game.board, player.number, coordinates as EdgeCoord, building.kind)
        }

        return when (newBoard) {
            is EngineResult.Failure -> newBoard   // EngineResult<Nothing> — no cast needed
            is EngineResult.Success -> {
                val vps = boardManager.countVictoryPoints(newBoard.value)   // POST-build board, not game.board

                val updatedPlayers = game.players.map { p ->
                    val vp = vps[p.number] ?: 0

                    if (p.number != player.number) {
                        p.copy(victoryPoints = vp)
                    } else {
                        val spent = p.copy(resources = p.resources - cost, victoryPoints = vp)
                        when (building) {
                            is BuildKind.Road -> spent.copy(roadsLeft = spent.roadsLeft - 1)
                            is BuildKind.Village -> when (building.kind) {
                                VillageKind.SETTLEMENT -> spent.copy(settlementsLeft = spent.settlementsLeft - 1)
                                VillageKind.CITY -> spent.copy(citiesLeft = spent.citiesLeft - 1)
                            }
                        }
                    }
                }
                EngineResult.of(game.copy(board = newBoard.value, players = updatedPlayers))
            }
        }
    }
}
