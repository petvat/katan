package io.github.petvat.katan.shared.hexlib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Minimal physical coordinate on the screen.
 *
 */
data class PCoordinate(val x: Double, val y: Double)

@Serializable
sealed class Coordinates(open val q: Int, open val r: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Coordinates) return false
        return q == other.q && r == other.r
    }

    override fun hashCode(): Int {
        var result = q
        result = 31 * result + r
        return result
    }
}


/**
 * Logical coordiantes.
 */
@Serializable
class HexCoordinates(@SerialName("hex_q") override val q: Int, @SerialName("hex_r") override val r: Int) :
    Coordinates(q, r)

/**
 * Logical intersection coordinates.
 */
@Serializable
class ICoordinates(
    @SerialName("intersect_q") override val q: Int,
    @SerialName("intersect_r") override val r: Int
) : Coordinates(q, r)

/**
 * Logical edge coordinates.
 */
@Serializable
class EdgeCoordinates(
    @SerialName("edge_q") override val q: Int,
    @SerialName("edge_r") override val r: Int
) : Coordinates(q, r)


enum class CoordinateTypes {
    HEX, EDGE, INTERSECT
}
