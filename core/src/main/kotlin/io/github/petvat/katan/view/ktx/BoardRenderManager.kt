package io.github.petvat.katan.view.ktx

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.petvat.katan.view.Assets
import io.github.petvat.katan.shared.hexlib.*
import io.github.petvat.katan.shared.model.board.Tile
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.shared.protocol.dto.BoardView
import io.github.petvat.katan.shared.util.requireValues

/**
 * Board renderer for LibGDX gameState.
 *
 */
class BoardRenderManager(
    private val board: BoardView,
    private val batch: SpriteBatch,
    private val assets: Assets,
    private val layout: Layout
) {


    /**
     * Maps logic intersection coordinates to screen coordinates.
     */
    private var intersectionMap: Map<ICoordinates, PCoordinate> // NOTE: will this fail if we resize window?


    private lateinit var edgeRenderMap: Map<EdgeCoordinates, PCoordinate>

    /**
     * Map that tells the sprite batch what texture to render on a coordinate.
     */
    private var tileRenderMap = mutableMapOf<PCoordinate, TextureRegion>()

    /**
     * Map that tells the sprite batch what token to render.
     */
    private var tokenRenderMap = mutableMapOf<PCoordinate, TextureRegion>()

    /**
     * TODO: Find way to add roads
     */
    var roadRenderMap = mutableMapOf<PCoordinate, TextureRegion>()

    /**
     * TODO: Find way to add villages
     */
    var villageRenderMap = mutableMapOf<PCoordinate, TextureRegion>()

    var robberLocation: Pair<HexCoordinates, PCoordinate> =
        board.robberLocation to HexUtils.hexToPixel(layout, board.robberLocation)

    init {
        val hexes = board.tiles.map { it.hexCoordinate }
        intersectionMap = mapIntersectionCoordinates(layout, hexes)
        val mapIslandText = mapIslandTextures()
        tileRenderMap =
            (mapIslandText + mapSeaShoreTextures(3) + mapSeaTextures(4)) as MutableMap<PCoordinate, TextureRegion>

        // Transform tiles to doubled. We need to do this to work with edges/intersections.
        board.tiles = board.tiles
            .map {
                Tile(
                    HexUtils.transformToDoubled(it.hexCoordinate),
                    it.resource,
                    it.rollListenValue
                )
            }.toList()
    }


    fun checkForBoardUpdates() {
        // A producer consumer impl
        // But since there are so few elements:
        board.paths.forEach { p ->
            // TODO:
            if (edgeRenderMap[p.coordinate] != null) {
                roadRenderMap[edgeRenderMap[p.coordinate]]
            }
        }
        board.intersections.forEach { i ->
            if (intersectionMap[i.coordinate] != null) {
                villageRenderMap[intersectionMap[i.coordinate]]
            }
        }
        if (board.robberLocation != robberLocation.first) {
            // robberLocation[board.robberLocation]
        }

    }


    fun render() {

        checkForBoardUpdates()

        batch.begin()
        // Draw tiles
        drawMap(tileRenderMap)
        // draw tokens
        drawMap(tokenRenderMap, functionY = { coord, tex -> coord.y.toFloat() - (tex.regionHeight / 2) + 5 })
        // draw road
        drawMap(roadRenderMap)
        // draw village
        drawMap(villageRenderMap)

        batch.end()
    }

    private fun drawMap(
        map: Map<PCoordinate, TextureRegion>,
        functionX: (PCoordinate, TextureRegion) -> Float = { coord, tex -> coord.x.toFloat() - tex.regionWidth / 2 },
        functionY: (PCoordinate, TextureRegion) -> Float = { coord, tex -> coord.y.toFloat() - tex.regionHeight / 2 }
    ) {
        map.forEach { (coord, tex) ->
            batch.draw(tex, functionX(coord, tex), functionY(coord, tex))
        }
    }

    private fun mapIntersectionCoordinates(
        layout: Layout,
        hexCoordinates: List<HexCoordinates>
    ): Map<ICoordinates, PCoordinate> {
        return HexUtils.intersectionCoordinates(layout, hexCoordinates)
    }

    private fun mapIslandTextures(): Map<PCoordinate, TextureRegion> {
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
        board.tiles.forEach { tile ->
            val resource = tile.resource ?: Resource.NON_RESOURCE
            val pCoord = HexUtils.hexToPixel(layout, tile.hexCoordinate)
            hexTextures[pCoord] = assets.tileTextureMap[textureMap[resource]!!]!!
            if (resource != Resource.NON_RESOURCE) {
                tokenRenderMap[pCoord] = assets.tokenTextureMap[tile.rollListenValue]!!
            }

        }
        return hexTextures
    }

    /**
     * Returns a map of textures of surrounding sea shore tiles.
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
    private fun mapSeaShoreTextures(width: Int): Map<PCoordinate, TextureRegion> {
        val surroundingSeaShore = HexUtils.hexRing(width)
        val seaShoretextures = mutableMapOf<PCoordinate, TextureRegion>()

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

    private fun mapSeaTextures(width: Int): Map<PCoordinate, TextureRegion> {
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
