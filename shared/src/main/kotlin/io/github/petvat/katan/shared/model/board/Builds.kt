package io.github.petvat.katan.shared.model.board


import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.protocol.dto.RoadDTO
import io.github.petvat.katan.shared.protocol.dto.Transmittable
import io.github.petvat.katan.shared.protocol.dto.VillageDTO
import kotlinx.serialization.Serializable

@Serializable
sealed class BuildKind {
    data class Village(val kind: VillageKind) : BuildKind()
    data class Road(val kind: RoadKind) : BuildKind()
}

@Serializable
enum class VillageKind(val productionNumber: Int, val cost: ResourceMap, val vp: Int) {
    SETTLEMENT(1, ResourceMap(1, 0, 1, 1, 1), 1),
    CITY(2, ResourceMap(0, 3, 2, 0, 0), 2)
}

@Serializable
enum class RoadKind(val cost: ResourceMap) {
    ROAD(ResourceMap(1, 0, 0, 0, 1))
}

// TODO: MOVE TO SERVER MODULE

/**
 * Represents a settlement or a city on the board.
 *
 * @param villageKind the village kind, SETTLEMENT or CITY
 * @param owner the player owner
 *
 */
class Village(
    val villageKind: VillageKind,
    val owner: Player,
) {
    fun harvest(resource: Resource) {
        owner.inventory.transaction(resource, villageKind.productionNumber)
    }
}

class Road(
    val roadKind: RoadKind,
    val owner: Player
)

/**
 * Active edge, i.e. an edge with a road.
 */
data class Edge(
    val coordinate: EdgeCoordinates,
    val road: Road
)

/**
 * Active intersection, i.e. an intersection with a village.
 */
data class Intersection(
    val coordinate: ICoordinates,
    val village: Village
)
