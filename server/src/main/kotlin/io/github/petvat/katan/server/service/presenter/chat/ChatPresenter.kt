package io.github.petvat.katan.server.service.presenter.chat

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.event.ChatEvent
import io.github.petvat.katan.server.service.gateway.handleErrorResponse
import io.github.petvat.katan.server.service.presenter.Presenter
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response

class ChatPresenter(val channelRegistry: ChannelRegistry) : Presenter<ChatEvent> {
    override fun present(requester: ConnectedClient, result: ChatEvent): Map<UserId, Response> {
        val channel =
            channelRegistry.get(result.targetChannelId) ?: return handleErrorResponse(
                requester.auth.id,
                ErrorCode.NOT_FOUND
            )
        return channel.subs.keys.associateWith {
            Response.Chat(
                message = result.message.message,
                from = result.message.userId.value
            )
        }
    }

}
