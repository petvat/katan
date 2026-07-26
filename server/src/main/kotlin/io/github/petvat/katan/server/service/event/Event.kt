package io.github.petvat.katan.server.service.event

import io.github.petvat.katan.server.service.channel.ChannelId
import io.github.petvat.katan.shared.protocol.ErrorCode

sealed interface Event {
    val targetChannelId: ChannelId?
    var channelSeq: Int? // TODO: This or either manually channel seq for each event or Presenter return outMessage NOTE: That is easier, do it
    val description: String

    data class Failure(override val description: String, val code: ErrorCode) :
        GameEvent, GroupEvent {
        override var channelSeq: Int? = null
        override val targetChannelId = null
    }
}

