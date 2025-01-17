package io.github.petvat.katan.shared.model.game


import io.github.petvat.katan.shared.model.board.Player

/**
 * This class represents a trade context.
 *
 * @property id The ID of this trade
 * @property initiator The player who offered this trade
 * @property targets The target players of this trade
 * @property acceptedBy The player who accepted this trade
 * @property offer The proposed offer
 * @property inReturn The proposed return
 */
class Trade(
    val id: Int,
    val initiator: Player,
    val targets: Set<Int>,
    var acceptedBy: Player?,
    val offer: ResourceMap,
    val inReturn: ResourceMap
) {
    /**
     * Atomic.
     */
    fun transact(acceptor: Player) {
        if (initiator.inventory.minus(inReturn) && acceptor.inventory.minus(offer)) {
            initiator.inventory.plus(offer)
            acceptor.inventory.plus(inReturn)
            acceptedBy = acceptor
        } else throw IllegalStateException(
            "Trade $id can not be completed because the two contracting parts do not have" +
                "sufficient resources."
        )
    }
}
