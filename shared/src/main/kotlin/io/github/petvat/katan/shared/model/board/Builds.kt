package io.github.petvat.katan.shared.model.board


import io.github.petvat.katan.shared.hexlib.EdgeCoord
import io.github.petvat.katan.shared.hexlib.NodeCoord
import io.github.petvat.katan.shared.model.game.ResourceMap
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

/**
 * Represents a settlement or a city on the board.
 *
 * @param villageKind the village kind, SETTLEMENT or CITY
 * @param owner the player owner
 *
 */
@Serializable
data class Village(
    val villageKind: VillageKind,
    val owner: Int,
) {
//    fun harvest(resource: Resource) {
//        owner.inventory.transaction(resource, villageKind.productionNumber)
//    }
}

@Serializable
class Road(
    val roadKind: RoadKind,
    val owner: Int
)

/**
 * Active edge, i.e. an edge with a road.
 */
@Serializable
data class Edge(
    val coordinate: EdgeCoord,
    val road: Road
)

/**
 * Active intersection, i.e. an intersection with a village.
 */
@Serializable
data class Node(
    val coordinate: NodeCoord,
    val village: Village
)
