package io.github.petvat.katan.server.service.event

import io.github.petvat.katan.server.service.channel.ChannelId
import io.github.petvat.katan.server.service.client.ClientId
import io.github.petvat.katan.shared.UserId


sealed interface GroupEvent : Event {
    data class Init(
        val groupId: ChannelId,
        val gameId: ChannelId,
    ) : GroupEvent {
        override val description = "Game ($gameId) created."

    }

    data class Created(
        val groupId: ChannelId,
        override val description: String,
    ) : GroupEvent

    data class Joined(val userId: UserId, val name: String) : GroupEvent {
        override val description = "$name ($userId) joined."
    }

}
