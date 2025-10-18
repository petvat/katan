package io.github.petvat.katan.server.service.handler

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.client.Auth
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.command.InitGame
import io.github.petvat.katan.server.service.command.JoinGroup
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.server.service.channel.GameFactory
import io.github.petvat.katan.server.service.channel.GroupChannel
import io.github.petvat.katan.shared.protocol.ErrorCode

class InitGameHandler(private val channelManager: ChannelRegistry) : GroupCommandHandler<InitGame> {
    override fun execute(client: ConnectedClient, channel: GroupChannel, command: InitGame): GroupEvent {
        //if (channel.host != client.auth.userId) return Event.Failure("Only host can start the game.")
        // TODO : CHECK IF REQER HOST

        val gameChannel = GameFactory.create(channel.settings, channel.subs.keys)
        channelManager.register(gameChannel.id, gameChannel)
        channelManager.unregister(channel.id)

        return GroupEvent.Init(
            groupId = channel.id,
            gameId = gameChannel.id
        )
    }
}

class JoinGroupHandler : GroupCommandHandler<JoinGroup> {
    override fun execute(client: ConnectedClient, channel: GroupChannel, command: JoinGroup): Event {
        if (client.auth is Auth.Unauth) return Event.Failure("Not authorized.", ErrorCode.DENIED)

        // TODO: return user id.
        return GroupEvent.Joined(client.auth.id, client.auth.name)
    }
}


