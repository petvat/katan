package io.github.petvat.katan.server.board

/**
 * Active intersection, i.e. an intersection with a village.
 */
data class Intersection(
    val coordinate: io.github.petvat.katan.server.board.Coordinate,
    val village: io.github.petvat.katan.server.board.Village
) {

}
