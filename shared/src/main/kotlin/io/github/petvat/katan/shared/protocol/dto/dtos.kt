package io.github.petvat.katan.shared.protocol.dto

import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.Board
import io.github.petvat.katan.shared.model.board.RoadKind
import io.github.petvat.katan.shared.model.board.Tile
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.GameMode
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.PermissionLevel
import io.github.petvat.katan.shared.model.game.PlayerColor
import kotlinx.serialization.Serializable

/**
 * Simplified game progress available to players from the perspective of client.
 */
//@Serializable
//data class PrivateGameState(
//    val playerNumber: Int,
//    val otherPlayers: List<OtherPlayerDTO>,
//    val turnOrder: List<Int>,
//    var turnPlayer: Int,
//    val board: BoardDTO
//)

/**
 * Simplified game progress available to players from the perspective of client.
 *
 */
@Serializable
data class GameStateDTO(
    val player: PlayerDTO,
    val otherPlayers: List<PlayerDTO>,
    val turnOrder: List<Int>,
    var turnPlayer: Int,
    val board: BoardDTO
)

/**
 * NOTE: For now, all player data is sent to all players.
 *
 * NOTE: 'var' because reusing this for client logic.
 */
@Serializable
data class PlayerDTO(
    val playerNumber: Int,
    val color: PlayerColor,
    var victoryPoints: Int,
    var resources: ResourceMap,
    var settlementCount: Int,
    var cityCount: Int,
    var roadCount: Int
)

/**
 * Dto for visible player data.
 *
 * NOTE: Color is assigned by client, will not be consistent across clients
 */
@Serializable
data class OtherPlayerDTO(
    val playerNumber: Int,
    var victoryPoints: Int,
    var cardCount: Int,
    var settlementCount: Int,
    var cityCount: Int,
    var roadCount: Int
)

@Serializable
data class PublicUserDTO(
    val id: String,
    var username: String
)

@Serializable
data class PrivateUserDTO(
    val username: String,
    val password: String,
    // Add other stuff
)


/**
 * Accessible to members of this group.
 */
@Serializable
open class PrivateGroupDTO(
    open val id: String,
    open val clients: MutableMap<String, String>,
    open val level: PermissionLevel,
    open val chatLog: MutableList<Pair<String, String>>, // TODO: Use custom data structure.
    open val settings: Settings
)

/**
 * Accessible to all users.
 */
@Serializable
data class PublicGroupDTO(
    val id: String,
    val numClients: Int,
    val maxClients: Int,
    val level: PermissionLevel,
    val mode: GameMode,
)

@Serializable
data class VillageDTO(
    var villageKind: VillageKind,
    val owner: Int,
) : DomainDTO

@Serializable
data class RoadDTO(
    val roadKind: RoadKind,
    val owner: Int
) : DomainDTO

@Serializable
data class IntersectionDTO(
    val coordinate: ICoordinates,
    val village: VillageDTO
) : DomainDTO

@Serializable
data class EdgeDTO(
    val coordinate: EdgeCoordinates,
    val road: RoadDTO
) : DomainDTO

/**
 * Data Transfer Object of [Board] that hides sensitive information.
 */
@Serializable
data class BoardDTO(
    var tiles: List<Tile>,
    val intersections: MutableList<IntersectionDTO>,
    val paths: MutableList<EdgeDTO>,
    val robberLocation: HexCoordinates
) : DomainDTO
