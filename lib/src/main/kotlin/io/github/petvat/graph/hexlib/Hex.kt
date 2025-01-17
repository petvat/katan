package io.github.petvat.graph.hexlib

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HexTile {

}

/**
 * Minimal coordinate on the screen.
 */
data class Coordinate(val x: Double, val y: Double)


/**
 * Logical coordiante.
 */
data class HexCoordiante(val q: Int, val r: Int)

/**
 * The width = 2 * inradius (r).
 * The inradius = circumradius (R) * cos(30 degrees) = sqrt(3)/2, i.e. difference between r and R.
 *
 * The width = 2 * r * sqrt(3)/2 = r * sqrt(3).
 *
 */


/**
 * Only support pointy top first.
 *
 */
data class Layout(
    val inradius: Coordinate,
    val origin: Coordinate,
) {
    // Hex to pixel linear transformation
    val a1 = sqrt(3.0)
    val b1 = sqrt(3.0) / 2.0
    val c1 = 0.0
    val d1 = 3.0 / 2.0

    // Pixel to Hex linear transformation
    val a2 = sqrt(3.0) / 3.0
    val b2 = -1.0 / 3.0
    val c2 = 0.0
    val d2 = 2.0 / 3.0

    val startAngle = 0.5
}


/**
 * Linear transformation.
 *
 */
fun hexToPixel(l: Layout, coord: HexCoordiante): Coordinate {
    val x = l.inradius.x.toDouble() * (l.a1 * coord.q + l.b1 * coord.r)
    val y = l.inradius.y.toDouble() * (l.c1 * coord.q + l.d1 * coord.r)
    return Coordinate(x + l.origin.x, y + l.origin.y)
}

private fun hexCornerOffset(l: Layout, corner: Int): Coordinate {
    val angle = 2.0 * PI * (l.startAngle + corner) / 6
    return Coordinate(l.inradius.x * cos(angle), l.inradius.y * sin(angle))
}

/**
 * Returns corner coordinates of hex.
 */
fun hexCorners(l: Layout, hex: HexCoordiante): List<Coordinate> {
    val corners: MutableList<Coordinate> = mutableListOf()
    val center = hexToPixel(l, hex)
    for (i in 0 until 6) {
        val offset = hexCornerOffset(l, i)
        corners.add(Coordinate(center.x + offset.x, center.y + offset.y))
    }
    return corners
}

fun main() {
    
}

class HexEdges {

}
