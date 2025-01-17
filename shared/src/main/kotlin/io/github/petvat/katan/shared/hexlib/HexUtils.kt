package io.github.petvat.katan.shared.hexlib

import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


/**
 * Utility functions for hexagons.
 */
object HexUtils {

    /**
     * From left, clockwise
     */
    private val directionVectors = arrayOf(
        -1, 0,
        0, -1,
        1, -1,
        1, 0,
        0, 1,
        -1, 1
    )

    /**
     * To support edges and vertices
     */
    val directionVectorsDoubled = directionVectors.map { it * 2 }.toList()

    /**
     * Intersection for *doubled* direction vectors
     */
    private val intersectionOffsets = arrayOf(
        0, 0,
        3, -1,
        0, 2,
        1, 1,
        -2, 2,
        1, -1
    )

    /**
     * Transform direction vectors to doubled direction vectors to support intersections and edges
     */
    fun transformToDoubled(hexes: Collection<HexCoordinates>): MutableList<HexCoordinates> {
        return hexes.map { HexCoordinates(it.q * 2, it.r * 2) }.toMutableList()
    }

    fun transformToDoubled(hex: HexCoordinates): HexCoordinates {
        return HexCoordinates(hex.q, hex.r)
    }

    /**
     * Transform doubled direction vectors back to single direction vectors so that hexagon operations works properly.
     *
     * E.g. robber location.
     *
     */
    fun transformToSingle(hexes: Collection<HexCoordinates>): MutableList<HexCoordinates> {
        return hexes.map { HexCoordinates(it.q / 2, it.r / 2) }.toMutableList()
    }

    /**
     * Transform logical coordinates to screen coordinate.
     *
     */
    fun hexToPixel(l: Layout, coord: HexCoordinates): PCoordinate {
        // [x] = inradius.x * [a1 b1][q]
        // [y] = inradius.y   [c1 d1][r]
        val x = l.inradius.x.toDouble() * (l.a1 * coord.q + l.b1 * coord.r)
        val y =
            l.inradius.y.toDouble() * (l.c1 * coord.q + l.d1 * coord.r) * -1 // HACK: A sign error somewhere invertes the y-axis
        return PCoordinate(x + l.origin.x, y + l.origin.y)
    }

    fun pixelToHex(l: Layout, coord: PCoordinate): HexCoordinates {
        TODO()
    }

    /**
     * @param corner 0 is top left
     *
     */
    private fun hexCornerOffset(l: Layout, corner: Int): PCoordinate {
        val angle = 2.0 * PI * (l.startAngle + corner) / 6
        println("corner: $corner")
        return PCoordinate(l.inradius.x * cos(angle), l.inradius.y * sin(angle))
    }

    /**
     * Returns corner coordinates of hex.
     */
    fun hexCorners(l: Layout, hex: HexCoordinates): List<PCoordinate> {
        val corners = mutableListOf<PCoordinate>()
        val center = hexToPixel(l, hex)
        for (i in 0 until 6) {
            val offset = hexCornerOffset(l, i)
//            println(i)
//            println(offset)
//            println()
            corners.add(PCoordinate(center.x + offset.x, center.y + offset.y))
        }
        return corners
    }


    /**
     * Converts a logical intersection coordinate to a screen corner coordinate
     *
     * Excepts *doubled* coordinates.
     *
     * @return Returns the screen corner coordinate. Returns null if this intersection cannot exist.
     *
     */
    fun intersectionToCorner(
        layout: Layout,
        hexCoordinates: List<HexCoordinates>,
        icoord: ICoordinates
    ): PCoordinate? {
        // get adjacent tile
        // There could be at most 3 adjacent hexes.
        // Depending on whether the intersection is at the pointy end or not, we get the following offsets:
        // TODO: Use adjacentHexes
        val offsets: Array<Int>
        var corner: Int
        if (icoord.q % 2 == 0) { // pointy
            offsets = arrayOf( // TODO: this is just intersectionOffsets odds
                0, 0,
                0, -2,
                2, -2
            )
            corner = 1 // Because top left is 0, and this would be the next.
        } else {
            offsets = arrayOf(
                -1, -1,
                -1, 1,
                -3, 1
            )
            corner = 4
        }

        for (i in offsets.indices.step(2)) {
            val hex = HexCoordinates(icoord.q + offsets[i], icoord.r + offsets[i + 1])
            if (hex in hexCoordinates) {
                return hexCornerOffset(layout, corner)
            }
            corner = (corner + 2) % 6 // we get 1, 3 and 5, or 4, 6 and 8.
        }
        return null
    }

    /**
     * Very simple algorithm right now, TODO: make better algorithm
     *
     * Computes mappings between logical and screen coordinates for all intersections.
     *
     */
    fun intersectionCoordinates(
        layout: Layout,
        hexCoordinates: List<HexCoordinates>
    ): MutableMap<ICoordinates, PCoordinate> {
        // TODO: For each hex coordinate, Map<Logical, Screen>
        val intersectionMap = mutableMapOf<ICoordinates, PCoordinate>()
        val doubledHexCoord = transformToDoubled(hexCoordinates)
        println(doubledHexCoord)

        for (i in hexCoordinates.indices) {
            var corner = 1
            for (j in intersectionOffsets.indices.step(2)) {
                val cornerCoord = hexCornerOffset(layout, corner)
                val icoord = ICoordinates(
                    doubledHexCoord[i].q + intersectionOffsets[j],
                    doubledHexCoord[i].r + intersectionOffsets[j + 1]
                )
                intersectionMap[icoord] = cornerCoord
                corner++
            }
        }
        return intersectionMap
    }


    /**
     *
     * Generate a map in the shape of a flat hexagon.
     *
     * @param width corresponds to the number of hexes in the "widest point".
     *
     */
    fun generateHexagonalMap(width: Int): Collection<HexCoordinates> {
        val hexes: MutableList<HexCoordinates> = mutableListOf()

        hexes.add(HexCoordinates(0, 0)) // Add origin hex manually as it does not work with the algorithm

        val offsets = directionVectors.toList()
        Collections.rotate(offsets, -4) // rotate 4

        for (k in 1..width) {
            for (i in directionVectors.indices step 2) {
                val hexOffsetQ = directionVectors[i] * k
                val hexOffsetR = directionVectors[i + 1] * k
                var dirQ = 0
                var dirR = 0
                repeat(k) {
                    hexes.add(HexCoordinates(hexOffsetQ + dirQ, hexOffsetR + dirR))
                    dirQ += offsets[i]
                    dirR += offsets[i + 1]
                }
            }
        }

        return hexes
    }

    fun hexRing(width: Int): List<HexCoordinates> {
        val hexes: MutableList<HexCoordinates> = mutableListOf()
        val offsets = directionVectors.toList()
        Collections.rotate(offsets, -4) // rotate 4

        for (i in directionVectors.indices step 2) {
            val hexOffsetQ = directionVectors[i] * width
            val hexOffsetR = directionVectors[i + 1] * width
            var dirQ = 0
            var dirR = 0
            repeat(width) {
                hexes.add(HexCoordinates(hexOffsetQ + dirQ, hexOffsetR + dirR))
                dirQ += offsets[i]
                dirR += offsets[i + 1]
            }
        }
        return hexes
    }

    /**
     * Convert logical hex coordinates to screen coordinates using layout.
     */
    fun screenHexCoordinates(
        layout: Layout,
        hexes: Collection<HexCoordinates>
    ): MutableList<PCoordinate> {
        return hexes.map { hexToPixel(layout, it) }.toMutableList()
    }


    fun adjacentHexes(icoord: ICoordinates): List<HexCoordinates> {
        val hexes = mutableListOf<HexCoordinates>()
        val offsets = if (icoord.q % 2 == 0) { // pointy
            arrayOf( // TODO: this is just intersectionOffsets odds
                0, 0,
                0, -2,
                2, -2
            )
        } else {
            arrayOf(
                -1, -1,
                -1, 1,
                -3, 1
            )
        }
        for (i in offsets.indices.step(2)) {
            hexes.add(HexCoordinates(icoord.q + offsets[i], icoord.r + offsets[i + 1]))
        }
        return hexes
    }

    /**
     *
     */
    fun adjacentIntersections(hexCoordinate: HexCoordinates): List<ICoordinates> {
        var coord = hexCoordinate
        val intersections = mutableListOf<ICoordinates>()
        if (!isDoubled(coord)) {
            // TODO: FIX
            coord = transformToDoubled(coord)
        }
        for (i in intersectionOffsets.indices.step(2)) {
            intersections.add(
                ICoordinates(
                    coord.q + intersectionOffsets[i],
                    coord.r + intersectionOffsets[i + 1]
                )
            )
        }
        return intersections
    }

    private fun isDoubled(hex: HexCoordinates): Boolean {
        return hex.q % 2 == 0
    }
}

fun main() {
    val l = Layout(PCoordinate(10.0, 10.0), PCoordinate(0.0, 0.0))
    val coord = HexCoordinates(0, 0)
    HexUtils.hexCorners(l, coord)
    val icoord = ICoordinates(1, -1)
    val icoord2 = ICoordinates(0, 2)
    println(HexUtils.intersectionToCorner(l, listOf(coord), icoord))
    println(HexUtils.intersectionToCorner(l, listOf(coord), icoord2))
    println(HexUtils.intersectionCoordinates(l, listOf(coord)))
}


