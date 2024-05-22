package io.github.petvat.katan.server.board


/**
 * Also hex maybe.
 *
 * Represents a tile on the board.
 *
 * Current impl: centralized
 *
 */
data class Tile(
    val coordinate: Coordinate,
    val resource: Resource?,
    val rollListenValue: Int,
)

enum class Terrain {
    FOREST, FIELDS, PASTURE, MOUNTAINS, HILLS, DESERT
}
