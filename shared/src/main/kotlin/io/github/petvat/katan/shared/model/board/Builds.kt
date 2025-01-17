package io.github.petvat.katan.shared.model.board


import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.protocol.dto.RoadView
import io.github.petvat.katan.shared.protocol.dto.Transmittable
import io.github.petvat.katan.shared.protocol.dto.VillageView
import kotlinx.serialization.Serializable

@Serializable
sealed class BuildKind {
    data class Village(val kind: VillageKind) : BuildKind()
    data class Road(val kind: RoadKind) : BuildKind()
}

@Serializable
enum class VillageKind(val productionNumber: Int, val cost: ResourceMap) {
    SETTLEMENT(1, ResourceMap(1, 0, 1, 1, 1)),
    CITY(2, ResourceMap(0, 3, 2, 0, 0))
}

@Serializable
enum class RoadKind(val cost: ResourceMap) {
    ROAD(ResourceMap(1, 0, 0, 0, 1))
    // SHIP(ResourceMap(1, 0, 0, 1, 0))
}

/**
 * Represents a settlement or a city on the board.
 *
 * @param villageKind the village kind, SETTLEMENT or CITY
 * @param owner the player owner
 *
 */
class Village(
    val villageKind: VillageKind,
    val owner: Player, // NOTE: Could use playerNumber here
) : Transmittable {
    fun harvest(resource: Resource) {
        owner.inventory.transaction(resource, villageKind.productionNumber)
    }

    override fun fromDomain(): VillageView {
        return VillageView(villageKind, owner.id)
    }
}

class Road(
    val roadKind: RoadKind,
    val owner: Player
) : Transmittable {
    override fun fromDomain(): RoadView {
        return RoadView(roadKind, owner.id)
    }
}
