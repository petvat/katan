package io.github.petvat.katan.server.service.event

import io.github.petvat.katan.server.service.channel.ChannelId
import io.github.petvat.katan.shared.protocol.ErrorCode

/**
 * Events are named for the channel whose subscribers consume them.
 *
 * @property targetChannelId The channel this event happened in
 * @property channelSeq The sequence number of this event in channel's history
 */
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

