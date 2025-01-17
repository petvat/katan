package io.github.petvat.katan.shared.model.board

import io.github.petvat.katan.shared.User
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.session.PublicPlayerView
import kotlinx.serialization.Serializable


/**
 * This class represents the player in a game.
 *
 * TODO: Consistency model DTO
 *
 * @property id The id of this class should be the same as the [User]-id associated with this player.
 */
@Serializable
data class Player(
    val id: kotlin.String,
    val playerNumber: Int,
    val settings: Settings,
) {
    val inventory = ResourceMap(0, 0, 0, 0, 0)
    var victoryPoints: Int = 0
    var settlementCount: Int = settings.maxSettlements
    var cityCount: Int = settings.maxCities
    var roadCount: Int = settings.maxRoads
    val cardCount: Int = inventory.get().values.sum()
    // TODO: devCards


    fun toPublic(): PublicPlayerView {
        return PublicPlayerView(
            id = id,
            cardCount = cardCount,
            settlementCount = settlementCount,
            cityCount = cityCount,
            roadCount = roadCount,
            victoryPoints = victoryPoints
        )
    }


}
