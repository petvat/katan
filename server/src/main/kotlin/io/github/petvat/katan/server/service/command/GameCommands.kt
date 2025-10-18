package io.github.petvat.katan.server.service.command

import io.github.petvat.katan.server.service.channel.ChannelId
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.GameId
import io.github.petvat.katan.shared.model.board.BuildKind

data class RollDice(override val gameId: ChannelId) : GameCommand

data class Build(
    override val gameId: ChannelId,
    val buildKind: BuildKind,
    val coordinates: Coordinates,
) : GameCommand
