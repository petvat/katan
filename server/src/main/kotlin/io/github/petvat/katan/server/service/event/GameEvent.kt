package io.github.petvat.katan.server.service.event

import io.github.petvat.katan.server.service.channel.ChannelId
import io.github.petvat.katan.shared.model.game.Phase
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.ResourceMap


sealed interface GameEvent : Event {


    /**
     * @property sourceChannelId the (group) channel this came from
     */
    data class Init(
        override val targetChannelId: ChannelId,
        val sourceChannelId: ChannelId,
    ) : GameEvent {
        override var channelSeq: Int? = null

        override val description: String
            get() = "Game $targetChannelId has been created, $sourceChannelId has been destroyed."
    }

    data class TradeInitiated(
        override val targetChannelId: ChannelId,
        override var channelSeq: Int? = null,
        val initiator: Int,
        val tradeId: Int,
        val targets: Set<Int>,
        val offer: ResourceMap,
        val inReturn: ResourceMap
    ) : GameEvent {
        override val description: String get() = "Trade $tradeId initiated by $initiator."
    }

    /**
     * @property dead Trade is dead when [by] was the last trade target to decline.
     */
    data class TradeDeclined(
        override val targetChannelId: ChannelId,
        override var channelSeq: Int? = null,
        val tradeId: Int,
        val by: Int,
        val dead: Boolean
    ) : GameEvent {
        override val description: String get() = "$by declined trade $tradeId."
    }

    /**
     *
     * @property resources Player hands post-transaction
     */
    data class TradeExecuted(
        override val targetChannelId: ChannelId,
        override var channelSeq: Int? = null,
        val tradeId: Int,
        val initiator: Int,
        val acceptor: Int,
        val resources: Map<Int, ResourceMap>
    ) : GameEvent {
        override val description: String get() = "$acceptor accepted trade $tradeId."
    }

    data class TurnEnded(
        override val targetChannelId: ChannelId,
        override var channelSeq: Int? = null,
        val nextPlayer: Int
    ) : GameEvent {
        override val description: String
            get() = "Turn has ended. $nextPlayer is next."
    }

    data class DiceRolledSummary(
        override val targetChannelId: ChannelId,
        override var channelSeq: Int? = null,
        val roll1: Int,
        val roll2: Int,
        val resources: Map<Int, ResourceMap>,
        val nextPhase: Phase
    ) : GameEvent {

        override val description: String
            get() = "Dice rolled."
    }

    data class CardStolen(
        override val targetChannelId: ChannelId,
        override var channelSeq: Int? = null,
        val victim: Int,
        val resources: ResourceMap
    ) : GameEvent {

        override val description = "Stole from $victim!"
    }

    /**
     * @property setupOver Whether the last initial building has been built and setup is over.
     */
    data class BuiltInitial(
        override val targetChannelId: ChannelId,
        override var channelSeq: Int? = null,
        val coordinates: Coordinates,
        val buildKind: BuildKind,
        val vps: Map<Int, Int>,
        val setupOver: Boolean
    ) : GameEvent {

        override val description = "$buildKind was built at $coordinates."
    }

    data class Built(
        override val targetChannelId: ChannelId,
        override var channelSeq: Int? = null,
        val coordinates: Coordinates,
        val buildKind: BuildKind,
        val vps: Map<Int, Int>
    ) : GameEvent {

        override val description = "$buildKind was built at $coordinates."
    }

}
