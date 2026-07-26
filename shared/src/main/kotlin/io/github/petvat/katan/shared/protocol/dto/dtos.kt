package io.github.petvat.katan.shared.protocol.dto

import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.Board
import io.github.petvat.katan.shared.model.board.RoadKind
import io.github.petvat.katan.shared.model.board.Tile
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.*
import kotlinx.serialization.Serializable

/**
 * Top-level snapshot DTOs.
 *
 * GameSnapshotPlayerView and GameSnapshotSpectatorView are deliberately
 * separate types, not one type with a nullable `yourHand`. A spectator
 * client literally cannot deserialize/access hand data that was never
 * serialized in the first place -- the guarantee is structural, not a
 * "trust me, it's always null for spectators" convention.
 *
 * ASSUMPTION: Phase, Trade, GameEvent, RuleBook not seen -- PhaseDTO and
 * TradeDTO below are reasonable guesses; GameEvent/RuleBook I've left out
 * of both snapshots entirely on purpose, see notes below each.
 */

@Serializable
data class TradeDTO(
    val tradeId: Int,
    val initiator: Int,
    val targetPlayers: Set<Int>,
    val offer: ResourceMapData,
    val inReturn: ResourceMapData
)

@Serializable
data class PlayerPublicDTO(
    val number: Int,
    val resourceCardCount: Int,
    val devCardCount: Int,
    val victoryPointCount: Int
)

@Serializable
data class PlayerDTO(
    val number: Int,
    // TODO: Hand
    val resources: ResourceMapData,
    val victoryPointCount: Int,
    var roadsLeft: Int,
    var citiesLeft: Int,
    var settlementsLeft: Int
)


@Serializable
data class GameSnapshotPlayerDTO(
    val player: PlayerDTO,
    val board: BoardDTO,
    val otherPlayers: List<PlayerPublicDTO>,
    val phase: Phase, // TODO: MOVE PHASE TO SHARED
    val turnOrder: List<Int>,
    val turnPlayer: Int,
    val ongoingTrades: List<TradeDTO>
)

@Serializable
data class GameSnapshotSpectatorDTO(
    val board: BoardDTO,
    val players: List<PlayerPublicDTO>,
    val phase: Phase,
    val turnOrder: List<Int>,
    val turnPlayer: Int,
    val ongoingTrades: List<TradeDTO>
)

/**
 * Accessible to all users.
 */
@Serializable
data class GroupExternalDTO(
    val id: String,
    val numClients: Int,
    val maxClients: Int,
    val mode: GameMode,
)

@Serializable
data class GroupInternalDTO(
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
    val coordinate: ICoordinates,
    val village: VillageDTO
)

@Serializable
data class EdgeDTO(
    val coordinate: EdgeCoordinates,
    val road: RoadDTO
)

// TODO: Move to fromDomain file
fun ResourceMap.toDto() = ResourceMapData(
    this[Resource.WOOD],
    this[Resource.ORE],
    this[Resource.WHEAT],
    this[Resource.WOOL],
    this[Resource.BRICK]
)


/**
 * Data Transfer Object of [Board] that hides sensitive information.
 */
@Serializable
data class BoardDTO(
    var tiles: List<Tile>,
    val intersections: List<IntersectionDTO>,
    val paths: List<EdgeDTO>,
    val robberLocation: HexCoordinates
)
