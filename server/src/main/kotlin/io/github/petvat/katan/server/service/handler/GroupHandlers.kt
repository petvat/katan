package io.github.petvat.katan.server.service.handler

import io.github.petvat.katan.server.service.channel.*
import io.github.petvat.katan.server.service.client.Auth
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.command.InitGame
import io.github.petvat.katan.server.service.command.JoinGroup
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.server.service.command.LeaveGroup
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.shared.protocol.ErrorCode


class InitGameHandler(private val channelManager: ChannelRegistry) : GroupCommandHandler<InitGame> {
    override fun execute(client: ConnectedClient, channel: GroupChannel, command: InitGame): GameEvent {
        if (channel.host != client.auth.id) return Event.Failure("Only host can start the game.", ErrorCode.DENIED)
        if (channel.subs.size < channel.settings.minPlayers) return Event.Failure(
            "Not enough player to start game. Minimum ${channel.settings.minPlayers} requred.",
            ErrorCode.DENIED
        )

        val gameChannel = GameFactory.create(channel.settings, channel.subs.keys, channel.chat)
        channelManager.register(gameChannel.id, gameChannel)
        channelManager.unregister(channel.id)

        return GameEvent.Init(
            sourceChannelId = channel.id,
            targetChannelId = gameChannel.id
        )
    }
}

class JoinGroupHandler : GroupCommandHandler<JoinGroup> {
    override fun execute(client: ConnectedClient, channel: GroupChannel, command: JoinGroup): Event {
        if (client.auth is Auth.Unauth) return Event.Failure("Not authorized.", ErrorCode.DENIED)

        channel.subscribe(client.auth.id, GroupSubscriber.Member)
        channel.chat?.subscribe(client.auth.id, ChatSubscriber.Member)
        // TODO: Unsub from lobby?
        return GroupEvent.Joined(targetChannelId = channel.id, userId = client.auth.id, name = client.auth.name)
    }
}

class LeaveGroupHandler : GroupCommandHandler<LeaveGroup> {
    override fun execute(client: ConnectedClient, channel: GroupChannel, command: LeaveGroup): Event {
        if (client.auth is Auth.Unauth) return Event.Failure("Not authorized.", ErrorCode.DENIED)

        channel.unsubscribe(client.auth.id)
        channel.chat?.unsubscribe(client.auth.id)

        return GroupEvent.Left(targetChannelId = channel.id, userId = client.auth.id)
    }
}


