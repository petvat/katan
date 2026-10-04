package io.github.petvat.katan.server.service.presenter.game

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GameChannel
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.server.service.presenter.toDto
import io.github.petvat.katan.server.service.presenter.toPlayerDTO
import io.github.petvat.katan.server.service.presenter.toPublicDto
import io.github.petvat.katan.server.service.presenter.toSpectatorDTO
import io.github.petvat.katan.shared.protocol.Response
import io.github.petvat.katan.shared.protocol.dto.ParticipantGameSnapshot
import io.github.petvat.katan.shared.protocol.dto.SpectatorGameSnapshot
import io.github.petvat.katan.shared.protocol.dto.toDto

class InitGamePresenter(channelRegistry: ChannelRegistry) : AbstractGamePresenter<GameEvent.Init>(channelRegistry) {

    override fun playerView(
        event: GameEvent.Init,
        requestPlayer: Int,
        playerNumber: Int,
        channel: GameChannel
    ): Response.Init {

        val snapshot = channel.snapshot
        val s = snapshot.toPlayerDTO(playerNumber)
        return Response.Init(s)
    }

    override fun spectatorView(event: GameEvent.Init, channel: GameChannel): Response {
        val snapshot = channel.snapshot
        val s = snapshot.toSpectatorDTO()
        return Response.InitSpectator(s)
    }

}
