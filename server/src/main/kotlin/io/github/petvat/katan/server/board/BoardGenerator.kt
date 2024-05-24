package board

import io.github.petvat.katan.server.Settings
import io.github.petvat.katan.server.board.Coordinate
import io.github.petvat.katan.server.board.Resource
import io.github.petvat.katan.server.board.Tile
import io.github.petvat.katan.server.board.TileCoordinate


object BoardGenerator {

    /**
     * Probably necessary for creating UI
     */
    fun getIntersections(tileCoordinates: Collection<Coordinate>): Set<Coordinate> {
        TODO("Implement get intersections OR maybe not")
    }


    /**
     * Hex-shaped 5-tiles for testing purposes.
     */
    fun generateTestBoard(): List<Tile> {
        val tiles: MutableList<Tile> = mutableListOf()
        val numbers = mutableListOf<Int>()
        numbers.addAll(Settings.TERRAIN_NUMBERS)
        numbers.shuffle()

        val minWidth = 2
        val maxWidth = 4 // 4 + desert
        val height = (maxWidth + 1) - minWidth

        var x = -maxWidth
        var y = (height * 2) - maxWidth

        for (i in 0..<height) {
            for (j in 0..<2 + i) {
                tiles.add(
                    Tile(
                        Coordinate(x, y),
                        Resource.WOOL,
                        numbers.removeFirst()
                    )
                )
                tiles.add(
                    Tile(
                        Coordinate(y, x),
                        Resource.WOOL,
                        numbers.removeFirst()
                    )
                )
                x += 2
                y += 2
            }
            x = -maxWidth
            y = ((height * 2) - maxWidth) - (2 * (i + 1))
        }
        for (i in 0..<maxWidth + 1) {
            if (x == 0 && y == 0) {
                // desert
                tiles.add(Tile(Coordinate(x, y), null, 0))
            } else {
                tiles.add(
                    Tile(
                        Coordinate(x, y),
                        Resource.WOOL,
                        numbers.removeFirst()
                    )
                )
            }
            x += 2
            y += 2
        }
        return tiles
    }

    private fun randomTile(terrainCounts: MutableMap<Resource, Int>): Resource {
        val randomTerrain = terrainCounts.filterValues { it > 0 }.keys.random()
        terrainCounts[randomTerrain] = terrainCounts[randomTerrain]!! - 1
        return randomTerrain
    }

    fun generateBoard() {
        // With Custom game settings.
        // Random, tile counts
    }


    // FIXME:
    fun generateDefaultBoard(): List<Tile> {
        val tiles: MutableList<Tile> = mutableListOf()
        val numbers = mutableListOf<Int>()
        numbers.addAll(Settings.TERRAIN_NUMBERS)
        numbers.shuffle()

        val terrainCounts = mutableMapOf(
            Resource.WOOL to Settings.NUM_PASTURE,
            Resource.ORE to Settings.NUM_MOUNTAINS,
            Resource.WOOD to Settings.NUM_FOREST,
            Resource.BRICK to Settings.NUM_HILLS,
            Resource.WHEAT to Settings.NUM_FIELDS
        )

        val minWidth = 3
        val maxWidth = 4 // 4 + desert
        val height = (maxWidth + 1) - minWidth

        var x = -maxWidth
        var y = (height * 2) - maxWidth

        for (i in 0 until height) {
            for (j in 0 until 2 + i) {
                tiles.add(
                    Tile(
                        Coordinate(x, y),
                        randomTile(terrainCounts),
                        numbers.removeFirst()
                    )
                )
                tiles.add(
                    Tile(
                        Coordinate(y, x),
                        randomTile(terrainCounts),
                        numbers.removeFirst()
                    )
                )
                x += 2
                y += 2
            }
            x = -maxWidth
            y = ((height * 2) - maxWidth) - (2 * (i + 1))
        }
        for (i in 0 until maxWidth + 1) {
            if (x == 0 && y == 0) {
                // desert
                tiles.add(Tile(Coordinate(x, y), null, 0))
            } else {
                tiles.add(
                    Tile(
                        Coordinate(x, y),
                        randomTile(terrainCounts),
                        numbers.removeFirst()
                    )
                )
            }
            x += 2
            y += 2
        }
        return tiles
    }

    /**
     * Generate default board, i.e. radius = 3
     */
    fun generateDefaultBoardCircular(): List<Tile> {
        return generateBoardCircular(3)
    }

    /**
     * Generates a board using rings from radius.
     *
     * @see ringTiles
     */
    fun generateBoardCircular(rings: Int): List<Tile> {
        val tiles: MutableList<Tile> = mutableListOf()
        val tileCoordinates: MutableList<TileCoordinate> = mutableListOf()

        for (ring in 1..rings) {
            tileCoordinates.addAll(ringTiles(ring))
        }

        val numbers = mutableListOf<Int>()
        numbers.addAll(Settings.TERRAIN_NUMBERS)
        numbers.shuffle()

        val terrainCounts = mutableMapOf(
            Resource.WOOL to Settings.NUM_PASTURE,
            Resource.ORE to Settings.NUM_MOUNTAINS,
            Resource.WOOD to Settings.NUM_FOREST,
            Resource.BRICK to Settings.NUM_HILLS,
            Resource.WHEAT to Settings.NUM_FIELDS
        )

        tileCoordinates.forEach { coord ->
            if (coord.x == 0 && coord.y == 0) {
                tiles.add(Tile(coord, null, -1)) // desert
            } else {
                // TODO: Check this works
                tiles.add(Tile(coord, randomTile(terrainCounts), numbers.removeFirst()))
            }
        }
        return tiles
    }

    /**
     * Get tiles of radius away from origin.
     * @param radius away from origin
     * @return List of tile coordinates
     */
    private fun ringTiles(radius: Int): List<TileCoordinate> {

        val tiles: MutableList<TileCoordinate> = mutableListOf()

        fun addTiles(x: Int, y: Int) {
            tiles.add(TileCoordinate(x, y))
            tiles.add(TileCoordinate(y, x)) // add mirror as well
        }

        var currX = 0
        var currY = 0
        currX = radius * -2
        currY = radius * -2

        tiles.add(TileCoordinate(currX, currY))
        repeat(radius) {
            currY += 2
            addTiles(currX, currY)
        }
        repeat(radius) {
            currX += 2
            currY += 2
            addTiles(currX, currY)
        }
        repeat(radius) {
            currX += 2
            addTiles(currX, currY)
        }
        return tiles
    }

    fun randomizedBoardCircular(tiles: Int) {

    }


//    fun generateDefaultBoard(): List<Tile> {
//        val tiles: MutableList<Tile> = mutableListOf()
//
//        val numTiles = 19
//        val offsets = arrayOf(
//            -2, -2,
//            -2, 0,
//            0, 2,
//            2, 2,
//            2, 0,
//            0, -2
//        )
//        // Desert
//        tiles.add(Tile(Coordinate(0, 0), null, 0))
//
//        val numbers = Settings.TERRAIN_NUMBERS
//        numbers.shuffle()
//        val mapWidth = 7
//        var d = mapWidth - 1
//        val numbersIndex = 0
//        // Middle
//        for (i in 0..<mapWidth - 1) {
//            d += 2
//            tiles.add(Tile(Coordinate(d, d), Resource.WOOL, numbersIndex))
//        }
//        var x = 1 - mapWidth
//        var y = x
//        var currentWidth = mapWidth - 1
//        for (i in 0..2) {
//            x = 1 - mapWidth
//            y += 2
//            for (j in 0..currentWidth) {
//                tiles.add(Tile(Coordinate(x, y), Resource.WOOL, numbersIndex))
//                tiles.add(Tile(Coordinate(y, x), Resource.WOOL, numbersIndex))
//                x += 2
//                y += 2
//            }
//            currentWidth--
//        }
//    }
}
