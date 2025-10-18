package io.github.petvat.katan.server.service.command


import io.github.petvat.katan.server.service.service.Channel
import io.github.petvat.katan.server.service.service.ChannelId
import io.github.petvat.katan.shared.model.GameId


sealed interface Command


// TODO: THESE ARE REDUNDANT
sealed interface GroupCommand : Command {
    val groupId: ChannelId
}

sealed interface LobbyCommand : Command


sealed interface GameCommand : Command {
    val gameId: ChannelId
}

