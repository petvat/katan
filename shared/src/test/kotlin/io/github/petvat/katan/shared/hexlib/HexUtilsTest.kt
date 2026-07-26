package io.github.petvat.katan.shared.hexlib

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class HexUtilsTest {

    // -------------------------------------------------------------------------
    // transformToDoubled
    // -------------------------------------------------------------------------

    @Nested
    inner class TransformToDoubled {


        @Test
        fun `positive coordinates are doubled`() {
            assertEquals(HexCoordinates(2, 4), HexUtils.transformToDoubled(HexCoordinates(1, 2)))
        }

        @Test
        fun `negative coordinates are doubled`() {
            assertEquals(HexCoordinates(-2, 0), HexUtils.transformToDoubled(HexCoordinates(-1, 0)))
        }

        @Test
        fun `collection overload doubles all hexes`() {
            val input = listOf(HexCoordinates(0, 0), HexCoordinates(1, 1), HexCoordinates(-1, 2))
            val expected = listOf(HexCoordinates(0, 0), HexCoordinates(2, 2), HexCoordinates(-2, 4))
            assertEquals(expected, HexUtils.transformToDoubled(input))
        }

        @Test
        fun `already-doubled collection is not doubled again`() {
            val already = listOf(HexCoordinates(0, 0), HexCoordinates(2, 2))
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
                    ICoordinates(0, 1),
                    ICoordinates(1, 2),
                    ICoordinates(2, 1),
                    ICoordinates(1, 0),
                    ICoordinates(0, -1),
                    ICoordinates(-1, 0)
                ),
                HexUtils.adjacentIntersections(HexCoordinates(0, 0)).toSet()
            )
        }

        @Test
        fun `single-form hex is doubled internally before applying offsets`() {
            val fromSingle = HexUtils.adjacentIntersections(HexCoordinates(1, 1)).toSet()
            val fromDoubled = HexUtils.adjacentIntersections(HexCoordinates(2, 2)).toSet()
            assertEquals(fromDoubled, fromSingle)
        }

        @Test
        fun `always returns exactly 6 intersections`() {
            assertEquals(6, HexUtils.adjacentIntersections(HexCoordinates(0, 0)).size)
            assertEquals(6, HexUtils.adjacentIntersections(HexCoordinates(2, 0)).size)
        }

        @Test
        fun `neighboring hexes share exactly 2 intersections`() {
            val hex1 = HexUtils.adjacentIntersections(HexCoordinates(0, 0)).toSet()
            val hex2 = HexUtils.adjacentIntersections(HexCoordinates(0, 2)).toSet()
            assertEquals(2, (hex1 intersect hex2).size)
        }
    }

    // -------------------------------------------------------------------------
    // adjacentHexes(ICoordinates)
    // Flat (even q): offsets (-2,-1),(0,1),(0,-1) — verified correct.
    // Pointy (odd q): correct third offset is (-1,-2), not (0,-2).
    // -------------------------------------------------------------------------

    @Nested
    inner class AdjacentHexesFromIntersection {

        @Test
        fun `flat intersection (0,1), top of origin, shared by (0,0),(-2,0),(0,2)`() {
            assertEquals(
                setOf(HexCoordinates(0, 0), HexCoordinates(-2, 0), HexCoordinates(0, 2)),
                HexUtils.adjacentHexes(ICoordinates(0, 1)).toSet()
            )
        }

        @Test
        fun `flat intersection (2,1), bottom-right of origin, shared by (0,0),(2,2),(2,0)`() {
            assertEquals(
                setOf(HexCoordinates(0, 0), HexCoordinates(2, 2), HexCoordinates(2, 0)),
                HexUtils.adjacentHexes(ICoordinates(2, 1)).toSet()
            )
        }

        @Test
        fun `pointy intersection (1,2), top-right of origin, shared by (0,0),(0,2),(2,2)`() {
            // Catches bug: old offset (0,-2) gives (1,0) which is an intersection, not a hex
            assertEquals(
                setOf(HexCoordinates(0, 0), HexCoordinates(0, 2), HexCoordinates(2, 2)),
                HexUtils.adjacentHexes(ICoordinates(1, 2)).toSet()
            )
        }

        @Test
        fun `pointy intersection (1,0), bottom of origin, shared by (0,0),(2,0),(0,-2)`() {
            // Catches same bug: old offset (0,-2) gives (1,-2) instead of (0,-2)
            assertEquals(
                setOf(HexCoordinates(0, 0), HexCoordinates(2, 0), HexCoordinates(0, -2)),
                HexUtils.adjacentHexes(ICoordinates(1, 0)).toSet()
            )
        }

        @Test
        fun `always returns exactly 3 adjacent hexes`() {
            assertEquals(3, HexUtils.adjacentHexes(ICoordinates(0, 1)).size)
            assertEquals(3, HexUtils.adjacentHexes(ICoordinates(1, 2)).size)
        }

        @Test
        fun `all adjacent hexes have even coordinates (are valid hex centers)`() {
            HexUtils.adjacentHexes(ICoordinates(1, 2)).forEach { hex ->
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
    inner class IsValidIntersectionCoordinate {

        private val board = listOf(HexCoordinates(0, 0))

        @Test
        fun `all 6 intersections of the only hex on board are valid`() {
            listOf(
                ICoordinates(0, 1),
                ICoordinates(1, 2),
                ICoordinates(2, 1),
                ICoordinates(1, 0),
                ICoordinates(0, -1),
                ICoordinates(-1, 0)
            ).forEach { icoord ->
                assertTrue(HexUtils.isValidCoordinate(icoord, board)) {
                    "$icoord should be valid on board $board"
                }
            }
        }

        @Test
        fun `intersection not adjacent to any board hex is invalid`() {
            assertFalse(HexUtils.isValidCoordinate(ICoordinates(3, 3), board))
            assertFalse(HexUtils.isValidCoordinate(ICoordinates(-3, 0), board))
        }

        @Test
        fun `intersection shared by two hexes is valid when only one hex is on board`() {
            // (0,1) is shared by (0,0),(-2,0),(0,2) — only (0,0) is on board
            assertTrue(HexUtils.isValidCoordinate(ICoordinates(0, 1), board))
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
                EdgeCoordinates(0, 0),
                HexUtils.getAdjacentEdge(ICoordinates(0, 1), ICoordinates(1, 2))
            )
        }

        @Test
        fun `shared value 1 odd, a1 not greater than a2 — uses (a1, a2-1) second case`() {
            // intersections (1,2) and (2,1): shared=1(odd), a=(1,2) → 1>2 false → (1,1)
            assertEquals(
                EdgeCoordinates(1, 1),
                HexUtils.getAdjacentEdge(ICoordinates(1, 2), ICoordinates(2, 1))
            )
        }

        @Test
        fun `shared value 0 even — edge is intersection with higher sum`() {
            // intersections (0,1) and (-1,0): shared=0(even), sums 1 vs -1 → edge=(0,1)
            assertEquals(
                EdgeCoordinates(0, 1),
                HexUtils.getAdjacentEdge(ICoordinates(0, 1), ICoordinates(-1, 0))
            )
        }

        @Test
        fun `is symmetric, argument order does not affect result`() {
            val a = ICoordinates(0, 1)
            val b = ICoordinates(1, 2)
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
    inner class GetAdjacentIntersection {

        @Test
        fun `shared q, not highest — returns max edge coords as intersection`() {
            // a=(0,0),b=(0,1): shared q=0(even), 0>=1 false → max=(0,1) → ICoordinates(0,1)
            assertEquals(
                ICoordinates(0, 1),
                HexUtils.getAdjacentIntersection(EdgeCoordinates(0, 0), EdgeCoordinates(0, 1))
            )
        }

        @Test
        fun `shared r, not highest — returns max edge coords as intersection`() {
            // a=(0,0),b=(1,0): shared r=0(even), 0>=1 false → max=(1,0) → ICoordinates(1,0)
            assertEquals(
                ICoordinates(1, 0),
                HexUtils.getAdjacentIntersection(EdgeCoordinates(0, 0), EdgeCoordinates(1, 0))
            )
        }

        @Test
        fun `no shared axis — returns max edge coords as intersection`() {
            // a=(0,1),b=(1,0): no shared axis → max by sum: 1 vs 1, a wins → ICoordinates(0,1)
            assertEquals(
                ICoordinates(0, 1),
                HexUtils.getAdjacentIntersection(EdgeCoordinates(0, 1), EdgeCoordinates(1, 0))
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
            val i1 = ICoordinates(0, 1)
            val i2 = ICoordinates(1, 2)
            val sharedEdge = HexUtils.getAdjacentEdge(i1, i2)  // EdgeCoordinates(0,0)
            val otherEdge = EdgeCoordinates(0, 1)               // other edge meeting at (0,1)
            assertEquals(
                ICoordinates(0, 1),
                HexUtils.getAdjacentIntersection(sharedEdge, otherEdge)
            )
        }

        @Test
        fun `every intersection of a hex lists that hex as adjacent`() {
            val hex = HexCoordinates(0, 0)
            HexUtils.adjacentIntersections(hex).forEach { icoord ->
                assertTrue(hex in HexUtils.adjacentHexes(icoord)) {
                    "Intersection $icoord does not list hex $hex as adjacent"
                }
            }
        }

        @Test
        fun `all intersections of a hex are valid on a board containing that hex`() {
            val hex = HexCoordinates(0, 0)
            val board = listOf(hex)
            HexUtils.adjacentIntersections(hex).forEach { icoord ->
                assertTrue(HexUtils.isValidCoordinate(icoord, board)) {
                    "Intersection $icoord should be valid on board containing $hex"
                }
            }
        }
    }
}
