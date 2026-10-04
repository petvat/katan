package io.github.petvat.katan.server.service.engine.tradesystem

import io.github.petvat.katan.server.service.engine.Player
import io.github.petvat.katan.shared.model.game.ResourceMap

/**
 * This class represents a trade context.
 *
 * A trade is turn-scoped, it expires after initiator's turn ends.
 *
 * @property id The ID of this trade
 * @property initiator The player who offered this trade
 * @property targets The target players of this trade
 * @property acceptedBy The player who accepted this trade
 * @property offer The proposed offer
 * @property inReturn The proposed return
 */
data class TradeContext(
    val id: Int,
    val initiator: Player,
    val targets: Set<Int>,
    val declinedBy: Set<Int> = emptySet(),
    var acceptedBy: Player?,
    val offer: ResourceMap,
    val inReturn: ResourceMap,
    val alive: Boolean,
) {

    val pending: Set<Int> get() = targets - declinedBy

    val executed: Boolean get() = alive && acceptedBy != null

    fun transact(acceptor: Player): Pair<Player, Player>? {
        if (!initiator.resources.affords(offer)) return null
        if (!acceptor.resources.affords(inReturn)) return null
        return initiator.copy(resources = initiator.resources - offer + inReturn) to
            acceptor.copy(resources = acceptor.resources - inReturn + offer)
    }
}
