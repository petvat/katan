package io.github.petvat.katan.server.service.presenter.game

import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.GameChannel
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.server.service.presenter.toDto
import io.github.petvat.katan.server.service.presenter.toPublicDto
import io.github.petvat.katan.shared.protocol.Response
import io.github.petvat.katan.shared.protocol.dto.GameSnapshotPlayerDTO
import io.github.petvat.katan.shared.protocol.dto.GameSnapshotSpectatorDTO
import io.github.petvat.katan.shared.protocol.dto.toDto

class InitGamePresenter(channelRegistry: ChannelRegistry) : AbstractGamePresenter<GameEvent.Init>(channelRegistry) {

    override fun playerView(
        event: GameEvent.Init,
        requestPlayer: Int,
        playerNumber: Int,
        channel: GameChannel
    ): Response.Init {

        val snapshot = channel.snapshot

        return Response.Init(

            privateGameState = GameSnapshotPlayerDTO(
                player = snapshot.players.single { it.number == playerNumber }.toDto(),
                otherPlayers = snapshot.players.filter { it.number != playerNumber }.map { it.toPublicDto() }.toList(),
                turnPlayer = snapshot.turnPlayer,
                board = snapshot.board.toDto(),
                phase = snapshot.phase,
                ongoingTrades = snapshot.ongoingTrades.map { it.toDto() },
                turnOrder = snapshot.turnOrder
            ),
            // if (playerNumber == requestPlayer) "You started the game." else "$requestPlayer started the game.",
        )
    }

    override fun spectatorView(event: GameEvent.Init, channel: GameChannel): Response {
        val snapshot = channel.snapshot
        return Response.InitSpectator(
            GameSnapshotSpectatorDTO(
                players = snapshot.players.map { it.toPublicDto() }.toList(),
                turnPlayer = snapshot.turnPlayer,
                board = snapshot.board.toDto(),
                ongoingTrades = snapshot.ongoingTrades.map { it.toDto() },
                phase = snapshot.phase,
                turnOrder = snapshot.turnOrder
            ),
            // "Game started."
        )
    }

}
