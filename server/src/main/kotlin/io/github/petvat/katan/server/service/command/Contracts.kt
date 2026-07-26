package io.github.petvat.katan.server.service.command


import io.github.petvat.katan.server.service.channel.ChannelId
import io.github.petvat.katan.server.service.channel.ChatMessage


sealed interface Command

// TODO: THESE ARE REDUNDANT
sealed interface GroupCommand : Command

sealed interface LobbyCommand : Command

sealed interface GameCommand : Command

sealed interface ChatCommand : Command

data class Chat(val message: String) : ChatCommand
