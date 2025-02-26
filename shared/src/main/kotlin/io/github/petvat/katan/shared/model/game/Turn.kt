package io.github.petvat.katan.shared.model.game


// TODO: Don't need this.
data class Turn(val gameId: String, val playerId: Int, val turnNumber: Int) {
    var currentTrade: Trade? = null
    val trades: List<Trade> = mutableListOf()

}

