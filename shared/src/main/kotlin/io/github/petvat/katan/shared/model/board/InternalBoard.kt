package io.github.petvat.katan.shared.model.board

import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.protocol.dto.BoardView
import io.github.petvat.katan.shared.protocol.dto.Transmittable


interface Board

/**
 * Contains all data of piece locations on the board.
 */
data class InternalBoard(
    //var players: Set<Player>, // TODO: Needed?
    var tiles: MutableList<Tile>,
    var intersections: Collection<Intersection> = emptyList(),
    var paths: Collection<Edge> = emptyList(),
    var robberLocation: HexCoordinates
) : Transmittable, Board {
    override fun fromDomain(): BoardView {
        return BoardView(
            tiles,
            intersections.map { it.fromDomain() },
            paths.map { it.fromDomain() },
            robberLocation
        )
    }

}
