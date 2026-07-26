package io.github.petvat.katan.server.service.engine

import io.github.petvat.katan.server.service.engine.tradesystem.Trade
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.shared.model.board.Board
import io.github.petvat.katan.shared.model.game.Phase

data class GameSnapshot(
    val rules: RuleBook, // TODO: Remove
    val players: List<Player>,
    val board: Board,
    val phase: Phase,
    val turnOrder: List<Int>,
    val turnPlayer: Int,
    val ongoingTrades: List<Trade> = emptyList(),
    val eventHistory: List<GameEvent>
)
