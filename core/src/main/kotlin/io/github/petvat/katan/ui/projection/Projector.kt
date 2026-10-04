package io.github.petvat.katan.ui.projection

import io.github.petvat.katan.model.BoardHelpers
import io.github.petvat.katan.model.state.ChatSession
import io.github.petvat.katan.model.state.GameSession
import io.github.petvat.katan.model.state.GroupSession
import io.github.petvat.katan.shared.hexlib.EdgeCoord
import io.github.petvat.katan.shared.hexlib.HexCoord
import io.github.petvat.katan.shared.hexlib.NodeCoord
import io.github.petvat.katan.shared.model.board.Board
import io.github.petvat.katan.shared.model.board.Edge
import io.github.petvat.katan.shared.model.board.Node
import io.github.petvat.katan.shared.model.board.Tile
import io.github.petvat.katan.shared.model.game.GameMode
import io.github.petvat.katan.shared.model.game.Phase
import io.github.petvat.katan.shared.model.game.PlayerColor
import io.github.petvat.katan.shared.model.game.ResourceMap

data class UserProjection(
    val id: String,
    val name: String
)

data class ChatProjection(
    val chatlog: List<Pair<String, String>>
)

data class GroupProjection(
    val players: List<UserProjection>,
    val gameMode: GameMode,
    val capacity: Int,
)

data class GameProjection(
    val phase: Phase,
    val turnPlayer: Int,
    val thisPlayer: ThisPlayerProjection,
    val otherPlayers: List<OtherPlayerProjection>,
) {
    val isMyturn: Boolean get() = turnPlayer == thisPlayer.playerNumber
    val showRollDice: Boolean get() = isMyturn && phase == Phase.ROLL_DICE
    val canBuild: Boolean get() = isMyturn && phase != Phase.ROLL_DICE
}

data class ThisPlayerProjection(
    val playerNumber: Int,
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
    val intersections: List<Node>,
    val paths: List<Edge>,
    val robberLocation: HexCoord,
    val setupFrontier: List<NodeCoord>,
    val roadFrontier: List<EdgeCoord>,
    val settlementFrontier: List<NodeCoord>,
    val cityFrontier: List<NodeCoord>
)

class BoardProjector(val helpers: BoardHelpers) {


    fun project(board: Board, playerNumber: Int) =
        BoardProjection(
            tiles = board.tiles.toList(),
            intersections = board.intersections.toList(),
            paths = board.paths.toList(),
            robberLocation = board.robberLocation,
            setupFrontier = helpers.updateSetupFrontier(playerNumber, board).toList(),
            settlementFrontier = helpers.updateSettlementFrontier(playerNumber, board).toList(),
            cityFrontier = helpers.updateCityFrontier(playerNumber, board).toList(),
            roadFrontier = helpers.updateRoadFrontier(playerNumber, board).toList()
        )
}


object Projector {
    fun project(game: GameSession) =
        GameProjection(
            turnPlayer = game.turnPlayer,
            thisPlayer = PlayerProjector.thisPlayer(game),
            otherPlayers = PlayerProjector.otherPlayers(game),
            phase = game.phase
        )

    fun project(chat: ChatSession) =
        ChatProjection(
            chatlog = chat.log
        )

    fun project(group: GroupSession) =
        GroupProjection(
            players = group.members.map { (id, name) -> UserProjection(id, name) },
            gameMode = group.settings.gameMode,
            capacity = group.settings.maxPlayers
        )
}

object PlayerProjector {

    fun thisPlayer(game: GameSession) =
        ThisPlayerProjection(
            inventory = ResourceMap(
                wood = game.resources.wood,
                ore = game.resources.ore,
                wheat = game.resources.wheat,
                brick = game.resources.brick,
                wool = game.resources.wool
            ),
            victoryPoints = game.victoryPoints[game.player] ?: 0,
            color = game.colors?.get(game.player) ?: throw IllegalStateException(),
            playerNumber = game.player
        )


    fun otherPlayers(game: GameSession) =
        game.otherPlayers.map { player ->
            OtherPlayerProjection(
                playerNumber = player,
                color = game.colors?.get(player) ?: throw IllegalStateException(),
                victoryPoints = game.victoryPoints[player] ?: 0,
                cardCount = game.otherResources[player] ?: throw IllegalStateException()
            )
        }


}
