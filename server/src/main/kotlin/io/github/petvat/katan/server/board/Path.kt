package io.github.petvat.katan.server.board

/**
 * Active path, i.e. path that has a road structure built on it
 * @see Road
 */
data class Path(
    val coordinate: io.github.petvat.katan.server.board.Coordinate,
    val road: io.github.petvat.katan.server.board.Road
)
