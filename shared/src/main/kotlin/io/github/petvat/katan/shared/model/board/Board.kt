package io.github.petvat.katan.shared.model.board

import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.protocol.dto.BoardDTO
import io.github.petvat.katan.shared.protocol.dto.Transmittable
import kotlinx.serialization.Serializable


/**
 * Contains all data of piece locations on the board.
 */
data class Board(
    val tiles: MutableList<Tile>,
    val intersections: Collection<Intersection> = emptyList(),
    val paths: Collection<Edge> = emptyList(),
    val robberLocation: HexCoordinates
)
