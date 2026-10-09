package io.github.petvat.katan.shared.protocol.dto

import io.github.petvat.katan.shared.hexlib.EdgeCoord
import io.github.petvat.katan.shared.hexlib.HexCoord
import io.github.petvat.katan.shared.hexlib.NodeCoord
import io.github.petvat.katan.shared.model.board.Board
import io.github.petvat.katan.shared.model.board.RoadKind
import io.github.petvat.katan.shared.model.board.Tile
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.*
import kotlinx.serialization.Serializable


@Serializable
data class UserData(
    val userId: String,
    val name: String
)

@Serializable
data class TradeDTO(
    val tradeId: Int,
    val initiator: Int,
    val targetPlayers: Set<Int>,
    val offer: ResourceMap,
    val inReturn: ResourceMap
)

/**
 *
 */
@Serializable
data class PublicPlayer(
    val number: Int,
    val resourceCardCount: Int,
    val devCardCount: Int,
    val victoryPointCount: Int
)

/**
 * Private view of player.
 */
@Serializable
data class PrivatePlayer(
    val number: Int,
    val resources: ResourceMap,
    val victoryPointCount: Int,
    var roadsLeft: Int,
    var citiesLeft: Int,
    var settlementsLeft: Int
)

/** Lobby listing, delta-updated from GroupUpdate broadcasts. */
@Serializable
data class GroupExternal(
    val id: String,
    val memberCount: Int,
    val capacity: Int,
    val mode: GameMode? = null
)

@Serializable
data class ParticipantGameSnapshot(
    val player: PrivatePlayer,
    val board: BoardDTO,
    val otherPlayers: List<PublicPlayer>,
    val phase: Phase,
    val colors: Map<Int, PlayerColor>,
    val turnOrder: List<Int>,
    val turnPlayer: Int,
    val ongoingTrades: List<Trade>,
    val rules: RuleBook,
    val meta: GameMeta
)

@Serializable
data class SpectatorGameSnapshot(
    val board: BoardDTO,
    val players: List<PublicPlayer>,
    val phase: Phase,
    val turnOrder: List<Int>,
    val turnPlayer: Int,
    val ongoingTrades: List<Trade>,
    val rules: RuleBook,
    val meta: GameMeta
)

/**
 * Accessible to all users.
 */
@Serializable
data class PublicGroup(
    val id: String,
    val numClients: Int,
    val maxClients: Int,
    val mode: GameMode,
)

@Serializable
data class PrivateGroup(
    val id: String,
    val clients: Map<String, String>,
    val settings: Settings,
)


// ---- Board DTOs ----

@Serializable
data class VillageDTO(
    var villageKind: VillageKind,
    val owner: Int,
)

@Serializable
data class RoadDTO(
    val roadKind: RoadKind,
    val owner: Int
)

@Serializable
data class IntersectionDTO(
    val coordinate: NodeCoord,
    val village: VillageDTO
)

@Serializable
data class EdgeDTO(
    val coordinate: EdgeCoord,
    val road: RoadDTO
)

// TODO: Move to fromDomain file
//fun ResourceMap.toDto() = ResourceMap(
//    this[Resource.WOOD],
//    this[Resource.ORE],
//    this[Resource.WHEAT],
//    this[Resource.WOOL],
//    this[Resource.BRICK]
//)


/**
 * Data Transfer Object of [Board] that hides sensitive information.
 */
@Serializable
data class BoardDTO(
    var tiles: List<Tile>,
    val intersections: List<IntersectionDTO>,
    val paths: List<EdgeDTO>,
    val robberLocation: HexCoord
)
