package io.github.petvat.katan.shared.hexlib
//
//import java.util.*
//import kotlin.math.PI
//import kotlin.math.cos
//import kotlin.math.sin
//
//
///**
// * Utility functions for hexagons.
// */
//object HexUtils2 {
//
//    /**
//     * From left, clockwise
//     */
//    private val directionVectors = arrayOf(
//        -1, 0,
//        0, -1,
//        1, -1,
//        1, 0,
//        0, 1,
//        -1, 1
//    )
//
//    /**
//     * To support edges and vertices
//     */
//    val directionVectorsDoubled = directionVectors.map { it * 2 }.toList()
//
//
//    /**
//     * Intersection offset around hexagon with doubled coordinates.
//     */
//    private val intersectionOffsets = arrayOf(
//        0, 1,
//        1, 2,
//        2, 1,
//        1, 0,
//        0, -1,
//        -1, 0,
//    )
//
//
//    /**
//     * Intersection for *doubled* direction vectors
//     */
////    private val intersectionOffsets = arrayOf(
////        0, 0,
////        3, -1,
////        0, 2,
////        1, 1,
////        -2, 2,
////        1, -1
////    )
//
//    /**
//     * Transform direction vectors to doubled direction vectors to support intersections and edges
//     */
//    fun transformToDoubled(hexes: Collection<HexCoord>): MutableList<HexCoord> {
//        return hexes.map { HexCoord(it.q * 2, it.r * 2) }.toMutableList()
//    }
//
//    fun transformToDoubled(hex: HexCoord): HexCoord {
//        return HexCoord(hex.q * 2, hex.r * 2)
//    }
//
//    /**
//     * Transform doubled direction vectors back to single direction vectors so that hexagon operations works properly.
//     *
//     * E.g. robber location.
//     *
//     */
//    fun transformToSingle(hexes: Collection<HexCoord>): MutableList<HexCoord> {
//        return hexes.map { HexCoord(it.q / 2, it.r / 2) }.toMutableList()
//    }
//
//    /**
//     * Transform logical coordinates to screen coordinate.
//     */
//    fun hexToPixel(l: Layout, coord: HexCoord): PCoord {
//        // [x] = inradius.x * [a1 b1][q]
//        // [y] = inradius.y   [c1 d1][r]
//        val x = l.inradius.x.toDouble() * (l.a1 * coord.q + l.b1 * coord.r)
//        val y =
//            l.inradius.y.toDouble() * (l.c1 * coord.q + l.d1 * coord.r) * -1 // HACK: A sign error somewhere invertes the y-axis
//        return PCoord(x + l.origin.x, y + l.origin.y)
//    }
//
//    fun pixelToHex(l: Layout, coord: PCoord): HexCoord {
//        TODO()
//    }
//
//    /**
//     * @param corner 0 is top left
//     *
//     */
//    private fun hexCornerOffset(l: Layout, corner: Int): PCoord {
//        val angle = 2.0 * PI * (l.startAngle + corner) / 6
//        println("corner: $corner")
//        return PCoord(l.inradius.x * cos(angle), l.inradius.y * sin(angle))
//    }
//
//    /**
//     * Returns corner coordinates of hex.
//     */
//    fun hexCorners(l: Layout, hex: HexCoord): List<PCoord> {
//        val corners = mutableListOf<PCoord>()
//        val center = hexToPixel(l, hex)
//        for (i in 0 until 6) {
//            val offset = hexCornerOffset(l, i)
////            println(i)
////            println(offset)
////            println()
//            corners.add(PCoord(center.x + offset.x, center.y + offset.y))
//        }
//        return corners
//    }
//
//
//    private fun sharedValue(a1: Int, a2: Int, b1: Int, b2: Int): Int? {
//        return when {
//            a1 == b1 || a1 == b2 -> a1
//            a2 == b1 || a2 == b2 -> a2
//            else -> null
//        }
//    }
//
//
//    /**
//     * NOTE: DONE
//     *
//     * Computes the edge coordinate linking two intersection coordinates.
//     * @param a
//     * @param b
//     * @return
//     */
//    fun getAdjacentEdge(a: NodeCoord, b: NodeCoord): EdgeCoord {
//        val dom = sharedValue(a.q, a.r, b.q, b.r)
//            ?: throw IllegalArgumentException("Intersections $a and $b are not adjacent.")
//
//        if (dom % 2 == 0) {
//            // The edge will be equal to the intersection of highest summed value.
//            val asum = a.q + a.r
//            val bsum = b.q + b.r
//            return if (asum > bsum) EdgeCoord(a.q, a.r) else EdgeCoord(b.q, b.r)
//        }
//        return if (a.q > a.r) {
//            EdgeCoord(a.q - 1, a.r)
//        } else EdgeCoord(a.q, a.r - 1)
//    }
//
//    /**
//     * NOTE: DONE
//     *
//     * Computes the intersection linking two edges.
//     */
//    fun getAdjacentIntersection(a: EdgeCoord, b: EdgeCoord): NodeCoord {
//
//        val (i1, i2) = if (a.q + a.r > b.q + b.r) {
//            a.q to a.r
//        } else b.q to b.r
//
//        val j = if (a.q == b.q && (a.q >= a.r && a.q >= b.r))
//            1 else 0
//        val k = if (a.r == b.r && (a.r >= a.q && a.r >= b.q))
//            1 else 0
//
//        return NodeCoord(i1 + j, i2 + k)
//    }
//
//
//    /**
//     * Converts a logical intersection coordinate to a screen corner coordinate
//     *
//     * Excepts *doubled* coordinates. TODO: Transform to doubled
//     *
//     * @return Returns the screen corner coordinate. Returns null if this intersection cannot exist.
//     *
//     */
//    fun intersectionToCorner(
//        layout: Layout,
//        hexCoordinates: List<HexCoord>,
//        icoord: NodeCoord
//    ): PCoord? {
//        // get adjacent tile
//        // There could be at most 3 adjacent hexes.
//        // Depending on whether the intersection is at the pointy end or not, we get the following offsets:
//        // TODO: Use adjacentHexes
//        val offsets: Array<Int>
//        var corner: Int
//        if (icoord.q % 2 == 0) { // pointy
//            offsets = arrayOf( // TODO: this is just intersectionOffsets odds
//                0, 0,
//                0, -2,
//                2, -2
//            )
//            corner = 1 // Because top left is 0, and this would be the next.
//        } else {
//            offsets = arrayOf(
//                -1, -1,
//                -1, 1,
//                -3, 1
//            )
//            corner = 4
//        }
//
//        for (i in offsets.indices.step(2)) {
//            val hex = HexCoord(icoord.q + offsets[i], icoord.r + offsets[i + 1])
//            if (hex in hexCoordinates) {
//                return hexCornerOffset(layout, corner)
//            }
//            corner = (corner + 2) % 6 // we get 1, 3 and 5, or 4, 6 and 8.
//        }
//        return null
//    }
//
//
//    /**
//     *
//     *
//     * Very simple algorithm right now, TODO: make better algorithm
//     *
//     * Computes mappings between logical and screen coordinates for all intersections.
//     *
//     * @note Transforms to doubled for intersection format calculation.
//     *
//     */
//    fun intersectionCoordinates(
//        layout: Layout,
//        hexCoordinates: List<HexCoord>
//    ): MutableMap<NodeCoord, PCoord> {
//        // TODO: For each hex coordinate, Map<Logical, Screen>
//        val intersectionMap = mutableMapOf<NodeCoord, PCoord>()
//        val doubledHexCoord = transformToDoubled(hexCoordinates)
//        println(doubledHexCoord)
//
//        for (i in hexCoordinates.indices) {
//            var corner = 1
//            for (j in intersectionOffsets.indices.step(2)) {
//                val cornerCoord = hexCornerOffset(layout, corner)
//                val icoord = NodeCoord(
//                    doubledHexCoord[i].q + intersectionOffsets[j],
//                    doubledHexCoord[i].r + intersectionOffsets[j + 1]
//                )
//                intersectionMap[icoord] = cornerCoord
//
//                corner++
//            }
//        }
//        return intersectionMap
//    }
//
//    /**
//     * Computes mappings between logical and screen coordinates for all edges.
//     * The physical edge coordinate is represented by the two intersections that links the edge.
//     */
//    fun edgeCoordinates(
//        layout: Layout,
//        hexCoordinate: List<HexCoord>
//    ): MutableMap<EdgeCoord, Pair<PCoord, PCoord>> {
//        TODO()
//    }
//
//
////    /**
////     * Returns the edge linking two adjacent intersection coordinates.
////     */
////    fun getAdjacentEdge(icoord1: ICoordinates, icoord2: ICoordinates): EdgeCoordinates {
////        // Opt1
////        // Find the 2 hexes that icoord1 and icoord2 is touching, then take hexA - HexB
////
////    }
//
//    fun edgeMappings(
//        layout: Layout,
//        intersectionCoordinates: List<Pair<NodeCoord, PCoord>>
//    ) {
//
//        val edges = mutableMapOf<EdgeCoord, PCoord>()
//
//        for (i in intersectionCoordinates.indices step 2) {
//
//            val (ic1, pc1) = intersectionCoordinates[i]
//            val (ic2, pc2) = intersectionCoordinates[i + 1]
//
//            val midpoint = { coord1: Double, coord2: Double -> (coord1 + coord2) / 2 }
//
//            edges[getAdjacentEdge(ic1, ic2)] = PCoord(midpoint(pc1.x, pc2.x), midpoint(pc1.y, pc2.x))
//
//        }
//
//    }
//
//
//    /**
//     *
//     * Generate a map in the shape of a flat hexagon.
//     *
//     * @param width corresponds to the number of hexes in the "widest point".
//     *
//     */
//    fun generateHexagonalMap(width: Int): Collection<HexCoord> {
//        val hexes: MutableList<HexCoord> = mutableListOf()
//
//        hexes.add(HexCoord(0, 0)) // Add origin hex manually as it does not work with the algorithm
//
//        val offsets = directionVectors.toList()
//        Collections.rotate(offsets, -4) // rotate 4
//
//        for (k in 1..width) {
//            for (i in directionVectors.indices step 2) {
//                val hexOffsetQ = directionVectors[i] * k
//                val hexOffsetR = directionVectors[i + 1] * k
//                var dirQ = 0
//                var dirR = 0
//                repeat(k) {
//                    hexes.add(HexCoord(hexOffsetQ + dirQ, hexOffsetR + dirR))
//                    dirQ += offsets[i]
//                    dirR += offsets[i + 1]
//                }
//            }
//        }
//
//        return hexes
//    }
//
//    fun hexRing(width: Int): List<HexCoord> {
//        val hexes: MutableList<HexCoord> = mutableListOf()
//        val offsets = directionVectors.toList()
//        Collections.rotate(offsets, -4) // rotate 4
//
//        for (i in directionVectors.indices step 2) {
//            val hexOffsetQ = directionVectors[i] * width
//            val hexOffsetR = directionVectors[i + 1] * width
//            var dirQ = 0
//            var dirR = 0
//            repeat(width) {
//                hexes.add(HexCoord(hexOffsetQ + dirQ, hexOffsetR + dirR))
//                dirQ += offsets[i]
//                dirR += offsets[i + 1]
//            }
//        }
//        return hexes
//    }
//
//    /**
//     * Convert logical hex coordinates to screen coordinates using layout.
//     */
//    fun screenHexCoordinates(
//        layout: Layout,
//        hexes: Collection<HexCoord>
//    ): MutableList<PCoord> {
//        return hexes.map { hexToPixel(layout, it) }.toMutableList()
//    }
//
//
//    /**
//     * Computes the hexes adjacent to an intersection.
//     */
//    fun adjacentHexes(a: NodeCoord): List<HexCoord> {
//        val hexes = mutableListOf<HexCoord>()
//        val offsets = if (a.q % 2 != 0) { // pointy
//            arrayOf(
//                -1, 0, // top
//                1, 0, // bottom right
//                0, -2 // bottom left
//            )
//        } else {
//            arrayOf(
//                -2, -1, // top left
//                0, 1, // top right
//                0, -1 // bottom
//            )
//        }
//        for (i in offsets.indices.step(2)) {
//            hexes.add(HexCoord(a.q + offsets[i], a.r + offsets[i + 1]))
//        }
//        return hexes
//
//    }
//
//
////    fun adjacentHexes(icoord: ICoordinates): List<HexCoordinates> {
////        val hexes = mutableListOf<HexCoordinates>()
////        val offsets = if (icoord.q % 2 == 0) { // pointy
////            arrayOf( // TODO: this is just intersectionOffsets odds
////                0, 0,
////                0, -2,
////                2, -2
////            )
////        } else {
////            arrayOf(
////                -1, -1,
////                -1, 1,
////                -3, 1
////            )
////        }
////        for (i in offsets.indices.step(2)) {
////            hexes.add(HexCoordinates(icoord.q + offsets[i], icoord.r + offsets[i + 1]))
////        }
////        return hexes
////    }
//
//    /**
//     * Get the adjancent intersections of a hex.
//     */
//    fun adjacentIntersections(hexCoordinate: HexCoord): List<NodeCoord> {
//        var coord = hexCoordinate
//        val intersections = mutableListOf<NodeCoord>()
//        if (!isDoubled(coord)) {
//            // TODO: FIX
//            coord = transformToDoubled(coord)
//        }
//        for (i in intersectionOffsets.indices.step(2)) {
//            intersections.add(
//                NodeCoord(
//                    coord.q + intersectionOffsets[i],
//                    coord.r + intersectionOffsets[i + 1]
//                )
//            )
//        }
//        return intersections
//    }
//
//    private fun isDoubled(hex: HexCoord): Boolean {
//        return hex.q % 2 == 0
//    }
//
//
//    fun getAdjacentIntersections(a: NodeCoord, hexes: List<HexCoord>): List<NodeCoord> {
//
//        val adjacents = mutableListOf<NodeCoord>()
//        adjacents.add(NodeCoord(a.q + 1, a.r + 1))
//        adjacents.add(NodeCoord(a.q - 1, a.r - 1))
//        if (a.q % 2 == 0) {
//            adjacents.add(NodeCoord(a.q - 1, a.r + 1))
//        } else {
//            adjacents.add(NodeCoord(a.q + 1, a.r - 1))
//        }
//        return adjacents.filter { isValidCoordinate(it, hexes) }
//
//    }
//
//    /**
//     * Get adjacents intersection coordinates to intersection
//     * Usecase: check valid settlement placements
//     */
////    fun getAdjacentIntersections(coord: ICoordinates, hexes: List<HexCoordinates>): List<ICoordinates> {
////        val adjacents: MutableList<ICoordinates> = mutableListOf()
////        adjacents.add(ICoordinates(coord.q + 1, coord.r + 1))
////        adjacents.add(ICoordinates(coord.q - 1, coord.r - 1))
////        if (coord.q % 2 == 0) {
////            adjacents.add(ICoordinates(coord.q - 1, coord.r + 1))
////        } else {
////            adjacents.add(ICoordinates(coord.q + 1, coord.r - 1))
////        }
////        return adjacents.filter { isValidCoordinate(it, hexes) }
////    }
//
//    private fun getAdjacentHexes(
//        intersectionCoordinate: NodeCoord,
//        hexes: List<HexCoord>
//    ): List<HexCoord> {
//        val adj = adjacentHexes(intersectionCoordinate)
//        return hexes.filter { it in adj }
//    }
//
//
//    /**
//     * Get adjacent edges to an intersection.
//     */
//    fun getAdjacentPaths(icoord: NodeCoord, hexes: List<HexCoord>): List<EdgeCoord> {
//
//        val offsets = if (icoord.q % 2 == 0) arrayOf(
//            // top
//            0, 0,
//            -1, -1,
//            -1, 0
//        ) else {
//            // bottom
//            arrayOf(
//                0, 0,
//                0, -1,
//                -1, -1
//            )
//        }
//        return calculateOffsets(icoord, offsets).map { (q, r) -> EdgeCoord(q, r) }
//            .filter { isValidCoordinate(it, hexes) }
//    }
//
//    /**
//     * @param offsets expects array to iterate over in pairs of 2
//     * @return
//     */
//    private fun calculateOffsets(
//        coordinate: Coordinates,
//        offsets: Array<Int>,
//
//        ): List<Pair<Int, Int>> {
//        val coordinateOffsets: MutableList<Pair<Int, Int>> = mutableListOf()
//        for (i in offsets.indices.step(2)) {
//            coordinateOffsets.add(
//                Pair(
//                    coordinate.q + offsets[i],
//                    coordinate.r + offsets[i + 1]
//                )
//            )
//        }
//        return coordinateOffsets
//    }
//
//
//    /**
//     * Get adjacent edges to an edge.
//     */
//    fun getAdjacentPaths(ecoord: EdgeCoord): List<EdgeCoord> {
//
//        // |
//        val horizontalOffsets = arrayOf(
//            -1, 0,
//            0, 1,
//            0, -1,
//            1, 0
//        )
//        // \
//        val downOffsets = arrayOf(
//            -1, 0,
//            -1, -1,
//            1, 0,
//            1, 1
//        )
//        // /
//        val upOffsets = arrayOf(
//            0, 1,
//            1, 1,
//            -1, -1,
//            0, -1
//        )
//
//        val offsets: Array<Int> = if (ecoord.q + ecoord.r % 2 == 0) {
//            // Path is horizontal
//            horizontalOffsets
//        } else if (ecoord.q % 2 == 0) {
//            // Path is downwards
//            downOffsets
//        } else {
//            // path is upwards
//            upOffsets
//        }
//        return calculateOffsets(ecoord, offsets).map { (q, r) -> EdgeCoord(q, r) }
//    }
//
//
//    /**
//     * Checks if an intersection coordinate exists on the board.
//     */
//    fun isValidCoordinate(icoord: NodeCoord, hexes: List<HexCoord>): Boolean {
//        val offsets = arrayOf(
//            0, 1,
//            -2, -1,
//            0, -1
//        )
//        val adjacentTiles: MutableList<HexCoord> = mutableListOf()
//
//        for (i in offsets.indices.step(2)) {
//            if (icoord.q % 2 == 0) {
//                adjacentTiles.add(HexCoord(icoord.q + offsets[i], icoord.r + offsets[i + 1]))
//            } else {
//                adjacentTiles.add(HexCoord(icoord.q + offsets[i + 1], icoord.r + offsets[i]))
//            }
//        }
//        return hexes.any { adjacentTiles.contains(it) }
//    }
//
//    /// TODO: update
//    fun isValidCoordinate(ecoord: EdgeCoord, hexes: List<HexCoord>): Boolean {
//
//        // |
//        val horizontalOffsets = arrayOf(
//            -1, -1,
//            1, 1
//        )
//        // \
//        val downOffsets = arrayOf(
//            0, -1,
//            0, 1
//        )
//        // upOffsets
//        val upOffsets = arrayOf(
//            -1, 0,
//            1, 0
//        )
//
//
//        val offsets = if (ecoord.q + ecoord.r % 2 == 0) {
//            horizontalOffsets
//        } else if (ecoord.q % 2 == 0) {
//            downOffsets
//        } else {
//            upOffsets
//        }
//        val adjacentTiles = calculateOffsets(ecoord, offsets).map { (q, r) -> HexCoord(q, r) }
//
//        return hexes.any { adjacentTiles.contains(it) }
//    }
//
//}
//
//fun main() {
//    val l = Layout(PCoord(10.0, 10.0), PCoord(0.0, 0.0))
//    val coord = HexCoord(0, 0)
//    HexUtils.hexCorners(l, coord)
//    val icoord = NodeCoord(1, -1)
//    val icoord2 = NodeCoord(0, 2)
//    // println(HexUtils.intersectionToCorner(l, listOf(coord), icoord))
//    // println(HexUtils.intersectionToCorner(l, listOf(coord), icoord2))
//    println(HexUtils.intersectionCoordinates(l, listOf(coord)))
//    HexUtils2.generateHexagonalMap(3)
//}
//
//
