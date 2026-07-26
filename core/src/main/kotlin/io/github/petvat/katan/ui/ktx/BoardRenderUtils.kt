package io.github.petvat.katan.ui.ktx

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Collections
import io.github.petvat.katan.shared.hexlib.*
import io.github.petvat.katan.shared.model.board.Tile
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.shared.util.requireValues
import io.github.petvat.katan.ui.Assets


object BoardRenderUtils {

    var font: BitmapFont = BitmapFont()


    fun debugHexPoints(layout: Layout, tiles: List<Tile>) =
        tiles.associate { it.hexCoordinate to HexUtils.hexToPixel(layout, it.hexCoordinate) }


    fun mapCompleteIsland(layout: Layout, assets: Assets, tiles: List<Tile>, shoreWidthOffset: Int) =
        mapIslandTextures(tiles, layout, assets) +
            mapSeaShoreTextures(
                shoreWidthOffset, layout, assets
            ) +
            mapSeaTextures(
                shoreWidthOffset + 1, layout, assets
            )


    fun doubleTiles(tiles: List<Tile>): List<Tile> {
        return tiles
            .map {
                Tile(
                    HexUtils.transformToDoubled(it.hexCoordinate),
                    it.resource,
                    it.rollListenValue
                )
            }.toMutableList()
    }

//    fun mapIntersectionZoneTextures(
//        assets: Assets,
//        intersections: Collection<PCoordinate>,
//        playerColor: PlayerColor
//    ): Map<PCoordinate, TextureRegion> {
//        return intersections.associateWith {
//            assets.intersectionZoneMap[playerColor]!!
//        }
//    }

    /**
     *
     */
    fun mapIntersectionCoordinates(
        layout: Layout,
        hexCoordinates: List<HexCoordinates>
    ): Map<ICoordinates, PCoordinate> {
        return HexUtils.intersectionCoordinates(layout, hexCoordinates)
    }

    fun mapEdgeCoordinates(
        layout: Layout,
        hexCoordinates: List<HexCoordinates>
    ): Map<EdgeCoordinates, PCoordinate> {
        val edges = HexUtils.edgeCoordinates(layout, hexCoordinates)
        // Midpoints
        return edges.mapValues { (_, e) -> PCoordinate((e.first.x + e.second.x) / 2, (e.first.y + e.second.y) / 2) }
    }

    fun mapTokenTexture(tiles: List<Tile>, layout: Layout, assets: Assets): Map<PCoordinate, TextureRegion> {
        val tokenRenderMap = mutableMapOf<PCoordinate, TextureRegion>()
        tiles.forEach { tile ->
            val resource = tile.resource ?: Resource.NON_RESOURCE
            val pCoord = HexUtils.hexToPixel(layout, tile.hexCoordinate)
            if (resource != Resource.NON_RESOURCE) {
                tokenRenderMap[pCoord] = assets.tokenTextureMap[tile.rollListenValue]!!
            }
        }
        return tokenRenderMap
    }


    /**
     * Maps Island textures. Tiles as well as tokens.
     */
    fun mapIslandTextures(tiles: List<Tile>, layout: Layout, assets: Assets): Map<PCoordinate, TextureRegion> {
        val hexTextures = mutableMapOf<PCoordinate, TextureRegion>()

        val textureMap = mapOf<Resource?, Assets.Asset>(
            Resource.WOOL to Assets.Asset.PASTURE,
            Resource.WOOD to Assets.Asset.FOREST,
            Resource.WHEAT to Assets.Asset.GRAIN,
            Resource.ORE to Assets.Asset.MOUNTAINS,
            Resource.BRICK to Assets.Asset.GRAIN,
            Resource.NON_RESOURCE to Assets.Asset.GRAIN
        )

        // Populate the texture map with island tiles
        tiles.forEach { tile ->
            val resource = tile.resource ?: Resource.NON_RESOURCE
            val pCoord = HexUtils.hexToPixel(layout, tile.hexCoordinate)
            hexTextures[pCoord] = assets.tileTextureMap[textureMap[resource]!!]!!
        }
        return hexTextures
    }


    /**
     * Returns a map of textures of surrounding sea shore tiles,
     * i.e., a ring of sea tiles representing the shore around the island.
     *
     * Algorithm
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
    fun mapSeaShoreTextures(width: Int, layout: Layout, assets: Assets): Map<PCoordinate, TextureRegion> {
        val seaShoretextures = mutableMapOf<PCoordinate, TextureRegion>()

        val surroundingSeaShore = HexUtils.hexRing(width)
        java.util.Collections.rotate(surroundingSeaShore, 3) // HACK

        val shoreAssetsOrdered = listOf(
            Assets.Asset.SEA_R,
            Assets.Asset.SEA_DAR,
            Assets.Asset.SEA_DR,
            Assets.Asset.SEA_D,
            Assets.Asset.SEA_DL,
            Assets.Asset.SEA_DAL,
            Assets.Asset.SEA_L,
            Assets.Asset.SEA_UAL,
            Assets.Asset.SEA_UL,
            Assets.Asset.SEA_U,
            Assets.Asset.SEA_UR,
            Assets.Asset.SEA_UAR,
        )
        val shoreAssets: List<TextureRegion> = assets.tileTextureMap.requireValues(shoreAssetsOrdered)

        var j = 0
        var i = 0
        while (i < surroundingSeaShore.size) {
            if (j % 2 == 0) {
                // Odd, then only one texture
                seaShoretextures[HexUtils.hexToPixel(layout, surroundingSeaShore[i])] = shoreAssets[j]
            } else {
                repeat(width - 1) {
                    seaShoretextures[HexUtils.hexToPixel(layout, surroundingSeaShore[i])] = shoreAssets[j]
                    i++
                }
                j++
                continue
            }
            i++
            j++
        }
        return seaShoretextures
    }

    /**
     * Returns a map of physical coordinates to sea texture regions, representing the sea beyond the sea shore.
     */
    fun mapSeaTextures(width: Int, layout: Layout, assets: Assets): Map<PCoordinate, TextureRegion> {
        val seaTextureMap = mutableMapOf<PCoordinate, TextureRegion>()
        for (j in 0..<5) {
            val seaRing = HexUtils.hexRing(width + j)
            for (hex in seaRing) {
                seaTextureMap[HexUtils.hexToPixel(layout, hex)] = assets.tileTextureMap[Assets.Asset.SEA]!!
            }
        }
        return seaTextureMap
    }

}

