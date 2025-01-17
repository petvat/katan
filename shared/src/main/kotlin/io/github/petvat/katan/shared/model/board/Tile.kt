package io.github.petvat.katan.shared.model.board

import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.model.game.Resource
import kotlinx.serialization.Serializable

/**
 * Represents a hexagonal tile on the board.
 * @param hexCoordinate board coordinates
 * @param resource May produce a resource
 * @param rollListenValue Will produce a resource if on this value
 */
@Serializable
data class Tile(
    val hexCoordinate: HexCoordinates,
    val resource: Resource?,
    val rollListenValue: Int
)
