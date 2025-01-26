package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.petvat.katan.event.*
import io.github.petvat.katan.ui.Assets
import io.github.petvat.katan.shared.hexlib.*
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.shared.util.requireValues
import io.github.petvat.katan.ui.model.GameViewModel
import io.github.petvat.katan.ui.model.Graphic
import io.github.petvat.katan.ui.model.PlayerColor


/**
 * Board renderer for LibGDX gameState.
 */
class BoardGraphic(
    override val viewModel: GameViewModel,
    private val batch: SpriteBatch,
    private val assets: Assets,
    private val layout: Layout
) : Graphic<GameViewModel> {

    private var settlementFrontierMap: Map<ICoordinates, PCoordinate> = mapOf()
    private var cityFrontierMap: Map<ICoordinates, PCoordinate> = mapOf()

    private var roadFrontierMap: Map<EdgeCoordinates, PCoordinate> = mapOf()

    private var setupFrontierMap: Map<ICoordinates, PCoordinate> = mapOf()

    /**
     * Maps logic intersection coordinates to screen coordinates.
     */
    private var intersectionMap: Map<ICoordinates, PCoordinate> // NOTE: will this fail if we resize window?

    /**
     * Maps logic edge coordinates to screen coordinates.
     *
     * TODO: Implement this.
     */
    private lateinit var edgeMap: Map<EdgeCoordinates, PCoordinate>

    /**
     * Map that tells the sprite batch what texture to render on a coordinate.
     */
    private var tileRenderMap: MutableMap<PCoordinate, TextureRegion>

    /**
     * Map that tells the sprite batch what token to render.
     */
    private var tokenRenderMap = mutableMapOf<PCoordinate, TextureRegion>()

    /**
     * TODO: Find way to add roads
     */
    private var roadRenderMap = mutableMapOf<PCoordinate, TextureRegion>()

    /**
     * TODO: Find way to add villages
     */
    private var villageRenderMap = mutableMapOf<PCoordinate, TextureRegion>()

    private var robberLocation: Pair<HexCoordinates, PCoordinate> =
        viewModel.robberLocation to HexUtils.hexToPixel(layout, viewModel.robberLocation)


    lateinit var intersectionHighLightTex: TextureRegion


    init {
        val hexes = viewModel.tiles.map { it.hexCoordinate }
        intersectionMap = mapIntersectionCoordinates(layout, hexes)
        val mapIslandText = mapIslandTextures()
        tileRenderMap =
            (mapIslandText + mapSeaShoreTextures(3) + mapSeaTextures(4)) as MutableMap<PCoordinate, TextureRegion>

        // Transform tiles to doubled. We need to do this to work with edges/intersections.
        // NOTE: ViewModel does this.


//        board.tiles = board.tiles
//            .map {
//                Tile(
//                    HexUtils.transformToDoubled(it.hexCoordinate),
//                    it.resource,
//                    it.rollListenValue
//                )
//            }.toList()
    }


    fun handleTouch(x: Int, y: Int) {
        TODO()
    }

    private fun drawAll(coordinates: List<PCoordinate>, texture: TextureRegion) {
        coordinates.forEach {
            batch.draw(texture, it.x.toFloat(), it.y.toFloat())
        }
    }


    fun render() {

        // checkForBoardUpdates()

        batch.begin()

        // TODO: Atomicity - what if viewModel changes during this?
        if (viewModel.roadPlacingMode) {
            drawAll(
                roadFrontierMap.values.toList(),
                intersectionHighLightTex
            ) // TODO: CHANGE
        }
        if (viewModel.settlementPlacingMode) {
            drawAll(
                settlementFrontierMap.values.toList(),
                intersectionHighLightTex
            )
        }
        if (viewModel.cityPlacingMode) {
            drawAll(
                cityFrontierMap.values.toList(),
                intersectionHighLightTex
            )
        }

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
        viewModel.tiles.forEach { tile ->
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

    fun onUiEvent(event: UiEvent) {
        if (event is BuildUiEvent) {
            if (event.buildKind is BuildKind.Village) {
                villageRenderMap[intersectionMap[event.coordinates as ICoordinates]!!] =
                    assets.villageTextureMap[event.playerColor]!!
            } else if (event.buildKind is BuildKind.Road) {
                roadRenderMap[edgeMap[event.coordinates as EdgeCoordinates]!!] =
                    assets.villageTextureMap[event.playerColor]!!
            }
        }
        if (event is PlaceBuildingUiEvent) {
            settlementFrontierMap = intersectionMap
                .filterKeys { key -> key in viewModel.settlementFrontier }

            cityFrontierMap = intersectionMap
                .filterKeys { key -> key in viewModel.cityFrontier }

            roadFrontierMap = edgeMap
                .filterKeys { key -> key in viewModel.roadFrontier }

            setupFrontierMap = intersectionMap
                .filterKeys { key -> key in viewModel.setUpFrontier }
        }
    }


    override fun onEvent(event: Event) {

        when (event) {

//            is PlaceBuildingCommand<*> -> {
//                // Update the frontier hightlights.
//                settlementFrontierMap = intersectionMap
//                    .filterKeys { key -> key in viewModel.settlementFrontier }
//
//                cityFrontierMap = intersectionMap
//                    .filterKeys { key -> key in viewModel.cityFrontier }
//
//                roadFrontierMap = edgeMap
//                    .filterKeys { key -> key in viewModel.roadFrontier }
//
//                setupFrontierMap = intersectionMap
//                    .filterKeys { key -> key in viewModel.setUpFrontier }
//            }

            is BuildEvent -> {

                // Building animation. Difficult because we need to fetch information about the player and such.
                // Annoying - don't do that.

            }

            else -> Unit
        }
    }

    override fun registerOnPropertyChanges() {
        viewModel.onPropertyChange(GameViewModel::intersections) {
            val newIntersect = it.last()
            // TODO: Infer the PlayerColor!
            villageRenderMap[intersectionMap[newIntersect.coordinate]!!] = assets.villageTextureMap[PlayerColor.RED]!!
        }

        viewModel.onPropertyChange(GameViewModel::paths) {
            val newPath = it.last()
            // TODO: Infer the PlayerColor!
            villageRenderMap[edgeMap[newPath.coordinate]!!] = assets.roadTexture[PlayerColor.RED]!!
        }

        viewModel.onPropertyChange(GameViewModel::robberLocation) {
            robberLocation = it to HexUtils.hexToPixel(layout, it)
        }

        viewModel.onPropertyChange(GameViewModel::settlementFrontier) {
            settlementFrontierMap = intersectionMap
                .filterKeys { key -> key in it }
        }

        viewModel.onPropertyChange(GameViewModel::cityFrontier) {
            cityFrontierMap = intersectionMap
                .filterKeys { key -> key in it }
        }

        viewModel.onPropertyChange(GameViewModel::roadFrontier) {
            roadFrontierMap = edgeMap
                .filterKeys { key -> key in it }
        }
    }
}
