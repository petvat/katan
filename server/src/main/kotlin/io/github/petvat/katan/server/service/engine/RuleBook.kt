package io.github.petvat.katan.server.service.engine

import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Settings

data class RuleBook(
    val moveRobberOn: Int,
    val cardLimit: Int,
    val settlementCost: ResourceMap,
    val cityCost: ResourceMap,
    val roadCost: ResourceMap
) {
    companion object {
        fun from(settings: Settings): RuleBook = TODO()
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
