package io.github.petvat.katan.server.service.gateway

import io.github.petvat.katan.server.service.channel.Channel
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
import io.github.petvat.katan.shared.protocol.ErrorCode

class GameService(
    ctx: ServerContext,
) : CommandDispatcher<GameCommand, GameChannel>, CommandContext by ctx {
    override val channelType = GameChannel::class

    override fun isPermitted(requester: ConnectedClient, channel: GameChannel): Boolean {
        return (requester.auth !is Auth.Unauth && channel.subs[requester.auth.id] is GameSubscriber.Player)
    }

    override fun isWrite(cmd: GameCommand) = true

}
