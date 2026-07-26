package io.github.petvat.katan.server.service.presenter

import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.Response

// TODO: NON-broadcast ...

// TODO: OutMessage!!!
interface Presenter<E : Event> {
    fun present(requester: ConnectedClient, result: E): Map<UserId, Response>
}
