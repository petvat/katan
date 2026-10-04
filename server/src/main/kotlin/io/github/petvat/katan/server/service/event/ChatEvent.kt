package io.github.petvat.katan.server.service.event

import io.github.petvat.katan.server.service.channel.ChannelId
import io.github.petvat.katan.server.service.channel.ChatMessage

class ChatEvent(
    override val targetChannelId: ChannelId,
    override var channelSeq: Int? = null,
    override val description: String,
    val message: ChatMessage
) : Event
