package io.github.petvat.katan.server.service.presenter.group

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GroupChannel
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.server.service.gateway.handleError
import io.github.petvat.katan.server.service.gateway.handleErrorResponse
import io.github.petvat.katan.server.service.presenter.Presenter
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response

abstract class AbstractGroupPresenter<E : GroupEvent>(private val channelRegistry: ChannelRegistry) : Presenter<E> {

    override fun present(requester: ConnectedClient, result: E): Map<UserId, Response> {
        val channel =
            result.targetChannelId?.let { channelRegistry.get(it) } as? GroupChannel ?: return handleErrorResponse(
                userId = requester.auth.id,
                code = ErrorCode.NOT_FOUND,
                detail = ""
            )

        val members = channel.subs
        val spectators = channel.lobby.subs // questionable
        val memberResponses = members.keys.associateWith { groupView(result, it, channel) }
        val spectatorsResponses = spectators.keys.associateWith {
            lobbyView(
                result,
                channel
            )
        }
        return memberResponses + spectatorsResponses
    }

    /**
     * Returns the appropriate response view for member id on event.
     *
     * @param event The event that is presented
     * @param pov The member that received this message
     */
    abstract fun groupView(
        event: E,
        pov: UserId,
        channel: GroupChannel
    ): Response

    abstract fun lobbyView(event: E, channel: GroupChannel): Response

}
