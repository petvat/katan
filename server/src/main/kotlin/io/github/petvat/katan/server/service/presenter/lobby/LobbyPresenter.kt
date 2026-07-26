package io.github.petvat.katan.server.service.presenter.lobby

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GroupChannel
import io.github.petvat.katan.server.service.channel.LobbyChannel
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.event.GroupEvent
import io.github.petvat.katan.server.service.presenter.Presenter
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.Response

//
//class JoinedPresenter(
//    private val channelRegistry: ChannelRegistry,
//    private val userRegistry: UserRegistry
//) : Presenter<GroupEvent.Joined, LobbyChannel> {
//    override fun present(
//        requester: ConnectedClient,
//        result: GroupEvent.Joined,
//        channel: LobbyChannel
//    ): Map<UserId, Response> {
//
//        val groupToJoin = channelRegistry.get(result.groupId) ?: return emptyMap()
//
//        val settings = (groupToJoin as GroupChannel).settings
//
//        val responseToJoined = mapOf(
//            requester.auth.id to Response.Joined(
//                groupId = groupToJoin.id.value,
//                members = groupToJoin.subs.keys().toList()
//                    .associate { it.value to (userRegistry.get(it)?.name ?: "Unnamed member") },
//                settings = settings
//            )
//        )
//
//        val responseToGroupMembers =
//            groupToJoin.subs.keys().toList().filter { it != requester.auth.id }.associateWith {
//                Response.UserJoined(
//                    groupId = groupToJoin.id.value,
//                    userId = requester.auth.id.value,
//                    name = requester.auth.name,
//                )
//            }.toMap()
//
//        // Notify Lobby
//        val lobbyResponses = channel.subs.keys().toList().associateWith {
//            Response.GroupUpdate(
//                groupId = groupToJoin.id.value,
//                groupToJoin.subs.keys.size,
//                groupToJoin.settings.maxPlayers
//            )
//        }
//
//        return responseToJoined + responseToGroupMembers + lobbyResponses
//
//
//    }
//
//}
