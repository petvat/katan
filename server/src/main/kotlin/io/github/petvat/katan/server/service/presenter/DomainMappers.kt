package io.github.petvat.katan.server.service.presenter

import io.github.petvat.katan.server.service.engine.GameState
import io.github.petvat.katan.server.service.engine.Player
import io.github.petvat.katan.server.service.engine.tradesystem.TradeContext
import io.github.petvat.katan.shared.model.game.Trade
import io.github.petvat.katan.shared.protocol.dto.*

fun TradeContext.toDto() = Trade(
    id = id,
    initiator = initiator.number,
    targets = targets,
    offer = offer,
    inReturn = inReturn,
    acceptedBy = acceptedBy?.number,
    declinedBy = declinedBy
)

fun Player.toPublicDto(): PublicPlayer {
    return PublicPlayer(
        number = number,
        resourceCardCount = resources.total,
        victoryPointCount = victoryPoints,
        devCardCount = -1 // TODO: FIX
    )
}

fun Player.toDto(): PrivatePlayer {
    return PrivatePlayer(
        number = number,
        resources = resources,
        victoryPointCount = victoryPoints,
        citiesLeft = citiesLeft,
        roadsLeft = roadsLeft,
        settlementsLeft = settlementsLeft
    )
}

fun GameState.toPlayerDTO(viewer: Int): ParticipantGameSnapshot {
    val player = players.first { it.number == viewer }
    return ParticipantGameSnapshot(
        player = player.toDto(),
        board = board.toDto(),
        otherPlayers = players.map { it.toPublicDto() },
        phase = phase,
        turnOrder = turnOrder,
        turnPlayer = turnPlayer,
        ongoingTrades = ongoingTrades.map { it.toDto() },
        rules = rules,
        colors = colors
    )
}

fun GameState.toSpectatorDTO(): SpectatorGameSnapshot = SpectatorGameSnapshot(
    board = board.toDto(),
    players = players.map { it.toPublicDto() },
    phase = phase,
    turnOrder = turnOrder,
    turnPlayer = turnPlayer,
    ongoingTrades = ongoingTrades.map { it.toDto() },
    rules = rules
)

