package io.github.petvat.katan.shared.model.board

import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.protocol.dto.EdgeView
import io.github.petvat.katan.shared.protocol.dto.Transmittable

/**
 * Active edge, i.e. an edge with a road.
 */
data class Edge(
    val coordinate: EdgeCoordinates,
    val road: Road
) : Transmittable {
    override fun fromDomain(): EdgeView {
        return EdgeView(coordinate, road.fromDomain())
    }
}
