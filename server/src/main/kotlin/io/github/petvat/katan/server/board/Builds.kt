package io.github.petvat.katan.server.board

sealed class BuildKind {
    data class Village(val kind: VillageKind) : BuildKind()
    data class Road(val kind: RoadKind) : BuildKind()
}

enum class VillageKind(val productionNumber: Int, val cost: ResourceMap) {
    SETTLEMENT(1, ResourceMap(1, 0, 1, 1, 1)),
    CITY(2, ResourceMap(0, 3, 2, 0, 0))
}

enum class RoadKind(val cost: ResourceMap) {
    ROAD(ResourceMap(1, 0, 0, 0, 1))
    // SHIP
}

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
