package io.github.petvat.katan.shared.model.game

import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.VillageKind
import kotlinx.serialization.Serializable

@Serializable
data class RuleBook(
    val moveRobberOn: Int,
    val cardLimit: Int,
    val settlementCost: ResourceMap,
    val cityCost: ResourceMap,
    val roadCost: ResourceMap
) {
    companion object {
        fun from(settings: Settings) = RuleBook(
            settings.numRobber,
            settings.cardLimit,
            settings.settlementCost,
            settings.cityCost,
            settings.roadCost
        )
    }

    // TODO: Clean up
    fun getCost(buildKind: BuildKind): ResourceMap {
        return when (buildKind) {
            is BuildKind.Road -> roadCost
            is BuildKind.Village -> when (buildKind.kind) {
                VillageKind.SETTLEMENT -> settlementCost
                VillageKind.CITY -> cityCost
            }
        }
    }
}
