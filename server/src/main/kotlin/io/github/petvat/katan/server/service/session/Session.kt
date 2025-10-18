package io.github.petvat.katan.server.service.session

import io.github.petvat.katan.server.service.client.ClientId
import io.github.petvat.katan.shared.model.SessionId
import java.nio.channels.SocketChannel
import java.util.concurrent.ConcurrentHashMap

data class Session(
    val belongsTo: ClientId,
    val id: SessionId,
    val socketChannel: SocketChannel
)
