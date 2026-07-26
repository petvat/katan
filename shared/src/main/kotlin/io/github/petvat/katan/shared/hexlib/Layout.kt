package io.github.petvat.katan.shared.hexlib

import kotlin.math.sqrt

/**
 * Represents fundamental geometric properties of a hexagon.
 *
 * @param inradius Uses x and y to support non-regular hexagons. The x and y-coordinates correspond to inradius
 * along the x and y-axis respectively.
 *
 * @param origin Coordinate of the center. All calculations are performed relative to this point.
 *
 * Some geometry:
 * - The width = 2 * inradius (r).
 * - The inradius = circumradius (R) * cos(30 degrees) = sqrt(3)/2, i.e. difference between r and R.
 * - The width = 2 * r * sqrt(3)/2 = r * sqrt(3).
 *
 * Only support pointy top first.
 *
 */
data class Layout(
    val inradius: PCoordinate,
    val origin: PCoordinate,
) {
    // Corrected Hex to Pixel linear transformation (Swapped q and r coefficients to fix the axis flip)
    val a1 = sqrt(3.0) / 2.0
    val b1 = sqrt(3.0) / 2.0

    // Swapping the vertical coefficients to correct top-left ( -1, 0 ) vs ( 0, -1 )
    val c1 = -3.0 / 2.0
    val d1 = 3.0 / 2.0

    // Pixel to Hex inverse matrix matching the swap
    val a2 = sqrt(3.0) / 3.0
    val b2 = 1.0 / 3.0
    val c2 = 0.0
    val d2 = 2.0 / 3.0

    // Restored to your original value so the tile art aligns correctly
    val startAngle = 0.5
}

// NOTE: BACKUP
//data class Layout(
//    val inradius: PCoordinate,
//    val origin: PCoordinate,
//) {
//    // Hex to Pixel linear transformation
//    //  [ a  b ] [ q ]
//    //  [ c  d ] [ r ]
//
//    // Red Blob axial system
////    val a1 = sqrt(3.0)
////    val b1 = sqrt(3.0) / 2.0
////    val c1 = 0.0
////    val d1 = 3.0 / 2.0
//
//    // Hexgrid axial system
//    val a1 = sqrt(3.0) / 2.0
//    val b1 = sqrt(3.0) / 2.0
//
//    val c1 = 3.0 / 2.0
//    val d1 = -3.0 / 2.0
//
//
//    // Pixel to Hex linear transformation
//    val a2 = sqrt(3.0) / 3.0
//    val b2 = -1.0 / 3.0
//    val c2 = 0.0
//    val d2 = 2.0 / 3.0
//
//    val startAngle = 0.5
//
//}
