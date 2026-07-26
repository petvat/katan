package io.github.petvat.katan.server.service.presenter.group

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GroupChannel
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.OutMessage
import io.github.petvat.katan.shared.protocol.Response

class LeaveGroupPresenter(channelRegistry: ChannelRegistry) :
    AbstractGroupPresenter<GroupEvent.Left>(channelRegistry) {
    override fun groupView(event: GroupEvent.Left, pov: UserId, channel: GroupChannel): Response {
        return if (pov == event.userId) {
            Response.LeftOk

        } else
            Response.Left(channel.id.value, event.userId.value)

    }

    override fun lobbyView(event: GroupEvent.Left, channel: GroupChannel): Response {
        return Response.GroupUpdate(channel.id.value, channel.subs.size, channel.settings.maxPlayers)
    }
}
