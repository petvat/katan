package io.github.petvat.katan.server.service.gateway

import io.github.petvat.katan.server.service.channel.*
import io.github.petvat.katan.server.service.client.Auth
import io.github.petvat.katan.server.service.client.ClientRegistry
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.command.ChatCommand
import io.github.petvat.katan.server.service.command.GameCommand
import io.github.petvat.katan.server.service.concurrency.LockManager
import io.github.petvat.katan.server.service.handler.CommandHandlerRegistry
import io.github.petvat.katan.server.service.presenter.PresenterRegistry

class ChatService(
    override val handlerRegistry: CommandHandlerRegistry,
    override val presenterRegistry: PresenterRegistry,
    override val lockManager: LockManager,
    override val channelManager: ChannelRegistry,
    override val clientRegistry: ClientRegistry,
    override val userRegistry: UserRegistry,
) : CommandDispatcher<ChatCommand, ChatChannel> {
    override fun isPermitted(requester: ConnectedClient, channel: ChatChannel): Boolean {
        return (requester.auth !is Auth.Unauth && channel.subs[requester.auth.id] is ChatSubscriber.Member)
    }

    override fun isWrite(cmd: ChatCommand) = true

}
