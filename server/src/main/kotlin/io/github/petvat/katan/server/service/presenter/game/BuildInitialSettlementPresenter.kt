package io.github.petvat.katan.server.service.presenter.game

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GameChannel
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.shared.protocol.Response

class BuildInitialSettlementPresenter(channelRegistry: ChannelRegistry, userRegistry: UserRegistry) :
    AbstractGamePresenter<GameEvent.BuiltInitial>(channelRegistry, userRegistry) {
    override fun playerView(
        event: GameEvent.BuiltInitial,
        requestPlayer: Int,
        playerNumber: Int,
        channel: GameChannel, // TODO: Decouple, use Id instead.
    ): Response {

        if (!event.setupOver) {
            return Response.Build(
                gameId = channel.id.value,
                builder = requestPlayer,
                buildkind = event.buildKind,
                coordinates = event.coordinates,
                victoryPoints = event.vps,
                // description = "Initial settlement built on ${event.coordinates}"
            )
        }
        return Response.SetupEnded(
            gameId = channel.id.value,
            builder = requestPlayer,
            buildkind = event.buildKind,
            coordinates = event.coordinates,
            victoryPoints = event.vps,
            // description = "Initial settlement built on ${event.coordinates}. Setup phase has ended.",
            thisPlayer = channel.snapshot.players
                .single { it.number == playerNumber }
                .resources, // TODO: Probably better to decouple channel and do this in event receipt.
            otherPlayers = channel.snapshot.players.filter { it.number != playerNumber }
                .associate { it.number to it.resources.total }
        )
    }

    override fun spectatorView(event: GameEvent.BuiltInitial, channel: GameChannel): Response {
        return Response.OK // TODO: Implement
    }

}
