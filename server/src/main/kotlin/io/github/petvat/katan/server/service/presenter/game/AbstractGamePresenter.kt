package io.github.petvat.katan.server.service.presenter.game

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GameChannel
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.server.service.presenter.Presenter
import io.github.petvat.katan.server.service.channel.GameSubscriber
import io.github.petvat.katan.server.service.gateway.handleError
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response

/**
 * TODO: To model Client = app instance, use UserId in Game and Group. Find all members by UserId.
 */
abstract class AbstractGamePresenter<E : GameEvent>(
    private val channelRegistry: ChannelRegistry
) : Presenter<E> {

    override fun present(requester: ConnectedClient, result: E): Map<UserId, Response> {
        val channel =
            result.targetChannelId?.let { channelRegistry.get(it) } as? GameChannel ?: return handleError(
                requester.auth.id,
                ErrorCode.NOT_FOUND,
                ""
            )

        val players = channel.subs.filter { it.value == GameSubscriber.Player }.keys
        val spectators = channel.subs.filter { it.value == GameSubscriber.Spectator }.keys
        val snapshot = channel.snapshot

        val playerResponses = players.associateWith {
            val client = channel.userToPlayerId[it]!! // HACK: Weird, because userId shows more intent.
            playerView(result, channel.userToPlayerId[requester.auth.id]!!, client, channel)
        }
        val spectatorResponses = spectators.associateWith {
            spectatorView(result, channel)
        }
        return playerResponses + spectatorResponses
    }

    abstract fun playerView(event: E, requestPlayer: Int, playerNumber: Int, channel: GameChannel): Response

    // TODO: Default to buildPlayerView
    abstract fun spectatorView(event: E, channel: GameChannel): Response
}
