package io.github.petvat.katan.server.service.service

import io.github.petvat.katan.server.api.handleError
import io.github.petvat.katan.server.service.channel.Channel
import io.github.petvat.katan.server.service.channel.ChannelId
import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.client.ClientRegistry
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.command.Command
import io.github.petvat.katan.server.service.handler.CommandHandlerRegistry
import io.github.petvat.katan.server.service.concurrency.LockManager
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.server.service.presenter.PresenterRegistry
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response

interface CommandDispatcher<C : Command, Ch : Channel<*>> {
    val channelManager: ChannelRegistry
    val handlerRegistry: CommandHandlerRegistry
    val presenterRegistry: PresenterRegistry
    val clientRegistry: ClientRegistry
    val userRegistry: UserRegistry
    val lockManager: LockManager

    suspend fun handleCommand(
        requester: ConnectedClient,
        channelId: ChannelId,
        command: C,
        seq: Int
    ): Map<ConnectedClient, Response> {

        val channel = channelManager.get(channelId) ?: return handleError(requester, code = ErrorCode.NOT_FOUND)

        @Suppress("UNCHECKED_CAST")
        if (!isPermitted(requester, channel as Ch)) return handleError(requester, code = ErrorCode.DENIED)

        val handler = handlerRegistry.getHandlerFor(command)

        val block = {
            val event = handler.execute(requester, channel, command)

            if (event is Event.Failure) {
                userRegistry.getClientsByUser(requester.auth.id).associateWith {
                    Response.Error(
                        seq = seq,
                        code = event.code,
                        event.description
                    )
                }
            } else {
                val presenter = presenterRegistry.getPresenterFor(event)
                val responses = presenter
                    .present(requester, event, channel)
                    .flatMap { (userId, response) ->
                        userRegistry.getClientsByUser(userId).map { client ->
                            client to response
                        }
                    }
                    .toMap()
                responses
            }
        }
        val idsToResponses = if (isWrite(command))
            lockManager.callInMutex(channel, block)
        else
            block()

        return idsToResponses.mapKeys { (k, _) -> clientRegistry.get(k) }.toMap()
    }

    fun isWrite(cmd: C): Boolean

    fun isPermitted(requester: ConnectedClient, channel: Ch): Boolean

}

//
//interface CommandService<C : Command, R : Realm> {
//    val realmManager: RealmManager
//    val handlerRegistry: CommandHandlerRegistry
//    val presenterRegistry: PresenterRegistry
//    val lockManager: LockManager
//
//    suspend fun handleCommand(
//        requester: ConnectedClient,
//        realmId: RealmId,
//        command: C
//    ): Map<ConnectedClient, Response> {
//        val session = realmManager[realmId] ?: return handleError(requester, code = ErrorCode.NOT_FOUND)
//
//        @Suppress("UNCHECKED_CAST")
//        if (!isPermitted(requester, session as R)) return handleError(requester, code = ErrorCode.DENIED)
//
//        val handler = handlerRegistry.getHandlerFor(command)
//
//        return lockManager.callInMutex(session) {
//
//            val event = handler.execute(requester, session, command)
//
//            val presenter = presenterRegistry.getPresenterFor(event)
//            val responses = presenter.present(requester, event, session)
//
//            // if broadcast? -> broadcast to lobby!
//
//            responses
//        }
//    }
//
//    fun isPermitted(requester: ConnectedClient, realm: R): Boolean
//}




