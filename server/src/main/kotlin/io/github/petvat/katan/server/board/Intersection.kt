package io.github.petvat.katan.server.board

/**
 * Active intersection, i.e. an intersection with a village.
 */
data class Intersection(
    val coordinate: Coordinate,
    val village: Village
)
