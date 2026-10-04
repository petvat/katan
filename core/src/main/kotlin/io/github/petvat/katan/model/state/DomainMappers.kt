package io.github.petvat.katan.model.state

import io.github.petvat.katan.shared.protocol.dto.ParticipantGameSnapshot
import io.github.petvat.katan.shared.protocol.dto.toDomain

fun ParticipantGameSnapshot.toDomain(id: String): GameSession = GameSession(
    id = id,
    player = player.number,
    otherPlayers = otherPlayers.map { it.number },
    colors = colors,
    turnOrder = turnOrder,
    turnIndex = 0,
    resources = player.resources,
    otherResources = otherPlayers.associate { it.number to it.resourceCardCount },
    victoryPoints = otherPlayers.associate { it.number to it.victoryPointCount } + (player.number to player.victoryPointCount),
    phase = phase,
    winner = null,
    board = board.toDomain(),
    onGoingTrades = ongoingTrades,
    rules = rules,
)
