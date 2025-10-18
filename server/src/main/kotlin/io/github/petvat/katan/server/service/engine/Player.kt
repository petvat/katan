package io.github.petvat.katan.server.service.engine

import io.github.petvat.katan.shared.model.game.ResourceMap

data class Player(
    val number: Int,
    val resources: ResourceMap,
    var victoryPoints: Int,
    var roadsLeft: Int,
    var citiesLeft: Int,
    var settlementsLeft: Int
)
