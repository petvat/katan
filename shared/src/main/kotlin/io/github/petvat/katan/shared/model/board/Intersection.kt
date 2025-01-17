package io.github.petvat.katan.shared.model.board

import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.protocol.dto.IntersectionView
import io.github.petvat.katan.shared.protocol.dto.Transmittable

/**
 * Active intersection, i.e. an intersection with a village.
 */
data class Intersection(
    val coordinate: ICoordinates,
    val village: Village
) : Transmittable {
    override fun fromDomain(): IntersectionView {
        return IntersectionView(coordinate, village.fromDomain())
    }

}
