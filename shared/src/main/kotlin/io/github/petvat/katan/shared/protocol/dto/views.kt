package io.github.petvat.katan.shared.protocol.dto

import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.*
import io.github.petvat.katan.shared.model.game.GameMode
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.PermissionLevel
import kotlinx.serialization.Serializable

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
