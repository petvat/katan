package io.github.petvat.katan.server.service.handler

import io.github.petvat.katan.server.service.channel.*
import io.github.petvat.katan.server.service.client.Auth
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.command.CreateGroup
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.shared.protocol.ErrorCode

class CreateGroupHandler(
    private val channelRegistry: ChannelRegistry,
) : LobbyCommandHandler<CreateGroup> {
    override fun execute(client: ConnectedClient, channel: LobbyChannel, command: CreateGroup): GroupEvent {
        if (client.auth is Auth.Unauth) return Event.Failure("Not authorized.", ErrorCode.DENIED)
        val group = GroupFactory.create(command.settings, setOf(client.auth.id), channel)
        channelRegistry.register(group.id, group)
        group.chat?.let { channelRegistry.register(it.id, it) }
        return GroupEvent.Created(targetChannelId = group.id, description = "Group created")
    }
}

