package io.github.petvat.katan.shared.hexlib

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HexUtilsTest {

    // -------------------------------------------------------------------------
    // transformToDoubled
    // -------------------------------------------------------------------------

    @Nested
    inner class TransformToDoubled {


        @Test
        fun `positive coordinates are doubled`() {
            assertEquals(HexCoord(2, 4), HexUtils.transformToDoubled(HexCoord(1, 2)))
        }

        @Test
        fun `negative coordinates are doubled`() {
            assertEquals(HexCoord(-2, 0), HexUtils.transformToDoubled(HexCoord(-1, 0)))
        }

        @Test
        fun `collection overload doubles all hexes`() {
            val input = listOf(HexCoord(0, 0), HexCoord(1, 1), HexCoord(-1, 2))
            val expected = listOf(HexCoord(0, 0), HexCoord(2, 2), HexCoord(-2, 4))
            assertEquals(expected, HexUtils.transformToDoubled(input))
        }

        @Test
        fun `already-doubled collection is not doubled again`() {
            val already = listOf(HexCoord(0, 0), HexCoord(2, 2))
            assertEquals(already, HexUtils.transformToDoubled(already))
        }
    }

    // -------------------------------------------------------------------------
    // adjacentIntersections
    // Verified from diagram: hex (0,0) doubled → intersections
    // (0,1),(1,2),(2,1),(1,0),(0,-1),(-1,0)
    // -------------------------------------------------------------------------

    @Nested
    inner class AdjacentIntersections {

        @Test
        fun `six intersections around origin hex`() {
            assertEquals(
                setOf(
                    NodeCoord(0, 1),
                    NodeCoord(1, 2),
                    NodeCoord(2, 1),
                    NodeCoord(1, 0),
                    NodeCoord(0, -1),
                    NodeCoord(-1, 0)
                ),
                HexUtils.adjacentIntersections(HexCoord(0, 0)).toSet()
            )
        }

        @Test
        fun `single-form hex is doubled internally before applying offsets`() {
            val fromSingle = HexUtils.adjacentIntersections(HexCoord(1, 1)).toSet()
            val fromDoubled = HexUtils.adjacentIntersections(HexCoord(2, 2)).toSet()
            assertEquals(fromDoubled, fromSingle)
        }

        @Test
        fun `always returns exactly 6 intersections`() {
            assertEquals(6, HexUtils.adjacentIntersections(HexCoord(0, 0)).size)
            assertEquals(6, HexUtils.adjacentIntersections(HexCoord(2, 0)).size)
        }

        @Test
        fun `neighboring hexes share exactly 2 intersections`() {
            val hex1 = HexUtils.adjacentIntersections(HexCoord(0, 0)).toSet()
            val hex2 = HexUtils.adjacentIntersections(HexCoord(0, 2)).toSet()
            assertEquals(2, (hex1 intersect hex2).size)
        }
    }

    // -------------------------------------------------------------------------
    // adjacentHexes(ICoordinates)
    // Flat (even q): offsets (-2,-1),(0,1),(0,-1) — verified correct.
    // Pointy (odd q): correct third offset is (-1,-2), not (0,-2).
    // -------------------------------------------------------------------------

    @Nested
    inner class AdjacentHexesFromNode {

        @Test
        fun `flat intersection (0,1), top of origin, shared by (0,0),(-2,0),(0,2)`() {
            assertEquals(
                setOf(HexCoord(0, 0), HexCoord(-2, 0), HexCoord(0, 2)),
                HexUtils.hexesTouchingNode(NodeCoord(0, 1)).toSet()
            )
        }

        @Test
        fun `flat intersection (2,1), bottom-right of origin, shared by (0,0),(2,2),(2,0)`() {
            assertEquals(
                setOf(HexCoord(0, 0), HexCoord(2, 2), HexCoord(2, 0)),
                HexUtils.hexesTouchingNode(NodeCoord(2, 1)).toSet()
            )
        }

        @Test
        fun `pointy intersection (1,2), top-right of origin, shared by (0,0),(0,2),(2,2)`() {
            // Catches bug: old offset (0,-2) gives (1,0) which is an intersection, not a hex
            assertEquals(
                setOf(HexCoord(0, 0), HexCoord(0, 2), HexCoord(2, 2)),
                HexUtils.hexesTouchingNode(NodeCoord(1, 2)).toSet()
            )
        }

        @Test
        fun `pointy intersection (1,0), bottom of origin, shared by (0,0),(2,0),(0,-2)`() {
            // Catches same bug: old offset (0,-2) gives (1,-2) instead of (0,-2)
            assertEquals(
                setOf(HexCoord(0, 0), HexCoord(2, 0), HexCoord(0, -2)),
                HexUtils.hexesTouchingNode(NodeCoord(1, 0)).toSet()
            )
        }

        @Test
        fun `always returns exactly 3 adjacent hexes`() {
            assertEquals(3, HexUtils.hexesTouchingNode(NodeCoord(0, 1)).size)
            assertEquals(3, HexUtils.hexesTouchingNode(NodeCoord(1, 2)).size)
        }

        @Test
        fun `all adjacent hexes have even coordinates (are valid hex centers)`() {
            HexUtils.hexesTouchingNode(NodeCoord(1, 2)).forEach { hex ->
                assertTrue(hex.q % 2 == 0 && hex.r % 2 == 0) {
                    "Expected even coords for hex center, got $hex"
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // isValidCoordinate(ICoordinates)
    // -------------------------------------------------------------------------

    @Nested
    inner class IsValidNodeCoordinate {

        private val board = listOf(HexCoord(0, 0))

        @Test
        fun `all 6 intersections of the only hex on board are valid`() {
            listOf(
                NodeCoord(0, 1),
                NodeCoord(1, 2),
                NodeCoord(2, 1),
                NodeCoord(1, 0),
                NodeCoord(0, -1),
                NodeCoord(-1, 0)
            ).forEach { icoord ->
                assertTrue(HexUtils.isValidCoordinate(icoord, board)) {
                    "$icoord should be valid on board $board"
                }
            }
        }

        @Test
        fun `intersection not adjacent to any board hex is invalid`() {
            assertFalse(HexUtils.isValidCoordinate(NodeCoord(3, 3), board))
            assertFalse(HexUtils.isValidCoordinate(NodeCoord(-3, 0), board))
        }

        @Test
        fun `intersection shared by two hexes is valid when only one hex is on board`() {
            // (0,1) is shared by (0,0),(-2,0),(0,2) — only (0,0) is on board
            assertTrue(HexUtils.isValidCoordinate(NodeCoord(0, 1), board))
        }
    }

    // -------------------------------------------------------------------------
    // getAdjacentEdge
    // -------------------------------------------------------------------------

    @Nested
    inner class GetAdjacentEdge {

        @Test
        fun `shared value 1 odd, a1 not greater than a2 — uses (a1, a2-1)`() {
            // intersections (0,1) and (1,2): shared=1(odd), a=(0,1) → 0>1 false → (0,0)
            assertEquals(
                EdgeCoord(0, 0),
                HexUtils.getAdjacentEdge(NodeCoord(0, 1), NodeCoord(1, 2))
            )
        }

        @Test
        fun `shared value 1 odd, a1 not greater than a2 — uses (a1, a2-1) second case`() {
            // intersections (1,2) and (2,1): shared=1(odd), a=(1,2) → 1>2 false → (1,1)
            assertEquals(
                EdgeCoord(1, 1),
                HexUtils.getAdjacentEdge(NodeCoord(1, 2), NodeCoord(2, 1))
            )
        }

        @Test
        fun `shared value 0 even — edge is intersection with higher sum`() {
            // intersections (0,1) and (-1,0): shared=0(even), sums 1 vs -1 → edge=(0,1)
            assertEquals(
                EdgeCoord(0, 1),
                HexUtils.getAdjacentEdge(NodeCoord(0, 1), NodeCoord(-1, 0))
            )
        }

        @Test
        fun `is symmetric, argument order does not affect result`() {
            val a = NodeCoord(0, 1)
            val b = NodeCoord(1, 2)
            assertEquals(
                HexUtils.getAdjacentEdge(a, b),
                HexUtils.getAdjacentEdge(b, a)
            )
        }
    }

    // -------------------------------------------------------------------------
    // getIntersections
    // -------------------------------------------------------------------------
    @Nested
    inner class GetIntersections {


//        @Test
//        fun `f`() {
//            HexUtils.intersectionCoordinates()
//        }
    }


    // -------------------------------------------------------------------------
    // getAdjacentIntersection
    // -------------------------------------------------------------------------

    @Nested
    inner class GetAdjacentNode {

        @Test
        fun `shared q, not highest — returns max edge coords as intersection`() {
            // a=(0,0),b=(0,1): shared q=0(even), 0>=1 false → max=(0,1) → ICoordinates(0,1)
            assertEquals(
                NodeCoord(0, 1),
                HexUtils.getAdjacentIntersection(EdgeCoord(0, 0), EdgeCoord(0, 1))
            )
        }

        @Test
        fun `shared r, not highest — returns max edge coords as intersection`() {
            // a=(0,0),b=(1,0): shared r=0(even), 0>=1 false → max=(1,0) → ICoordinates(1,0)
            assertEquals(
                NodeCoord(1, 0),
                HexUtils.getAdjacentIntersection(EdgeCoord(0, 0), EdgeCoord(1, 0))
            )
        }

        @Test
        fun `no shared axis — returns max edge coords as intersection`() {
            // a=(0,1),b=(1,0): no shared axis → max by sum: 1 vs 1, a wins → ICoordinates(0,1)
            assertEquals(
                NodeCoord(0, 1),
                HexUtils.getAdjacentIntersection(EdgeCoord(0, 1), EdgeCoord(1, 0))
            )
        }
    }

    // -------------------------------------------------------------------------
    // Round-trips — the property the game logic depends on
    // -------------------------------------------------------------------------

    @Nested
    inner class RoundTrip {

        @Test
        fun `getAdjacentEdge then getAdjacentIntersection recovers original intersection`() {
            val i1 = NodeCoord(0, 1)
            val i2 = NodeCoord(1, 2)
            val sharedEdge = HexUtils.getAdjacentEdge(i1, i2)  // EdgeCoordinates(0,0)
            val otherEdge = EdgeCoord(0, 1)               // other edge meeting at (0,1)
            assertEquals(
                NodeCoord(0, 1),
                HexUtils.getAdjacentIntersection(sharedEdge, otherEdge)
            )
        }

        @Test
        fun `every intersection of a hex lists that hex as adjacent`() {
            val hex = HexCoord(0, 0)
            HexUtils.adjacentIntersections(hex).forEach { icoord ->
                assertTrue(hex in HexUtils.hexesTouchingNode(icoord)) {
                    "Intersection $icoord does not list hex $hex as adjacent"
                }
            }
        }

        @Test
        fun `all intersections of a hex are valid on a board containing that hex`() {
            val hex = HexCoord(0, 0)
            val board = listOf(hex)
            HexUtils.adjacentIntersections(hex).forEach { icoord ->
                assertTrue(HexUtils.isValidCoordinate(icoord, board)) {
                    "Intersection $icoord should be valid on board containing $hex"
                }
            }
        }
    }
}
