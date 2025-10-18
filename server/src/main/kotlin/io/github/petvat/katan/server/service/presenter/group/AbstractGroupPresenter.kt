package io.github.petvat.katan.server.service.presenter.group

import io.github.petvat.katan.server.service.channel.GroupChannel
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.server.service.presenter.Presenter
import io.github.petvat.katan.server.service.client.ClientId
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.Response

abstract class AbstractGroupPresenter<E : GroupEvent> : Presenter<E, GroupChannel> {

    override fun present(requester: ConnectedClient, result: E, channel: GroupChannel): Map<UserId, Response> {
        val members = channel.subs
        val spectators = channel.lobby.subs // questionable
        val memberResponses = members.keys.associateWith { inGroupView(result, it, channel) }
        val spectatorsResponses = spectators.keys.associateWith {
            outGroupView(
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
     * @param session The group session this event was performed
     */
    abstract fun inGroupView(
        event: E,
        pov: UserId,
        channel: GroupChannel
    ): Response // TODO: Should have similar name to Game presenter.

    abstract fun outGroupView(event: E, channel: GroupChannel): Response

}
