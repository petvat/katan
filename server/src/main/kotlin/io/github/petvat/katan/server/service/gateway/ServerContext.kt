package io.github.petvat.katan.server.service.gateway

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.ResumeTokenStore
import io.github.petvat.katan.server.service.client.ClientRegistry
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.concurrency.LockManager
import io.github.petvat.katan.server.service.handler.CommandHandlerRegistry
import io.github.petvat.katan.server.service.presenter.PresenterRegistry


/**
 * Shared dependencies for all command dispatchers. Constructed once at startup.
 */
class ServerContext(
    override val clientRegistry: ClientRegistry,
    override val userRegistry: UserRegistry,
    override val channelManager: ChannelRegistry,
    override val lockManager: LockManager,
    override val resumeTokenStore: ResumeTokenStore,
) : CommandContext {
    override val presenterRegistry = PresenterRegistry(userRegistry, channelManager)
    override val handlerRegistry = CommandHandlerRegistry(channelManager)
    val tokenStore = ResumeTokenStore()
}
