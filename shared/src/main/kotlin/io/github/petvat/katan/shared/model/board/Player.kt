package io.github.petvat.katan.shared.model.board

import io.github.petvat.katan.shared.User
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.dto.PlayerColor


/**
 * This class represents the player in a game.
 *
 * TODO: Move to server.
 *
 * TODO: PlayerViewInterface
 *
 * @property id The id of this class should be the same as the [User]-id associated with this player.
 */
data class Player(
    val id: String,
    val playerNumber: Int,
    val color: PlayerColor
) {
    var inventory = ResourceMap(0, 0, 0, 0, 0)
    var victoryPoints = 0
    var settlementCount = 0
    var cityCount = 0
    var roadCount = 0
}


