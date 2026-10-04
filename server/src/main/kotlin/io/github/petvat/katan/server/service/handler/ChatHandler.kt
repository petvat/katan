package io.github.petvat.katan.server.service.handler

import io.github.petvat.katan.server.service.channel.ChatChannel
import io.github.petvat.katan.server.service.channel.ChatMessage
import io.github.petvat.katan.server.service.client.Auth
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.command.Chat
import io.github.petvat.katan.server.service.event.ChatEvent
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.shared.protocol.ErrorCode

class ChatHandler : ChatCommandHandler {
    override fun execute(client: ConnectedClient, channel: ChatChannel, command: Chat): Event {
        if (client.auth is Auth.Unauth) return Event.Failure("Unauth.", ErrorCode.DENIED)

        val message = ChatMessage(client.auth.id, command.message)
        channel.chatHistory.add(message)

        return ChatEvent(channel.id, description = "${client.auth.id} sent a message", message = message)

    }
}
