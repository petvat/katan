package io.github.petvat.katan.server.game

import io.github.petvat.katan.server.board.ResourceMap

class Trade(
    val tradeID: Int,
    val gameProgress: GameProgress,
    val initiator: Int,
    val acceptedBy: Int,
    val targets: Set<Int>,
    val offer: ResourceMap,
    val request: ResourceMap
) {
    fun transact() {
        // TODO: Implement transaction
        throw IllegalStateException(
            "Trade can not be completed because the two contracting parts do not have" +
                "sufficient resources."
        )
    }
}
