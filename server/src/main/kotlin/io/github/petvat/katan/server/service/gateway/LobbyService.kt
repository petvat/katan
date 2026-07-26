package io.github.petvat.katan.server.service.gateway

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.LobbyChannel
import io.github.petvat.katan.server.service.client.ClientRegistry
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.command.LobbyCommand
import io.github.petvat.katan.server.service.handler.CommandHandlerRegistry
import io.github.petvat.katan.server.service.concurrency.LockManager
import io.github.petvat.katan.server.service.presenter.PresenterRegistry


// TODO: Redundant
class LobbyService(
    override val handlerRegistry: CommandHandlerRegistry,
    override val presenterRegistry: PresenterRegistry,
    override val lockManager: LockManager,
    override val channelManager: ChannelRegistry,
    override val clientRegistry: ClientRegistry,
    override val userRegistry: UserRegistry
) : CommandDispatcher<LobbyCommand, LobbyChannel> {
    override fun isWrite(cmd: LobbyCommand) = false
    override fun isPermitted(requester: ConnectedClient, channel: LobbyChannel) = true // TODO: FIX
}
