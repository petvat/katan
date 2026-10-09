package io.github.petvat.katan.server.service.presenter.group

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GroupChannel
import io.github.petvat.katan.server.service.channel.GroupSubscriber
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.Response
import io.github.petvat.katan.shared.protocol.dto.UserData


class JoinGroupPresenter(private val userRegistry: UserRegistry, channelRegistry: ChannelRegistry) :
    AbstractGroupPresenter<GroupEvent.Joined>(channelRegistry) {

    override fun groupView(event: GroupEvent.Joined, pov: UserId, channel: GroupChannel): Response {
        return if (event.userId == pov) {
            Response.Joined(
                groupId = channel.id.value,
                members = channel.subs.filterValues { it is GroupSubscriber }.keys.map {
                    UserData(
                        requireNotNull(it.value) { "User $it is not in registry." },
                        userRegistry.get(it)?.name ?: "unnamed user"
                    )
                },
                settings = channel.settings,
                chatId = channel.chat?.id?.value ?: throw IllegalStateException("Chat does not exist")
            )
        } else {
            Response.UserJoined(
                groupId = channel.id.value,
                UserData(event.userId.value, userRegistry.get(event.userId)?.name ?: "Unnamed user")
            )
        }
    }

    override fun lobbyView(event: GroupEvent.Joined, channel: GroupChannel): Response {
        return Response.GroupUpdate(
            channel.id.value,
            capacity = channel.settings.maxPlayers,
            memberCount = channel.subs.size
        )

    }

}
