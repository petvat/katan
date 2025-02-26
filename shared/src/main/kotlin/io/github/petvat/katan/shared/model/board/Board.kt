package io.github.petvat.katan.shared.model.board

import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.protocol.dto.BoardDTO
import io.github.petvat.katan.shared.protocol.dto.Transmittable


/**
 * Contains all data of piece locations on the board.
 */
data class Board(
    var tiles: MutableList<Tile>,
    var intersections: Collection<Intersection> = emptyList(),
    var paths: Collection<Edge> = emptyList(),
    var robberLocation: HexCoordinates
)
