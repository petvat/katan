package io.github.petvat.katan.ui.ktx


import io.github.petvat.katan.shared.hexlib.*
import io.github.petvat.katan.shared.model.board.Board
import io.github.petvat.katan.shared.model.board.Tile
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.ui.ASSETS
import io.github.petvat.katan.ui.projection.BoardRenderMap
import kotlin.math.abs

/**
 * Pure functions producing the static board scaffold.
 * The dynamic overlay (buildings, robber, frontier)
 */
object BoardRenderUtils {

    fun scaffold(layout: Layout, board: Board, extraShoreRings: Int = 0): BoardRenderMap {
        val hexCoords = board.tiles.map { it.hexCoordinate }
        val hexSet = hexCoords.toSet()
        // Largest w such that some tile lies on hexRing(w) — the island's radius
        // in the project's own coordinate metric.
        val islandRadius = generateSequence(1) { it + 1 }
            .takeWhile { w -> HexUtils.hexRing(w).any { it in hexSet } }
            .last()
        val shoreRing = islandRadius + 1 + extraShoreRings
        return BoardRenderMap(
            tiles = islandTextures(layout, board.tiles) +
                shoreTextures(layout, shoreRing) +
                seaTextures(layout, shoreRing + 1),
            tokens = board.tiles
                .filter { it.resource != null && it.rollListenValue > 0 }
                .associate { HexUtils.hexToPixel(layout, it.hexCoordinate) to it.rollListenValue },
            hexes = hexCoords.associateWith { HexUtils.hexToPixel(layout, it) },
            nodes = HexUtils.nodeCoords(layout, hexCoords),
            edges = HexUtils.edgeCoords(layout, hexCoords)
                .mapValues { (_, e) -> PCoord((e.first.x + e.second.x) / 2, (e.first.y + e.second.y) / 2) }
        )
    }

    fun debugHexPoints(layout: Layout, tiles: List<Tile>) =
        tiles.associate { it.hexCoordinate to HexUtils.hexToPixel(layout, it.hexCoordinate) }

    private val tileAsset = mapOf(
        Resource.WOOL to ASSETS.Board.PASTURE,
        Resource.WOOD to ASSETS.Board.FOREST,
        Resource.WHEAT to ASSETS.Board.FIELDS,
        Resource.ORE to ASSETS.Board.MOUNTAINS,
        Resource.BRICK to ASSETS.Board.FIELDS, // TODO: Change to HILLS
        null to ASSETS.Board.DESERT
    )

    private fun islandTextures(layout: Layout, tiles: List<Tile>): Map<PCoord, ASSETS.Board> =
        tiles.associate {
            HexUtils.hexToPixel(layout, it.hexCoordinate) to tileAsset[it.resource]!!
        }

    /** Returns a map of textures of surrounding sea shore tiles,
     * i.e., a ring of sea tiles representing the shore around the island.
     *
     * Algorithm:
     * R x 1,
     * DAR x N,
     * DR x 1,
     * D x N,
     * DL x 1,
     * DAL x N,
     * L x 1,
     * UAL x N,
     * UL x 1,
     * U x N,
     * UR x 1,
     * UAR x N
     *
     */
    private fun shoreTextures(layout: Layout, width: Int): Map<PCoord, ASSETS.Board> {
        val seaShoreTextures = mutableMapOf<PCoord, ASSETS.Board>()
        val surroundingSeaShore = HexUtils.hexRing(width)
        java.util.Collections.rotate(surroundingSeaShore, 3) // HACK: align ring start with SEA_R

        val shoreAssetsOrdered = listOf(
            ASSETS.Board.SEA_R, ASSETS.Board.SEA_DAR, ASSETS.Board.SEA_DR,
            ASSETS.Board.SEA_D, ASSETS.Board.SEA_DL, ASSETS.Board.SEA_DAL,
            ASSETS.Board.SEA_L, ASSETS.Board.SEA_UAL, ASSETS.Board.SEA_UL,
            ASSETS.Board.SEA_U, ASSETS.Board.SEA_UR, ASSETS.Board.SEA_UAR,
        )

        var j = 0
        var i = 0
        while (i < surroundingSeaShore.size) {
            if (j % 2 == 0) {
                // Odd, then only one texture
                seaShoreTextures[HexUtils.hexToPixel(layout, surroundingSeaShore[i])] = shoreAssetsOrdered[j]
            } else {
                repeat(width - 1) {
                    seaShoreTextures[HexUtils.hexToPixel(layout, surroundingSeaShore[i])] = shoreAssetsOrdered[j]
                    i++
                }
                j++
                continue
            }
            i++
            j++
        }
        return seaShoreTextures
    }

    private fun seaTextures(layout: Layout, width: Int): Map<PCoord, ASSETS.Board> {
        val seaTextureMap = mutableMapOf<PCoord, ASSETS.Board>()
        for (j in 0..<5) {
            for (hex in HexUtils.hexRing(width + j)) {
                seaTextureMap[HexUtils.hexToPixel(layout, hex)] = ASSETS.Board.SEA
            }
        }
        return seaTextureMap
    }
}

//
//class BoardRenderingUtils(
//    private val layout: Layout,
//) {
//    fun mapCompleteIsland(tiles: List<Tile>, shoreWidthOffset: Int) =
//        mapIslandTextures(tiles) +
//            mapSeaShoreTextures(
//                shoreWidthOffset
//            ) +
//            mapSeaTextures(
//                shoreWidthOffset + 1
//            )
//
//    /**
//     * Maps Island textures. Tiles as well as tokens.
//     */
//    private fun mapIslandTextures(tiles: List<Tile>): Map<PCoord, ASSETS.Board> {
//        val hexTextures = mutableMapOf<PCoord, ASSETS.Board>()
//
//        val textureMap = mapOf<Resource?, ASSETS.Board>(
//            Resource.WOOL to ASSETS.Board.PASTURE,
//            Resource.WOOD to ASSETS.Board.FOREST,
//            Resource.WHEAT to ASSETS.Board.GRAIN,
//            Resource.ORE to ASSETS.Board.MOUNTAINS,
//            Resource.BRICK to ASSETS.Board.HILLS,
//            Resource.NON_RESOURCE to ASSETS.Board.DESERT
//        )
//
//        // Populate the texture map with island tiles
//        tiles.forEach { tile ->
//            val resource = tile.resource ?: Resource.NON_RESOURCE
//            val pCoord = HexUtils.hexToPixel(layout, tile.hexCoordinate)
//            hexTextures[pCoord] = textureMap[resource]!!
//        }
//        return hexTextures
//    }
//
//
//    /**
//     * Returns a map of textures of surrounding sea shore tiles,
//     * i.e., a ring of sea tiles representing the shore around the island.
//     *
//     * Algorithm
//     * R x 1,
//     * DAR x N,
//     * DR x 1,
//     * D x N,
//     * DL x 1,
//     * DAL x N,
//     * L x 1,
//     * UAL x N,
//     * UL x 1,
//     * U x N,
//     * UR x 1,
//     * UAR x N
//     *
//     */
//    private fun mapSeaShoreTextures(width: Int): Map<PCoord, ASSETS.Board> {
//        val seaShoretextures = mutableMapOf<PCoord, ASSETS.Board>()
//
//        val surroundingSeaShore = HexUtils.hexRing(width)
//        java.util.Collections.rotate(surroundingSeaShore, 3) // HACK
//
//        val shoreAssetsOrdered = listOf(
//            ASSETS.Board.SEA_R,
//            ASSETS.Board.SEA_DAR,
//            ASSETS.Board.SEA_DR,
//            ASSETS.Board.SEA_D,
//            ASSETS.Board.SEA_DL,
//            ASSETS.Board.SEA_DAL,
//            ASSETS.Board.SEA_L,
//            ASSETS.Board.SEA_UAL,
//            ASSETS.Board.SEA_UL,
//            ASSETS.Board.SEA_U,
//            ASSETS.Board.SEA_UR,
//            ASSETS.Board.SEA_UAR,
//        )
//        // val shoreAssets: List<TextureRegion> = assets.tileTextureMap.requireValues(shoreAssetsOrdered)
//
//        var j = 0
//        var i = 0
//        while (i < surroundingSeaShore.size) {
//            if (j % 2 == 0) {
//                // Odd, then only one texture
//                seaShoretextures[HexUtils.hexToPixel(layout, surroundingSeaShore[i])] = shoreAssetsOrdered[j]
//            } else {
//                repeat(width - 1) {
//                    seaShoretextures[HexUtils.hexToPixel(layout, surroundingSeaShore[i])] = shoreAssetsOrdered[j]
//                    i++
//                }
//                j++
//                continue
//            }
//            i++
//            j++
//        }
//        return seaShoretextures
//    }
//
//    /**
//     * Returns a map of physical coordinates to sea texture regions, representing the sea beyond the sea shore.
//     */
//    private fun mapSeaTextures(width: Int): Map<PCoord, ASSETS.Board> {
//        val seaTextureMap = mutableMapOf<PCoord, ASSETS.Board>()
//        for (j in 0..<5) {
//            val seaRing = HexUtils.hexRing(width + j)
//            for (hex in seaRing) {
//                seaTextureMap[HexUtils.hexToPixel(layout, hex)] = ASSETS.Board.SEA
//            }
//        }
//        return seaTextureMap
//    }
//}

//
//object BoardRenderUtils {
//
//    fun debugHexPoints(layout: Layout, tiles: List<Tile>) =
//        tiles.associate { it.hexCoordinate to HexUtils.hexToPixel(layout, it.hexCoordinate) }
//
//
//    // TODO: Should not be Assets
//    fun mapCompleteIsland(layout: Layout, assets: Assets, tiles: List<Tile>, shoreWidthOffset: Int) =
//        mapIslandTextures(tiles, layout, assets) +
//            mapSeaShoreTextures(
//                shoreWidthOffset, layout, assets
//            ) +
//            mapSeaTextures(
//                shoreWidthOffset + 1, layout, assets
//            )
//
//    fun mapHexes(
//        layout: Layout,
//        hexCoords: List<HexCoord>
//    ): Map<HexCoord, PCoord> {
//        return hexCoords.associateWith { HexUtils.hexToPixel(layout, it) }
//    }
//
//    fun mapNodes(
//        layout: Layout,
//        hexCoordinates: List<HexCoord>
//    ): Map<NodeCoord, PCoord> {
//        return HexUtils.nodeCoords(layout, hexCoordinates)
//    }
//
//    fun mapEdges(
//        layout: Layout,
//        hexCoordinates: List<HexCoord>
//    ): Map<EdgeCoord, PCoord> {
//        val edges = HexUtils.edgeCoords(layout, hexCoordinates)
//        // Midpoints
//        return edges.mapValues { (_, e) -> PCoord((e.first.x + e.second.x) / 2, (e.first.y + e.second.y) / 2) }
//    }
//
//    fun mapTokenTexture(tiles: List<Tile>, layout: Layout, assets: Assets): Map<PCoord, TextureRegion> {
//        val tokenRenderMap = mutableMapOf<PCoord, TextureRegion>()
//        tiles.forEach { tile ->
//            val resource = tile.resource ?: Resource.NON_RESOURCE
//            val pCoord = HexUtils.hexToPixel(layout, tile.hexCoordinate)
//            if (resource != Resource.NON_RESOURCE) {
//                tokenRenderMap[pCoord] = assets.tokenTextureMap[tile.rollListenValue]!!
//            }
//        }
//        return tokenRenderMap
//    }
//
//
//    /**
//     * Maps Island textures. Tiles as well as tokens.
//     */
//    private fun mapIslandTextures(tiles: List<Tile>, layout: Layout, assets: Assets): Map<PCoord, TextureRegion> {
//        val hexTextures = mutableMapOf<PCoord, TextureRegion>()
//
//        val textureMap = mapOf<Resource?, Assets.Asset>(
//            Resource.WOOL to Assets.Asset.PASTURE,
//            Resource.WOOD to Assets.Asset.FOREST,
//            Resource.WHEAT to Assets.Asset.GRAIN,
//            Resource.ORE to Assets.Asset.MOUNTAINS,
//            Resource.BRICK to Assets.Asset.GRAIN,
//            Resource.NON_RESOURCE to Assets.Asset.GRAIN
//        )
//
//        // Populate the texture map with island tiles
//        tiles.forEach { tile ->
//            val resource = tile.resource ?: Resource.NON_RESOURCE
//            val pCoord = HexUtils.hexToPixel(layout, tile.hexCoordinate)
//            hexTextures[pCoord] = assets.tileTextureMap[textureMap[resource]!!]!!
//        }
//        return hexTextures
//    }
//
//
//    /**
//     * Returns a map of textures of surrounding sea shore tiles,
//     * i.e., a ring of sea tiles representing the shore around the island.
//     *
//     * Algorithm
//     * R x 1,
//     * DAR x N,
//     * DR x 1,
//     * D x N,
//     * DL x 1,
//     * DAL x N,
//     * L x 1,
//     * UAL x N,
//     * UL x 1,
//     * U x N,
//     * UR x 1,
//     * UAR x N
//     *
//     */
//    private fun mapSeaShoreTextures(width: Int, layout: Layout, assets: Assets): Map<PCoord, TextureRegion> {
//        val seaShoretextures = mutableMapOf<PCoord, TextureRegion>()
//
//        val surroundingSeaShore = HexUtils.hexRing(width)
//        java.util.Collections.rotate(surroundingSeaShore, 3) // HACK
//
//        val shoreAssetsOrdered = listOf(
//            Assets.Asset.SEA_R,
//            Assets.Asset.SEA_DAR,
//            Assets.Asset.SEA_DR,
//            Assets.Asset.SEA_D,
//            Assets.Asset.SEA_DL,
//            Assets.Asset.SEA_DAL,
//            Assets.Asset.SEA_L,
//            Assets.Asset.SEA_UAL,
//            Assets.Asset.SEA_UL,
//            Assets.Asset.SEA_U,
//            Assets.Asset.SEA_UR,
//            Assets.Asset.SEA_UAR,
//        )
//        val shoreAssets: List<TextureRegion> = assets.tileTextureMap.requireValues(shoreAssetsOrdered)
//
//        var j = 0
//        var i = 0
//        while (i < surroundingSeaShore.size) {
//            if (j % 2 == 0) {
//                // Odd, then only one texture
//                seaShoretextures[HexUtils.hexToPixel(layout, surroundingSeaShore[i])] = shoreAssets[j]
//            } else {
//                repeat(width - 1) {
//                    seaShoretextures[HexUtils.hexToPixel(layout, surroundingSeaShore[i])] = shoreAssets[j]
//                    i++
//                }
//                j++
//                continue
//            }
//            i++
//            j++
//        }
//        return seaShoretextures
//    }
//
//    /**
//     * Returns a map of physical coordinates to sea texture regions, representing the sea beyond the sea shore.
//     */
//    private fun mapSeaTextures(width: Int, layout: Layout, assets: Assets): Map<PCoord, TextureRegion> {
//        val seaTextureMap = mutableMapOf<PCoord, TextureRegion>()
//        for (j in 0..<5) {
//            val seaRing = HexUtils.hexRing(width + j)
//            for (hex in seaRing) {
//                seaTextureMap[HexUtils.hexToPixel(layout, hex)] = assets.tileTextureMap[Assets.Asset.SEA]!!
//            }
//        }
//        return seaTextureMap
//    }
//
//}

