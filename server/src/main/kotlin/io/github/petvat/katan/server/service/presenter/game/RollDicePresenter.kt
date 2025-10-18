package io.github.petvat.katan.server.service.presenter.game

import io.github.petvat.katan.server.service.engine.Phase
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.shared.protocol.Response

class RollDicePresenter :
    AbstractGamePresenter<GameEvent.DiceRolledSummary>() {

    override fun buildPlayerView(event: GameEvent.DiceRolledSummary, requestPlayer: Int, playerNumber: Int): Response {

        return Response.DiceRolled(
            playerNumber = requestPlayer, // TRIGGERER
            roll1 = event.roll1,
            roll2 = event.roll2,
            resources = event.resources[playerNumber]!!,
            othersResources = event.resources
                .filter { entry -> entry.key != playerNumber }
                .mapValues { (_, value) -> value.count() },
            moveRobber = event.nextPhase == Phase.MOVE_ROBBER,
            event.description
        )
    }

    override fun buildSpectatorView(event: GameEvent.DiceRolledSummary): Response {
        // Spectator Object!
        TODO()
    }
}
