package io.github.petvat.katan.server.service.presenter.game

import io.github.petvat.katan.server.service.channel.GameChannel
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.server.service.presenter.Presenter
import io.github.petvat.katan.server.service.channel.GameSubscriber
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.Response

/**
 * TODO: To model Client = app instance, use UserId in Game and Group. Find all clients by UserId.
 */
abstract class AbstractGamePresenter<E : GameEvent>(

) : Presenter<E, GameChannel> {

    override fun present(requester: ConnectedClient, result: E, channel: GameChannel): Map<UserId, Response> {
        val players = channel.subs.filter { it.value == GameSubscriber.Player }.keys
        val spectators = channel.subs.filter { it.value == GameSubscriber.Spectator }.keys

        val playerResponses = players.associateWith {
            val client = channel.clientToPlayerId[it]!! // HACK: Weird, because userId shows more intent.
            buildPlayerView(result, channel.clientToPlayerId[requester.auth.id]!!, client)
        }
        val spectatorResponses = spectators.associateWith {
            buildSpectatorView(result)
        }
        return playerResponses + spectatorResponses
    }

    abstract fun buildPlayerView(event: E, requestPlayer: Int, playerNumber: Int): Response
    abstract fun buildSpectatorView(event: E): Response
}
