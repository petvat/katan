package io.github.petvat.katan.server.service.engine.tradesystem

import io.github.petvat.katan.server.service.engine.Player
import io.github.petvat.katan.shared.model.game.ResourceMap


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
     *
     * TODO: MOVE TO Engine RESULT
     */
    fun transact(acceptor: Player) {
        if (initiator.resources.minus(inReturn) && acceptor.resources.minus(offer)) {
            initiator.resources.plus(offer)
            acceptor.resources.plus(inReturn)
            acceptedBy = acceptor
        } else throw IllegalStateException(
            "Trade $id can not be completed because the two contracting parts do not have" +
                "sufficient resources."
        )
    }
}
