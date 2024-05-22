package io.github.petvat.katan.server.board

import io.github.petvat.katan.server.Settings

data class Player(val ID: Int, var playerName: String, var userID: Int) {

    val inventory = ResourceMap(0, 0, 0, 0, 0)
    var victoryPoints: Int = 0
    var settlementCount: Int = io.github.petvat.katan.server.Settings.MAX_SETTLEMENTS
    var cityCount: Int = io.github.petvat.katan.server.Settings.MAX_CITIES
    var roadCount: Int = io.github.petvat.katan.server.Settings.MAX_ROADS
    val cardCount: Int = inventory.get().values.sum()
    // TODO: devCards
}
