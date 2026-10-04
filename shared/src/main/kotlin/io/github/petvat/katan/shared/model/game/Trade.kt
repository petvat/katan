package io.github.petvat.katan.shared.model.game

import kotlinx.serialization.Serializable


@Serializable
data class Trade(
    val id: Int,
    val initiator: Int,
    val targets: Set<Int>,
    val offer: ResourceMap,
    val inReturn: ResourceMap,
    val declinedBy: Set<Int>,
    val acceptedBy: Int?,
) {
    val pending: Set<Int> get() = targets - declinedBy
}

