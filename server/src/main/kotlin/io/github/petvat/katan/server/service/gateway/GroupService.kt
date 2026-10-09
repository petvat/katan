package io.github.petvat.katan.server.service.gateway

import io.github.petvat.katan.server.service.channel.Channel
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
import io.github.petvat.katan.shared.protocol.ErrorCode
import kotlin.reflect.KClass

class GroupService(ctx: ServerContext) : CommandDispatcher<GroupCommand, GroupChannel>, CommandContext by ctx {
    override val channelType = GroupChannel::class
    override fun isWrite(cmd: GroupCommand) = true

    override fun isPermitted(requester: ConnectedClient, channel: GroupChannel): Boolean {
        return (requester.auth !is Auth.Unauth) // Everyone that is auth is permitted (request join)

    }

}
