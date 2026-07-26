package io.github.petvat.katan.server.service.presenter.game

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GameChannel
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.shared.protocol.Response


class BuildPresenter(channelRegistry: ChannelRegistry) : AbstractGamePresenter<GameEvent.Built>(channelRegistry) {
    override fun playerView(
        event: GameEvent.Built,
        requestPlayer: Int,
        playerNumber: Int,
        channel: GameChannel // TODO: Decouple, use Id instead.
    ): Response {

        return Response.Build(
            gameId = channel.id.value,
            builder = requestPlayer,
            buildkind = event.buildKind,
            coordinates = event.coordinates,
            victoryPoints = event.vps,
            // description = "${event.buildKind} built on ${event.coordinates}"
        )
    }

    override fun spectatorView(event: GameEvent.Built, channel: GameChannel): Response {
        TODO("Not yet implemented")
    }

}


