package io.github.petvat.katan.server.service.handler

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GroupFactory
import io.github.petvat.katan.server.service.channel.LobbyChannel
import io.github.petvat.katan.server.service.client.Auth
import io.github.petvat.katan.server.service.client.AuthCredentials
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.command.CreateGroup
import io.github.petvat.katan.server.service.command.Register
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.server.service.event.LobbyEvent
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.ErrorCode
import java.util.UUID

class RegisterHandler : LobbyCommandHandler<Register> {
    override fun execute(client: ConnectedClient, channel: LobbyChannel, command: Register): LobbyEvent {
        if (client.auth !is Auth.Unauth) return Event.Failure("Already authorized.", ErrorCode.DENIED)
        when (command.auth) {
            is AuthCredentials.Admin -> TODO()
            is AuthCredentials.Guest -> client.auth =
                Auth.Guest(
                    command.auth.name,
                    UserId("guest:${UUID.randomUUID()}")
                )

            is AuthCredentials.User -> TODO()
        }
        return LobbyEvent.Registered(client.auth.id)
    }
}


class CreateGroupHandler(
    private val groupFactory: GroupFactory,
    private val channelRegistry: ChannelRegistry
) : LobbyCommandHandler<CreateGroup> {
    override fun execute(client: ConnectedClient, channel: LobbyChannel, command: CreateGroup): GroupEvent {
        if (client.auth is Auth.Unauth) return Event.Failure("Not authorized.", ErrorCode.DENIED)
        val group = groupFactory.create(command.settings, setOf(client.id), channel)
        channelRegistry.register(group.id, group)
        return GroupEvent.Created(group.id, "Group created")
    }
}

