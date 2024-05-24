package io.github.petvat.katan.server.board

/**
 * Represents a coordinate on the board.
 * This can be either Tile, Intersection or path coordinates.
 */
open class Coordinate(
    open val x: Int,
    open val y: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Coordinate) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

data class TileCoordinate(override val x: Int, override val y: Int) : Coordinate(x, y)
data class PathCoordinate(override val x: Int, override val y: Int) : Coordinate(x, y)
data class IntersectionCoordinate(override val x: Int, override val y: Int) : Coordinate(x, y)

// Vits å ha? Utgjer ingen stor forskjell. Uansett skild i ulike datastrukturar, lister osv.
sealed class Coordinate2 {
    abstract val x: Int
    abstract val y: Int

}

// could be both open and abstract
abstract class Coordinate3(val x: Int, val y: Int) {
}

// If inside of another class, Coordinate.TileCoordinate3
class TileCoordinate3(x: Int, y: Int) : Coordinate3(x, y)

