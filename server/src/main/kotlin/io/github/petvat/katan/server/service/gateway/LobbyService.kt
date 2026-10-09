package io.github.petvat.katan.server.service.gateway

import io.github.petvat.katan.server.service.channel.Channel
import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.LobbyChannel
import io.github.petvat.katan.server.service.client.ClientRegistry
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.command.LobbyCommand
import io.github.petvat.katan.server.service.handler.CommandHandlerRegistry
import io.github.petvat.katan.server.service.concurrency.LockManager
import io.github.petvat.katan.server.service.presenter.PresenterRegistry
import kotlin.reflect.KClass


// TODO: Redundant
class LobbyService(
    ctx: ServerContext
) : CommandDispatcher<LobbyCommand, LobbyChannel>, CommandContext by ctx {
    override val channelType = LobbyChannel::class

    override fun isWrite(cmd: LobbyCommand) = false
    override fun isPermitted(requester: ConnectedClient, channel: LobbyChannel) = true // TODO: FIX
}
