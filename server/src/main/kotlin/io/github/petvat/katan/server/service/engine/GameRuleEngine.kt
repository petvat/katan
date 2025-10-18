package io.github.petvat.katan.server.service.engine

import io.github.petvat.katan.server.service.engine.board.BoardManager2
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.Board
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.RoadKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import kotlin.random.Random

// TODO: This could ust be an Object
class GameRuleEngine(
    ruleBook: RuleBook,
) {
    private val boardManager = BoardManager2(ruleBook)

    fun buildVillage(board: Board, player: Int, coordinates: ICoordinates, villageKind: VillageKind) =
        boardManager.buildVillage(board, player, coordinates, villageKind)

    fun buildRoad(board: Board, player: Int, coordinates: EdgeCoordinates, roadKind: RoadKind) =
        boardManager.buildRoad(board, player, coordinates, roadKind)

    fun harvestResources(game: GameSnapshot, eyes: Int): Map<Int, ResourceMap> {

        return game.board.tiles
            .asSequence()
            .filter { it.rollListenValue == eyes }
            .filter { it.hexCoordinate != game.board.robberLocation }
            .flatMap {
                boardManager
                    .getAdjacentBuildings(game.board, it)
                    .map { b -> b to it }
            }
            .groupBy { (b, _) -> b.owner }
            .mapValues { (_, pairs) ->
                val resources = ResourceMap()
                pairs.forEach { (b, t) ->
                    t.resource?.let { resources.transaction(it, b.villageKind.productionNumber) }
                }
                resources
            }
    }


    fun harvestInitialResources(board: Board): Map<Int, ResourceMap> {
        val resources = mutableMapOf<Int, ResourceMap>()

        board.intersections.forEach { intersection ->
            val owner = intersection.village.owner
            val villageKind = intersection.village.villageKind

            boardManager.getAdjacentTiles(board, intersection.coordinate).forEach { tile ->
                tile.resource?.let { resource ->
                    val playerResources = resources.getOrPut(owner) { ResourceMap() }
                    playerResources.transaction(resource, villageKind.productionNumber)
                }
            }
        }
        return resources
    }


    fun discardResources(game: GameSnapshot): Map<Int, ResourceMap> =
        game.players.associate { player: Player ->
            val cards = player.resources.count()
            val limit = game.rules.cardLimit

            if (cards <= limit)
                return@associate player.number to ResourceMap()

            val toRemove = cards.floorDiv(2)

            val discarded = ResourceMap()

            repeat(toRemove) {
                val randomResource = player.resources.getMap()
                    .filter { it.value > 0 }.keys.random()
                discarded - randomResource //!!
            }
            player.number to discarded
        }


    // TODO: Move main logic to handler, keep harvest and discard functions
    fun handleRollDice(game: GameSnapshot, playerNum: Int): EngineResult<Pair<GameSnapshot, GameEvent>> {
        if (playerNum != game.turnPlayer) {
            return EngineResult.Failure("Cannot roll dice - not your turn.")
        }

        val (roll1, roll2) = rollDice()
        val eyes = roll1 + roll2

        val (resources, phase) = if (game.rules.moveRobberOn != eyes) {
            harvestResources(game, eyes) to Phase.BUILD_N_TRADE
        } else {
            discardResources(game) to Phase.MOVE_ROBBER
        }

        return EngineResult.of(
            game.copy(
                players = game.players.map {
                    it.copy(
                        resources = resources[it.number]!!
                    )
                }
            ) to
                GameEvent.DiceRolledSummary(
                    roll1 = roll1,
                    roll2 = roll2,
                    resources = resources,
                    nextPhase = phase
                )
        )
    }

    fun countVictoryPoints(game: GameSnapshot) =
        boardManager.countVictoryPoints(game.board)


    fun rollDice(): Pair<Int, Int> {
        val roll1 = Random.nextInt(1, 7)
        val roll2 = Random.nextInt(1, 7)
        return roll1 to roll2
    }

    fun hasSufficientResources(player: Player, cost: ResourceMap): Boolean {
        return player.resources.minus(cost)
    }

    fun build(
        player: Player,
        building: BuildKind,
        coordinates: Coordinates,
        game: GameSnapshot
    ): EngineResult<Pair<GameSnapshot, GameEvent>> {
        if (!hasSufficientResources(player, building.cost)) {
            return EngineResult.Failure("Not sufficient resources to build")
        }
        val board = when (building) {
            is BuildKind.Village -> {
                boardManager.buildVillage(
                    game.board,
                    player.number,
                    coordinates as ICoordinates,
                    building.kind
                )
            }

            is BuildKind.Road -> {
                boardManager.buildRoad(
                    game.board,
                    player.number,
                    coordinates as EdgeCoordinates,
                    building.kind
                )
            }
        }

        return when (board) {
            is EngineResult.Failure -> board
            is EngineResult.Success -> {
                val vps = countVictoryPoints(game)

                val updatedPlayers = game.players.map {
                    val vp = vps[it.number]!!
                    player.copy(victoryPoints = vp)
                }
                EngineResult.of(
                    game.copy(board = board.value, players = updatedPlayers) to
                        GameEvent.Built(
                            coordinates = coordinates,
                            buildKind = building,
                            vps = countVictoryPoints(game) // Important as any build action could lead to change is VPs for any player
                        )
                )
            }
        }
    }
}
