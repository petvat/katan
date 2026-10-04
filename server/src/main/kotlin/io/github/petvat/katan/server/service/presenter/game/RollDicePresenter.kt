package io.github.petvat.katan.server.service.presenter.game

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GameChannel
import io.github.petvat.katan.shared.model.game.Phase
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.shared.protocol.Response
import io.github.petvat.katan.shared.protocol.dto.toDto

class RollDicePresenter(channelRegistry: ChannelRegistry) :
    AbstractGamePresenter<GameEvent.DiceRolledSummary>(channelRegistry) {

    override fun playerView(
        event: GameEvent.DiceRolledSummary,
        requestPlayer: Int,
        playerNumber: Int,
        channel: GameChannel
    ): Response {

        return Response.DiceRolled(
            playerNumber = requestPlayer, // TRIGGERER
            roll1 = event.roll1,
            roll2 = event.roll2,
            resources = event.resources[playerNumber]!!.toDto(),
            othersResources = event.resources
                .filter { entry -> entry.key != playerNumber }
                .mapValues { (_, value) -> value.total },
            moveRobber = event.nextPhase == Phase.MOVE_ROBBER,
            gameId = channel.id.value,
            // description = event.description,
        )
    }

    override fun spectatorView(event: GameEvent.DiceRolledSummary, channel: GameChannel): Response {
        // Spectator Object!
        TODO()
    }
}
