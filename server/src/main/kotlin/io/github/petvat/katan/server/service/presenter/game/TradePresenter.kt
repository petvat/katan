package io.github.petvat.katan.server.service.presenter.game

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GameChannel
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.shared.protocol.Response

class TradeExecutedPresenter(channelRegistry: ChannelRegistry) :
    AbstractGamePresenter<GameEvent.TradeExecuted>(channelRegistry) {

    override fun playerView(
        event: GameEvent.TradeExecuted,
        requestPlayer: Int,
        playerNumber: Int,
        channel: GameChannel
    ): Response {
        val snapshot = channel.snapshot
        return Response.TradeExecuted(
            gameId = channel.id.value,
            tradeId = event.tradeId,
            acceptor = event.acceptor,
            resources = (event.resources[playerNumber]
                ?: snapshot.players.first { it.number == playerNumber }.resources),
            othersResources = event.resources
                .filter { it.key != playerNumber }
                .mapValues { it.value.total }
        )
    }

    override fun spectatorView(event: GameEvent.TradeExecuted, channel: GameChannel): Response {
        TODO() // counts only, same as your other spectator views
    }
}
