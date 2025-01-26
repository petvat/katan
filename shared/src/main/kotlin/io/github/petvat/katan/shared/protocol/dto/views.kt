package io.github.petvat.katan.shared.protocol.dto

import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.*
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.PermissionLevel
import kotlinx.serialization.Serializable

/**
 * Accesssable members of this group.
 */
@Serializable
open class PrivateGroupView(
    open val id: String,
    open val clients: MutableMap<String, String>,
    open val level: PermissionLevel,
    open val chatLog: MutableList<Pair<String, String>>, // TODO: Use custom data structure.
    open val settings: Settings
)


/**
 * Accessable to all users.
 */
@Serializable
data class PublicGroupView(
    val id: String,
    val numClients: Int,
    val level: PermissionLevel,
    val settings: Settings
)


@Serializable
data class VillageView(
    val villageKind: VillageKind,
    val owner: Int,
) : DomainDTO

@Serializable
data class RoadView(
    val roadKind: RoadKind,
    val owner: Int
) : DomainDTO

@Serializable
data class IntersectionView(
    val coordinate: ICoordinates,
    val village: VillageView
) : DomainDTO

@Serializable
data class EdgeView(
    val coordinate: EdgeCoordinates,
    val road: RoadView
) : DomainDTO

//@Serializable
//data class SessionDTO(
//    val id: PublicPlayerInfo,
//    val hostId: Int,
//    val settings: Settings,
//    val members: Set<PublicUserInfo>,
//    val chatLog: List<Pair<Int, String>>,
//    val joinable: Boolean
//) : DomainDTO

/**
 * Data Transfer Object of [InternalBoard] that hides sensitive information.
 */
@Serializable
data class BoardView(
    var tiles: List<Tile>,
    val intersections: MutableList<IntersectionView>,
    val paths: List<EdgeView>,
    val robberLocation: HexCoordinates
) : DomainDTO, Board
