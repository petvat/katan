package io.github.petvat.katan.server.service.event

import io.github.petvat.katan.server.service.channel.ChannelId
import io.github.petvat.katan.shared.UserId


sealed interface GroupEvent : Event {
    data class Created(
        override val targetChannelId: ChannelId, // groupId
        override var channelSeq: Int? = null,
        override val description: String,
    ) : GroupEvent

    data class Joined(
        override val targetChannelId: ChannelId,
        override var channelSeq: Int? = null,
        val userId: UserId,
        val name: String
    ) : GroupEvent {
        override val description = "$name ($userId) joined."
    }

    data class Left(
        override val targetChannelId: ChannelId,
        override var channelSeq: Int? = null,
        val userId: UserId
    ) :
        GroupEvent {
        override val description = "$userId left."
    }


}
