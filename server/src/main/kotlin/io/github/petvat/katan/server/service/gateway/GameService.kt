package io.github.petvat.katan.server.service.gateway

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GameChannel
import io.github.petvat.katan.server.service.channel.GameSubscriber
import io.github.petvat.katan.server.service.client.Auth
import io.github.petvat.katan.server.service.client.ClientRegistry
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.command.GameCommand
import io.github.petvat.katan.server.service.handler.CommandHandlerRegistry
import io.github.petvat.katan.server.service.concurrency.*
import io.github.petvat.katan.server.service.presenter.PresenterRegistry

class GameService(
    override val handlerRegistry: CommandHandlerRegistry,
    override val presenterRegistry: PresenterRegistry,
    override val lockManager: LockManager,
    override val channelManager: ChannelRegistry,
    override val clientRegistry: ClientRegistry,
    override val userRegistry: UserRegistry,
) : CommandDispatcher<GameCommand, GameChannel> {
    override fun isPermitted(requester: ConnectedClient, channel: GameChannel): Boolean {
        return (requester.auth !is Auth.Unauth && channel.subs[requester.auth.id] is GameSubscriber.Player)
    }

    override fun isWrite(cmd: GameCommand) = true

}
