package io.github.petvat.katan.server.service.gateway

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GroupChannel
import io.github.petvat.katan.server.service.channel.GroupSubscriber
import io.github.petvat.katan.server.service.client.Auth
import io.github.petvat.katan.server.service.client.ClientRegistry
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.command.GroupCommand
import io.github.petvat.katan.server.service.handler.CommandHandlerRegistry
import io.github.petvat.katan.server.service.concurrency.LockManager
import io.github.petvat.katan.server.service.presenter.PresenterRegistry

class GroupService(
    override val handlerRegistry: CommandHandlerRegistry,
    override val presenterRegistry: PresenterRegistry,
    override val lockManager: LockManager,
    override val channelManager: ChannelRegistry,
    override val clientRegistry: ClientRegistry,
    override val userRegistry: UserRegistry,
) : CommandDispatcher<GroupCommand, GroupChannel> {
    override fun isWrite(cmd: GroupCommand) = true

    override fun isPermitted(requester: ConnectedClient, channel: GroupChannel): Boolean {
        return (requester.auth !is Auth.Unauth) // Everyone that is auth is permitted (request join)

    }

}
