package io.github.petvat.katan.server.service.presenter.group

import io.github.petvat.katan.server.service.channel.GroupChannel
import io.github.petvat.katan.server.service.channel.GroupSubscriber
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.Response
import io.github.petvat.katan.shared.protocol.dto.PrivateGroupDTO

class JoinGroupPresenter(private val userRegistry: UserRegistry) : AbstractGroupPresenter<GroupEvent.Joined>() {

    override fun inGroupView(event: GroupEvent.Joined, pov: UserId, channel: GroupChannel): Response {
        return if (event.userId == pov) {
            Response.Joined(
                seq = channel.seq, // <- unsafe
                groupDTO = PrivateGroupDTO(
                    id = channel.id.value,
                    clients = channel.subs.filter { it is GroupSubscriber }.keys.associate {
                        it.value to userRegistry.get(it).name
                    },
                    settings = channel.settings
                )
            )
        } else {
            Response.UserJoined(
                seq = channel.seq,
                userId = pov.value,
                name = userRegistry.get(pov).name
            )
        }
    }

    override fun outGroupView(event: GroupEvent.Joined, channel: GroupChannel): Response {
        return Response.GroupUpdate(
            event.userId.value,
            capacity = channel.settings.maxPlayers,
            memberCount = channel.subs.size
        )
    }

}
