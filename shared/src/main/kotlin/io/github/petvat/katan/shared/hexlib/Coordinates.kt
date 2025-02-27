package io.github.petvat.katan.shared.hexlib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Minimal physical coordinate on the screen.
 *
 */
data class PCoordinate(val x: Double, val y: Double)

/**
 * Represents a logical coordinate on the board.
 */
@Serializable
sealed interface Coordinates {
    val q: Int
    val r: Int
}


/**
 * Logical hexagon/tile coordiantes.
 */
@Serializable
class HexCoordinates(@SerialName("hex_q") override val q: Int, @SerialName("hex_r") override val r: Int) :
    Coordinates

/**
 * Logical intersection coordinates.
 */
@Serializable
class ICoordinates(
    @SerialName("intersect_q") override val q: Int,
    @SerialName("intersect_r") override val r: Int
) : Coordinates

/**
 * Logical edge coordinates.
 */
@Serializable
class EdgeCoordinates(
    @SerialName("edge_q") override val q: Int,
    @SerialName("edge_r") override val r: Int
) : Coordinates


fun main() {
    val hex = HexCoordinates(1, 2)
    val json = Json.encodeToString(hex)
    println(json)
}
