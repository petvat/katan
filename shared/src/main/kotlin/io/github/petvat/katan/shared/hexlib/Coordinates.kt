package io.github.petvat.katan.shared.hexlib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Minimal physical coordinate on the screen.
 *
 */
data class PCoord(val x: Double, val y: Double)

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
data class HexCoord(@SerialName("hex_q") override val q: Int, @SerialName("hex_r") override val r: Int) :
    Coordinates

/**
 * Logical intersection coordinates.
 */
@Serializable
data class NodeCoord(
    @SerialName("node_q") override val q: Int,
    @SerialName("node_r") override val r: Int
) : Coordinates

/**
 * Logical edge coordinates.
 */
@Serializable
data class EdgeCoord(
    @SerialName("edge_q") override val q: Int,
    @SerialName("edge_r") override val r: Int
) : Coordinates
