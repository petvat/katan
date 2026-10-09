package io.github.petvat.katan.server.service.presenter.group

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GroupChannel
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.OutMessage
import io.github.petvat.katan.shared.protocol.Response

class CreateGroupPresenter(channelRegistry: ChannelRegistry) :
    AbstractGroupPresenter<GroupEvent.Created>(channelRegistry) {
    override fun groupView(event: GroupEvent.Created, pov: UserId, channel: GroupChannel): Response {
        return Response.GroupCreated(channel.id.value, channel.chat?.id?.value, channel.settings)
    }

    override fun lobbyView(event: GroupEvent.Created, channel: GroupChannel): Response {
        return Response.GroupUpdate(channel.id.value, channel.subs.size, channel.settings.maxPlayers)

    }
}
