package io.github.petvat.katan.server.service.engine

import io.github.petvat.katan.server.service.engine.tradesystem.TradeContext
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.shared.model.board.Board
import io.github.petvat.katan.shared.model.game.GameMeta
import io.github.petvat.katan.shared.model.game.Phase
import io.github.petvat.katan.shared.model.game.PlayerColor
import io.github.petvat.katan.shared.model.game.RuleBook

data class GameState(
    val rules: RuleBook, // TODO: Remove
    val players: List<Player>,
    val board: Board,
    val phase: Phase,
    val turnOrder: List<Int>,
    val turnPlayer: Int,
    val colors: Map<Int, PlayerColor>,
    val ongoingTrades: List<TradeContext> = emptyList(),
    val eventHistory: List<GameEvent>,
)
