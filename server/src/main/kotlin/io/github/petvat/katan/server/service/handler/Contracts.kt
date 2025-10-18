package io.github.petvat.katan.server.service.handler

import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.command.Command
import io.github.petvat.katan.server.service.command.GameCommand
import io.github.petvat.katan.server.service.command.GroupCommand
import io.github.petvat.katan.server.service.command.LobbyCommand
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.server.service.channel.*

sealed interface CommandHandler<C : Command, R : Channel<*>> {
    fun execute(client: ConnectedClient, channel: R, command: C): Event
}

interface GameCommandHandler<C : GameCommand> : CommandHandler<C, GameChannel> {
    override fun execute(client: ConnectedClient, channel: GameChannel, command: C): Event
}

interface GroupCommandHandler<C : GroupCommand> : CommandHandler<C, GroupChannel> {
    override fun execute(client: ConnectedClient, channel: GroupChannel, command: C): Event
}

interface LobbyCommandHandler<C : LobbyCommand> : CommandHandler<C, LobbyChannel> {
    override fun execute(client: ConnectedClient, channel: LobbyChannel, command: C): Event
}

interface ChatCommandHandler<C : LobbyCommand> : CommandHandler<C, ChatChannel> {
    override fun execute(client: ConnectedClient, channel: ChatChannel, command: C): Event
}

