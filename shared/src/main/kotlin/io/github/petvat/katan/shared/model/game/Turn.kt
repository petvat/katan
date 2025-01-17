package io.github.petvat.katan.shared.model.game

data class Turn(val gameId: String, val playerId: Int, val turnNumber: Int) {
    var currentTrade: Trade? = null
    val trades: List<Trade> = mutableListOf()

}

