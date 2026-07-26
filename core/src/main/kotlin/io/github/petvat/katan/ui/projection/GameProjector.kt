package io.github.petvat.katan.ui.projection

import io.github.petvat.katan.model.GameState
import io.github.petvat.katan.model.RuleEngine
import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.hexlib.HexUtils
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.Board
import io.github.petvat.katan.shared.model.board.Edge
import io.github.petvat.katan.shared.model.board.Intersection
import io.github.petvat.katan.shared.model.board.Tile
import io.github.petvat.katan.shared.model.game.PlayerColor
import io.github.petvat.katan.shared.model.game.ResourceMap

data class GameProjection(
    val currentTurnPlayer: Int,
    val thisPlayer: ThisPlayerProjection,
    val otherPlayers: List<OtherPlayerProjection>,
    val colors: Map<Int, PlayerColor>,
    val board: BoardProjection
)

data class ThisPlayerProjection(
    val inventory: ResourceMap,
    val victoryPoints: Int,
    val color: PlayerColor
)

data class OtherPlayerProjection(
    val playerNumber: Int,
    val color: PlayerColor,
    val victoryPoints: Int,
    val cardCount: Int
)

data class BoardProjection(
    val tiles: List<Tile>,
    val intersections: List<Intersection>,
    val paths: List<Edge>,
    val robberLocation: HexCoordinates,
    val setupFrontier: List<ICoordinates>,
    val roadFrontier: List<EdgeCoordinates>,
    val settlementFrontier: List<ICoordinates>,
    val cityFrontier: List<ICoordinates>
)


object GameProjector {

    // NOTE: Slow. Make smaller projections?
    fun project(game: GameState): GameProjection {
        return GameProjection(
            currentTurnPlayer = game.turnPlayer,

            thisPlayer = projectThisPlayer(game),
            otherPlayers = projectOtherPlayers(game),

            board = projectBoard(game.board, game.player),
            colors = game.colors
        )
    }

    private fun projectThisPlayer(game: GameState): ThisPlayerProjection {
        return ThisPlayerProjection(
            inventory = ResourceMap(
                wood = game.resources.wood,
                ore = game.resources.ore,
                wheat = game.resources.wheat,
                brick = game.resources.brick,
                wool = game.resources.wool
            ),
            victoryPoints = game.victoryPoints[game.player] ?: 0,
            color = game.colors[game.player] ?: throw IllegalStateException()
        )
    }

    private fun projectOtherPlayers(game: GameState): List<OtherPlayerProjection> {
        return game.otherPlayers.map { player ->
            OtherPlayerProjection(
                playerNumber = player,
                color = game.colors[player] ?: throw IllegalStateException(),
                victoryPoints = game.victoryPoints[player] ?: 0,
                cardCount = game.otherResources[player] ?: throw IllegalStateException()
            )
        }
    }

    private fun projectBoard(board: Board, playerNumber: Int): BoardProjection {
        return BoardProjection(
            tiles = board.tiles.map {
                Tile(
                    it.hexCoordinate,
                    it.resource,
                    it.rollListenValue
                )
            },
            intersections = board.intersections.toList(),
            paths = board.paths.toList(),
            robberLocation = board.robberLocation,
            setupFrontier = RuleEngine.updateSetupFrontier(playerNumber, board).toList(),
            settlementFrontier = RuleEngine.updateSettlementFrontier(playerNumber, board).toList(),
            cityFrontier = RuleEngine.updateCityFrontier(playerNumber, board).toList(),
            roadFrontier = RuleEngine.updateRoadFrontier(playerNumber, board).toList()
        )
    }
}
