package io.github.petvat.katan.server.service.presenter.group

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GroupChannel
import io.github.petvat.katan.server.service.channel.GroupSubscriber
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.Response


class JoinGroupPresenter(private val userRegistry: UserRegistry, channelRegistry: ChannelRegistry) :
    AbstractGroupPresenter<GroupEvent.Joined>(channelRegistry) {

    override fun groupView(event: GroupEvent.Joined, pov: UserId, channel: GroupChannel): Response {
        return if (event.userId == pov) {
            Response.Joined(

                groupId = channel.id.value,
                members = channel.subs.filter { it is GroupSubscriber }.keys.associate {
                    it.value to (userRegistry.get(it)?.name ?: "Unnamed user")
                }.toMap(),
                settings = channel.settings

            )

        } else {
            Response.UserJoined(
                groupId = channel.id.value,
                userId = pov.value,
                name = userRegistry.get(pov)?.name ?: "Unnamed user"
            )
        }
    }

    override fun lobbyView(event: GroupEvent.Joined, channel: GroupChannel): Response {
        return Response.GroupUpdate(
            event.userId.value,
            capacity = channel.settings.maxPlayers,
            memberCount = channel.subs.size
        )

    }

}
