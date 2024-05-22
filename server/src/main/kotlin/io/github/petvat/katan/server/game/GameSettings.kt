package io.github.petvat.katan.server.game

import io.github.petvat.katan.server.board.Player

data class GameSettings(
    val numPlayers: Int, val players: Set<Player>
) {
}