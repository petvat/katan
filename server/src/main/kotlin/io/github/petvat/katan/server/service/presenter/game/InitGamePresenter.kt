package io.github.petvat.katan.server.service.presenter.game

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GameChannel
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.server.service.presenter.toPlayerDTO
import io.github.petvat.katan.server.service.presenter.toSpectatorDTO
import io.github.petvat.katan.shared.protocol.Response


class InitGamePresenter(channelRegistry: ChannelRegistry, userRegistry: UserRegistry) :
    AbstractGamePresenter<GameEvent.Init>(channelRegistry, userRegistry) {

    override fun playerView(
        event: GameEvent.Init,
        requestPlayer: Int,
        playerNumber: Int,
        channel: GameChannel,
    ): Response.Init {

        val snapshot = channel.snapshot
        val meta = metaOf(channel)

        val s = snapshot.toPlayerDTO(playerNumber, meta)
        return Response.Init(s)
    }

    override fun spectatorView(event: GameEvent.Init, channel: GameChannel): Response {
        val snapshot = channel.snapshot
        val s = snapshot.toSpectatorDTO(metaOf(channel))
        return Response.InitSpectator(s)
    }

}
