package io.github.petvat.katan.server.service.presenter.lobby

import io.github.petvat.katan.server.service.channel.LobbyChannel
import io.github.petvat.katan.server.service.client.ClientId
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.event.LobbyEvent
import io.github.petvat.katan.server.service.presenter.Presenter
import io.github.petvat.katan.shared.protocol.Response

class RegisterPresenter : Presenter<LobbyEvent.Registered, LobbyChannel> {
    override fun present(
        requester: ConnectedClient,
        result: LobbyEvent.Registered,
        channel: LobbyChannel
    ): Map<ClientId, Response> {
        return mapOf(requester.id to Response.Registered(channel.seq, result.userId.value))
    }
}
