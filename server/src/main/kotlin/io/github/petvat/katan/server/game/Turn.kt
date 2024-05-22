package io.github.petvat.katan.server.game

data class Turn(val gameID: Int, val playerID: Int, val turnNumber: Int) {
    var currentTrade: Trade? = null
    val trades: List<Trade> = mutableListOf()

}

enum class TurnPhase() {
    ROLL_DICE, MOVE_ROBBER, STEAL_CARD, BUILD_AND_TRADE
}
