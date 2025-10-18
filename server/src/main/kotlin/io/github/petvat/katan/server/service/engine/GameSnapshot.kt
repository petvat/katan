package io.github.petvat.katan.server.service.engine

import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.shared.model.board.Board

data class GameSnapshot(
    val rules: RuleBook, // TODO: Remove
    val players: List<Player>,
    val board: Board,
    val phase: Phase,
    val turnOrder: List<Int>,
    val turnPlayer: Int,
    val eventHistory: List<GameEvent>
)
