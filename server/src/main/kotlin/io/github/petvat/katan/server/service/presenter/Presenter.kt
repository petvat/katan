package io.github.petvat.katan.server.service.presenter

import io.github.petvat.katan.server.service.channel.Channel
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.Response

// TODO: NON-broadcast ...
interface Presenter<E : Event, S : Channel<*>> {
    fun present(requester: ConnectedClient, result: E, channel: S): Map<UserId, Response>
}
