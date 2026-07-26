package io.github.petvat.katan.server.service.presenter

import io.github.petvat.katan.server.service.engine.GameSnapshot
import io.github.petvat.katan.server.service.engine.Player
import io.github.petvat.katan.server.service.engine.tradesystem.Trade
import io.github.petvat.katan.shared.protocol.dto.*

fun Trade.toDto(): TradeDTO = TradeDTO(
    tradeId = id,
    initiator = initiator.number,
    targetPlayers = targets,
    offer = offer.toDto(),
    inReturn = inReturn.toDto()
)

fun Player.toPublicDto(): PlayerPublicDTO {
    return PlayerPublicDTO(
        number = number,
        resourceCardCount = resources.count(),
        victoryPointCount = victoryPoints,
        devCardCount = -1 // TODO: FIX
    )
}

fun Player.toDto(): PlayerDTO {
    return PlayerDTO(
        number = number,
        resources = resources.toDto(),
        victoryPointCount = victoryPoints,
        citiesLeft = citiesLeft,
        roadsLeft = roadsLeft,
        settlementsLeft = settlementsLeft
    )
}

fun GameSnapshot.toPlayerDTO(viewer: Int): GameSnapshotPlayerDTO {
    val player = players.first { it.number == viewer }
    return GameSnapshotPlayerDTO(
        player = player.toDto(),
        board = board.toDto(),
        otherPlayers = players.map { it.toPublicDto() },
        phase = phase,
        turnOrder = turnOrder,
        turnPlayer = turnPlayer,
        ongoingTrades = ongoingTrades.map { it.toDto() }
    )
}

fun GameSnapshot.toSpectatorDTO(): GameSnapshotSpectatorDTO = GameSnapshotSpectatorDTO(
    board = board.toDto(),
    players = players.map { it.toPublicDto() },
    phase = phase,
    turnOrder = turnOrder,
    turnPlayer = turnPlayer,
    ongoingTrades = ongoingTrades.map { it.toDto() }
)

