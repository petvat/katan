package io.github.petvat.katan.shared.hexlib

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val logger = KotlinLogging.logger { }

/**
 * Utility functions for hexagonal grids.
 */
object HexUtils {

    /**
     * Direction vectors for adjacent hexes (single coordinates), from top-left clockwise.
     */
    private val directionVectors = arrayOf(
        -1, 0, // top-left
        0, 1, // top-right
        1, 1, // right
        1, 0, // bottom-right
        0, -1, // bottom-left
        -1, -1 // left
    )

    /**
     * Offsets from a doubled hex coordinate to its 6 intersection coordinates.
     */
    private val intersectionOffsets = arrayOf(
        -1, 0,    // top-left
        0, 1,   // top
        1, 2,   // top-right
        2, 1,   // bottom-right
        1, 0,   // bottom
        0, -1,   // bottom-left
    )

    /**
     * Transforms single axial hex coordinates to doubled form (q*2, r*2).
     * Required before computing intersection/edge coordinates.
     */
    fun transformToDoubled(hexes: List<HexCoord>): List<HexCoord> {
        return hexes.map { HexCoord(it.q * 2, it.r * 2) }.toList()
    }

    /**
     * Transforms single axial hex coordinates to doubled form (q*2, r*2).
     * Required before computing intersection/edge coordinates.
     */
    private fun transformToDoubled(hex: HexCoord): HexCoord {
        return HexCoord(hex.q * 2, hex.r * 2)
    }

    /**
     * Transforms doubled coordinates back to single axial form.
     * Used when passing coordinates back into hex operations that expect single form
     * (e.g. robber location).
     */
    private fun transformToSingle(hexes: Collection<HexCoord>): MutableList<HexCoord> {
        return hexes.map { HexCoord(it.q / 2, it.r / 2) }
            .toMutableList()
    }

    private fun isDoubled(hex: HexCoord): Boolean {
        // A hex is in doubled form if both q and r are even.
        return hex.q % 2 == 0 && hex.r % 2 == 0
    }

    /**
     * Returns the shared coordinate value between two edge coordinate pairs,
     * or null if no value is shared.
     *
     * TODO: For intersections swap!
     */
    private fun sharedValue(a1: Int, a2: Int, b1: Int, b2: Int): Int? {
        return when {
            a1 == b1 || a1 == b2 -> a1
            a2 == b1 || a2 == b2 -> a2
            else -> null
        }
    }

    /**
     * Returns the edge that connects two adjacent nodes.
     *
     * @param a First node coordinate
     * @param b Second node coordinate
     * @return The edge linking them, or null if they are not adjacent (i.e., do not share an edge).
     */
    fun edgeLinkingNodes(a: NodeCoord, b: NodeCoord): EdgeCoord? {
        // Candidate edge: component-wise minimum
        val candidate = EdgeCoord(
            q = minOf(a.q, b.q),
            r = minOf(a.r, b.r)
        )

        // Verify that both nodes actually lie on this edge
        val endpoints = nodesLinkingEdge(candidate)
        return if (a in endpoints && b in endpoints) candidate else null
    }


    /**
     * Computes the edge coordinate linking two adjacent intersection coordinates.
     *
     * TODO: Changes!
     * Find the denominating (shared) value.
     * - Even: edge = intersection with highest (q+r) sum.
     * - Odd:  edge = (a1-1, a2) if a1 > a2, else (a1, a2-1).
     */
//    fun getAdjacentEdge(a: ICoordinates, b: ICoordinates): EdgeCoordinates {
//        val dom = sharedValue(a.q, a.r, b.q, b.r) // TODO: Not correct check!
//            ?: throw IllegalArgumentException("Intersections $a and $b are not adjacent.")
//
//        if (dom % 2 == 0) {
//            val aSum = a.q + a.r
//            val bSum = b.q + b.r
//            return if (aSum > bSum) EdgeCoordinates(a.q, a.r) else EdgeCoordinates(b.q, b.r)
//        }
//        return if (a.q > a.r) {
//            EdgeCoordinates(a.q - 1, a.r)
//        } else {
//            EdgeCoordinates(a.q, a.r - 1)
//        }
//    }


    fun nodesLinkingEdge(edge: EdgeCoord): List<NodeCoord> {
        return if (edge.q % 2 == 0 && edge.r % 2 == 0) {
            listOf(NodeCoord(edge.q, edge.r + 1), NodeCoord(edge.q + 1, edge.r))
        } else listOf(NodeCoord(edge.q, edge.r), NodeCoord(edge.q + 1, edge.r + 1))
    }


    /**
     * Finds the single node that connects two distinct edges.
     *
     * @param edge1 First edge
     * @param edge2 Second edge
     * @return The common [NodeCoord] if the edges share exactly one node, otherwise null.
     *         Returns null if the edges are the same edge (share 2 nodes) or if they don't touch.
     */
    fun nodeLinkingEdges(edge1: EdgeCoord, edge2: EdgeCoord): NodeCoord? {
        val nodes1 = nodesLinkingEdge(edge1).toSet()
        val nodes2 = nodesLinkingEdge(edge2).toSet()
        val common = nodes1.intersect(nodes2)

        return when (common.size) {
            1 -> common.first()   // Exactly one common node – they meet at a corner
            else -> null          // 0 = disjoint, 2 = they are the exact same edge
        }
    }


    /**
     * Computes the intersection linking two edges.
     *
     * - Shared q: if shared is highest → (max.q+1, max.r), else max(A,B)
     * - Shared r: if shared is highest → (max.q, max.r+1), else max(A,B)
     * - No shared axis: max(A,B)
     *
     * max(A,B) is whichever edge has the higher (q+r) sum.
     */
//    fun getAdjacentIntersection(a: EdgeCoordinates, b: EdgeCoordinates): ICoordinates {
//        val maxEdge = if (a.q + a.r >= b.q + b.r) a else b
//
//        return when {
//            a.q == b.q -> {
//                val sharedIsHighest = a.q >= a.r && a.q >= b.r
//                if (sharedIsHighest) ICoordinates(maxEdge.q + 1, maxEdge.r)
//                else ICoordinates(maxEdge.q, maxEdge.r)
//            }
//
//            a.r == b.r -> {
//                val sharedIsHighest = a.r >= a.q && a.r >= b.q
//                if (sharedIsHighest) ICoordinates(maxEdge.q, maxEdge.r + 1)
//                else ICoordinates(maxEdge.q, maxEdge.r)
//            }
//
//            else -> ICoordinates(maxEdge.q, maxEdge.r)
//        }
//    }

    // -------------------------------------------------------------------------
    // Coordinate maps (logical -> screen)
    // -------------------------------------------------------------------------


    fun hexToPixel(l: Layout, coord: HexCoord): PCoord {
        // [x] = inradius.x * [a1 b1][q]
        // [y] = inradius.y   [c1 d1][r]
        val x = l.inradius.x * (l.a1 * coord.q + l.b1 * coord.r)
        val y = l.inradius.y * (l.c1 * coord.q + l.d1 * coord.r)

        return PCoord(x + l.origin.x, y + l.origin.y)
    }

    /**
     * Returns the pixel offset to a specific corner of a hex.
     * @param corner 0 is top-left, increments clockwise.
     */
    private fun hexCornerOffset(l: Layout, corner: Int): PCoord {
        val angle = 2.0 * PI * (l.startAngle + corner) / 6

        return PCoord(l.inradius.x * -cos(angle), l.inradius.y * sin(angle))
    }

    /**
     * Returns the 6 corner screen coordinates of a hex.
     */
    fun hexCorners(l: Layout, hex: HexCoord): List<PCoord> {
        val center = hexToPixel(l, hex)
        return (0 until 6).map { i ->
            val offset = hexCornerOffset(l, i)
            PCoord(center.x + offset.x, center.y + offset.y)
        }
    }

    /**
     * Computes the mapping from logical node coordinates to screen coordinates
     * for all nodes adjacent to the given (single) hex coordinates.
     *
     * Input hexes are in single axial form and are doubled internally.
     */
    fun nodeCoords(
        layout: Layout,
        hexCoord: List<HexCoord>
    ): MutableMap<NodeCoord, PCoord> {
        val intersectionMap = mutableMapOf<NodeCoord, PCoord>()

        for (i in hexCoord.indices) {
            val doubled = transformToDoubled(hexCoord[i])
            val center = hexToPixel(layout, hexCoord[i])

            for ((corner, j) in (intersectionOffsets.indices step 2).withIndex()) {
                val offset = hexCornerOffset(layout, corner)


                val icoord = NodeCoord(
                    (doubled.q) + intersectionOffsets[j],
                    (doubled.r) + intersectionOffsets[j + 1]
                )
                intersectionMap[icoord] = PCoord(center.x + offset.x, center.y + offset.y)

                println(
                    "hex=${hexCoord[i]} " +
                        "corner=$corner " +
                        "icoord=$icoord " +
                        "pixel=${center.x + offset.x}, ${center.y + offset.y}"
                )
            }
        }
        return intersectionMap
    }

    /**
     * Computes the mapping from logical edge coordinates to the pair of screen
     * coordinates representing the edge's two endpoint intersections.
     *
     */
//    fun edgeCoordinates(
//        layout: Layout,
//        hexCoordinates: List<HexCoordinates>
//    ): Map<EdgeCoordinates, Pair<PCoordinate, PCoordinate>> {
//        val dhexes = transformToDoubled(hexCoordinates)
//
//        return dhexes.flatMap { hex ->
//            val edges = transformToSingle(adjacentHexes(hex))
//                .map { EdgeCoordinates(it.q, it.r) }
//                .rotateLeft(1) // Because adjacent hex is top, while first edge is left
//
//            val corners = hexCorners(layout, hex)
//                // .rotateRight(1) // Because first adjacent is top-left
//                .windowed(2)
//
//            edges.indices.map { i ->
//                edges[i] to (corners[i][0] to corners[i][1])
//            }
//        }.toMap()
//    }


    fun edgeCoords(
        layout: Layout,
        hexCoords: List<HexCoord>
    ): Map<EdgeCoord, Pair<PCoord, PCoord>> {
        val dhexes = transformToDoubled(hexCoords)

        return dhexes.flatMap { hex ->
            val edges = transformToSingle(hexesTouchingHex(hex))
                .map { EdgeCoord(it.q, it.r) }
                .toMutableList()
                .also {
                    Collections.rotate(
                        it,
                        -1
                    )
                } // left by 1 NOTE: If no rotation, we should start with edge:NW

            val corners = hexCorners(layout, hex)

            // windowed(2) only gives 5 pairs from 6 corners, missing the wrap-around
            // pair [corner5, corner0]. Build all 6 consecutive pairs manually.
            val cornerPairs = (0 until 6).map { i ->
                corners[i] to corners[(i + 1) % 6]
            }

            edges.indices.map { i ->
                edges[i] to cornerPairs[i]
            }
        }.toMap()
    }

    /**
     * Generates a hexagonal-shaped map with [width] hexes along the widest axis.
     */
    fun generateHexagonalMap(width: Int): Collection<HexCoord> {
        val hexes: MutableList<HexCoord> = mutableListOf()
        hexes.add(HexCoord(0, 0))

        val offsets = directionVectors.toList()
        Collections.rotate(offsets, -4)

        for (k in 1..width) {
            for (i in directionVectors.indices step 2) {
                val hexOffsetQ = directionVectors[i] * k
                val hexOffsetR = directionVectors[i + 1] * k
                var dirQ = 0
                var dirR = 0
                repeat(k) {
                    hexes.add(HexCoord(hexOffsetQ + dirQ, hexOffsetR + dirR))
                    dirQ += offsets[i]
                    dirR += offsets[i + 1]
                }
            }
        }
        return hexes
    }

    /**
     * Returns all hex coordinates forming a ring at [width] steps from origin.
     */
    fun hexRing(width: Int): List<HexCoord> {
        val hexes: MutableList<HexCoord> = mutableListOf()
        val offsets = directionVectors.toList()
        Collections.rotate(offsets, -4) // HACK: ???

        for (i in directionVectors.indices step 2) {
            val hexOffsetQ = directionVectors[i] * width
            val hexOffsetR = directionVectors[i + 1] * width
            var dirQ = 0
            var dirR = 0
            repeat(width) {
                hexes.add(HexCoord(hexOffsetQ + dirQ, hexOffsetR + dirR))
                dirQ += offsets[i]
                dirR += offsets[i + 1]
            }
        }
        return hexes
    }

    /**
     * Maps a collection of hex coordinates to their screen coordinates.
     */
    fun screenHexCoordinates(
        layout: Layout,
        hexes: Collection<HexCoord>
    ): MutableList<PCoord> {
        return hexes.map { hexToPixel(layout, it) }.toMutableList()
    }


    /**
     * Returns adjacent hexes to a hex.
     */
    fun hexesTouchingHex(a: HexCoord): List<HexCoord> {
        return (directionVectors.indices step 2).map { i ->
            HexCoord(a.q + directionVectors[i], a.r + directionVectors[i + 1])
        }
    }

    /**
     * Returns the (up to 3) hex coordinates adjacent to an intersection.
     * Input is a doubled intersection coordinate.
     *
     * Two cases based on parity of q:
     * - q odd  ("pointy" vertex): offsets are (-1,0), (1,0), (0,-2)
     * - q even ("flat"  vertex): offsets are (-2,-1), (0,1), (0,-1)
     */
    fun hexesTouchingNode(a: NodeCoord): List<HexCoord> {
        val offsets = if (a.q % 2 == 0) {

            // q even - top
            arrayOf(
                -2, -1,
                0, 1,
                0, -1
            )
        } else {
            // q odd - bottom
            arrayOf(
                -1, -2,
                -1, 0,
                1, 0
            )
        }
        return (offsets.indices step 2).map { i ->
            HexCoord(a.q + offsets[i], a.r + offsets[i + 1])
        }
    }

    /**
     * Returns the 6 intersection coordinates adjacent to a (doubled) hex coordinate.
     * Doubles the coordinate internally if it is not already doubled.
     */
    fun adjacentIntersections(hexCoordinate: HexCoord): List<NodeCoord> {
        val coord = if (!isDoubled(hexCoordinate)) transformToDoubled(hexCoordinate) else hexCoordinate
        return (intersectionOffsets.indices step 2).map { i ->
            NodeCoord(coord.q + intersectionOffsets[i], coord.r + intersectionOffsets[i + 1])
        }
    }

    /**
     * Returns the (up to 3) edge coordinates adjacent to an intersection,
     * filtered to only those that exist on the given board.
     */
    fun getAdjacentPaths(icoord: NodeCoord, hexes: List<HexCoord>): List<EdgeCoord> {
        val offsets = if (icoord.q % 2 == 0) {
            // pointy (top/bottom) vertex
            arrayOf(
                0, 0,
                -1, -1,
                -1, 0
            )
        } else {
            // flat (side) vertex
            arrayOf(
                0, 0,
                0, -1,
                -1, -1
            )
        }
        return calculateOffsets(icoord, offsets)
            .map { (q, r) -> EdgeCoord(q, r) }
            .filter { isValidCoordinate(it, hexes) }
    }

    /**
     * Returns the (up to 4) edge coordinates adjacent to an edge.
     * Edge orientation is inferred from parity of coordinates.
     */
    fun getAdjacentPaths(ecoord: EdgeCoord): List<EdgeCoord> {
        // Three orientations: horizontal (|), downward (\), upward (/)
        val horizontalOffsets = arrayOf(-1, 0, 0, 1, 0, -1, 1, 0)
        val downOffsets = arrayOf(-1, 0, -1, -1, 1, 0, 1, 1)
        val upOffsets = arrayOf(0, 1, 1, 1, -1, -1, 0, -1)

        val offsets = if ((ecoord.q + ecoord.r) % 2 == 0) horizontalOffsets
        else if (ecoord.q % 2 == 0) downOffsets
        else upOffsets

        return calculateOffsets(ecoord, offsets).map { (q, r) -> EdgeCoord(q, r) }
    }

    /**
     * Returns the adjacent intersection coordinates to a given intersection,
     * filtered to those that exist on the given board.
     */
    fun getAdjacentIntersections(a: NodeCoord, hexes: List<HexCoord>): List<NodeCoord> {
        val adjacents = mutableListOf(
            NodeCoord(a.q + 1, a.r + 1),
            NodeCoord(a.q - 1, a.r - 1)
        )
        if (a.q % 2 == 0) {
            adjacents.add(NodeCoord(a.q - 1, a.r + 1))
        } else {
            adjacents.add(NodeCoord(a.q + 1, a.r - 1))
        }
        return adjacents.filter { isValidCoordinate(it, hexes) }
    }

    // -------------------------------------------------------------------------
    // Validity checks
    // -------------------------------------------------------------------------

    /**
     * Returns true if the intersection exists on the board, i.e. at least one of its
     * 3 adjacent hexes is present in [hexes].
     *
     * Adjacent hexes are derived from [hexesTouchingNode] which uses the correct parity
     * rule per the coordinate spec.
     */
    fun isValidCoordinate(icoord: NodeCoord, hexes: List<HexCoord>): Boolean {
        return hexesTouchingNode(icoord).any { it in hexes }
    }

    /**
     * Returns true if the edge exists on the board, i.e. at least one of its
     * 2 adjacent hexes is present in [hexes].
     *
     * Three edge orientations detected by coordinate parity, matching [getAdjacentPaths].
     */
    fun isValidCoordinate(ecoord: EdgeCoord, hexes: List<HexCoord>): Boolean {
        val horizontalOffsets = arrayOf(-1, -1, 1, 1)
        val downOffsets = arrayOf(0, -1, 0, 1)
        val upOffsets = arrayOf(-1, 0, 1, 0)

        val offsets = if (ecoord.q + ecoord.r % 2 == 0) horizontalOffsets
        else if (ecoord.q % 2 == 0) downOffsets
        else upOffsets

        return calculateOffsets(ecoord, offsets)
            .map { (q, r) -> HexCoord(q, r) }
            .any { it in hexes }
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Applies a flat array of (q,r) offset pairs to a base coordinate,
     * returning a list of (q,r) result pairs.
     *
     * @return A list of offsets as pairs of integers
     */
    private fun calculateOffsets(
        coordinate: Coordinates,
        offsets: Array<Int>
    ): List<Pair<Int, Int>> {
        return (offsets.indices step 2).map { i ->
            coordinate.q + offsets[i] to coordinate.r + offsets[i + 1]
        }
    }


    // TODO: Use Collections.rotate
    fun <T> List<T>.rotateLeft(n: Int): List<T> =
        drop(n) + take(n)

    fun <T> List<T>.rotateRight(n: Int): List<T> =
        takeLast(n) + dropLast(n)

    // -------------------------------------------------------------------------
    // Obsolete / superseded methods
    // -------------------------------------------------------------------------

    /*
     * intersectionToCorner — superseded by intersectionCoordinates().
     * The logic was partially correct but required the full hex list as input and
     * used a hardcoded offset table that didn't match the final coordinate spec.
     *
    fun intersectionToCorner(
        layout: Layout,
        hexCoordinates: List<HexCoordinates>,
        icoord: ICoordinates
    ): PCoordinate? { ... }
    */

    /*
     * edgeMappings — unfinished, superseded by edgeCoordinates() TODO.
     * Had a bug: midpoint y used pc2.x instead of pc2.y.
     *
    fun edgeMappings(
        layout: Layout,
        intersectionCoordinates: List<Pair<ICoordinates, PCoordinate>>
    ) { ... }
    */

    /*
     * getAdjacentHexes (private overload) — redundant with adjacentHexes(ICoordinates)
     * plus a filter. Kept inline at the one call site that needed it.
     *
    private fun getAdjacentHexes(
        intersectionCoordinate: ICoordinates,
        hexes: List<HexCoordinates>
    ): List<HexCoordinates> { ... }
    */
}

fun main() {
    val l = Layout(PCoord(10.0, 10.0), PCoord(0.0, 0.0))
    val coord = HexCoord(0, 0)
    println("Corners of (0,0): ${HexUtils.hexCorners(l, coord)}")
    println("Intersections of (0,0): ${HexUtils.nodeCoords(l, listOf(coord))}")


    println(HexUtils.generateHexagonalMap(3))
}
