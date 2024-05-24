package io.github.petvat.katan.server.board

/**
 * Active path, i.e. path that has a road structure built on it
 * @see Road
 */
data class Path(
    val coordinate: Coordinate,
    val road: Road
)
