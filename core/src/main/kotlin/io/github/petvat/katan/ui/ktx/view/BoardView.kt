package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import io.github.petvat.katan.ui.Assets
import io.github.petvat.katan.shared.hexlib.*
import io.github.petvat.katan.ui.projection.BoardOverlay
import io.github.petvat.katan.ui.viewmodel.BoardViewModel
import io.github.petvat.katan.ui.viewmodel.GameViewModel
import kotlin.math.roundToInt


//class BoardInputController(private val onTap: (Coordinates) -> Unit) {
//    fun handleTouch(screenX: Int, screenY: Int, camera: OrthographicCamera, frontier: Map<out Coordinates, PCoord>) {
//        val world = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
//        frontier.entries.find { (_, p) -> }
//    }
//
//    private fun circleContains(cx: Float, cy: Float, r: Float, px: Float, py: Float): Boolean {
//        val dx = px - cx;
//        val dy = py - cy;
//        return dx * dx + dy * dy < r * r
//    }
//}


/**
 * Batch-drawn board renderer. Not a scene2d actor.
 *
 * Wiring: subscribes to BoardVM::overlay once; caches and re-renders each frame.
 * Static layer (tiles/tokens) is resolved to TextureRegions once, here.
 */
class BoardView(
    private val viewModel: BoardViewModel,
    private val assets: Assets,
) {
    private val tileTextures: Map<PCoord, TextureRegion> =
        viewModel.scaffold.tiles.mapValues { (_, asset) -> assets.region(asset) }

    private val tokenTextures: Map<PCoord, TextureRegion> =
        viewModel.scaffold.tokens.mapValues { (_, value) -> assets.token(value) }

    private val debugLabels: Map<HexCoord, PCoord> = viewModel.scaffold.hexes
    private var debugFont = com.badlogic.gdx.graphics.g2d.BitmapFont()

    private var overlay: BoardOverlay = viewModel.overlay

    private val iZoneTexture: TextureRegion = assets.nodeZone(viewModel.myColor)
    private val eZoneTexture: TextureRegion = assets.edgeZone(viewModel.myColor)

    private val izoneTextures: Map<PCoord, TextureRegion> =
        overlay.frontier.map { (lc, ph) -> ph to assets.nodeZone(viewModel.myColor) }.toMap()

    private val ezoneTextures: Map<PCoord, TextureRegion> =
        overlay.frontier.map { (lc, ph) -> ph to assets.edgeZone(viewModel.myColor) }.toMap()

    private val hitRadius = 20f

    init {
        viewModel.onPropertyChange(BoardViewModel::overlay) { newOverlay ->
            overlay = newOverlay
        }
    }

    /** Called by the owning screen from its input handler. */
    fun handleTouch(screenX: Int, screenY: Int, camera: OrthographicCamera) {
        if (overlay.frontier.isEmpty()) return
        val world = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val hit = overlay.frontier.entries.find { (_, p) ->
            val dx = world.x - p.x.toFloat()
            val dy = world.y - p.y.toFloat()
            dx * dx + dy * dy <= hitRadius * hitRadius
        } ?: return
        viewModel.onBoardTap(hit.key)
    }

    /** Called by the owning screen each frame. */
    fun render(batch: SpriteBatch) {
        batch.begin()
        // 1. island + shore + sea
        tileTextures.forEach { (p, tex) -> drawCentered(batch, tex, p) }
        // 2. number tokens
        tokenTextures.forEach { (p, tex) -> drawCentered(batch, tex, p, yOff = 5f) }
        // 3. roads, villages
        overlay.roads.forEach { (p, m) -> drawCentered(batch, assets.road(m.color, m.orientation), p) }
        overlay.villages.forEach { (p, m) -> drawCentered(batch, assets.village(m.color, m.kind), p) }
        // 4. robber (add assets.robber() + a robber texture when you have one)
        // drawCentered(batch, assets.robber(), overlay.robber)
        // 5. placement highlights on top
        // TODO FIXME: EDGE ZONES!
        overlay.frontier.values.forEach { p -> drawCentered(batch, iZoneTexture, p) }
        // DEBUG: hex labels
        debugLabels.forEach { (hex, p) ->
            debugFont.draw(batch, "${hex.q},${hex.r}", p.x.toFloat(), p.y.toFloat())
        }
        batch.end()
    }

    private fun drawCentered(batch: SpriteBatch, tex: TextureRegion, p: PCoord, yOff: Float = 0f) {
        batch.draw(
            tex,
            p.x.roundToInt().toFloat() - tex.regionWidth / 2f,
            p.y.roundToInt().toFloat() - tex.regionHeight / 2f + yOff
        )
    }
}

//
///**
// * Board renderer for LibGDX.
// */
//class BoardView(
//    val viewModel: BoardViewModel,
//    private val batch: SpriteBatch,
//    private val assets: Assets,
//    private val layout: Layout
//) : View {
//    private val logger = KotlinLogging.logger { }
//
//    private var debugFont = BitmapFont()
//
//
//    private var settlementFrontierMap: Map<NodeCoord, PCoord> = mapOf()
//    private var cityFrontierMap: Map<NodeCoord, PCoord> = mapOf()
//    private var roadFrontierMap: Map<EdgeCoord, PCoord> = mapOf()
//    private var setupSettlementFrontierMap: Map<NodeCoord, PCoord> =
//        BoardRenderUtils.mapNodes(
//            layout,
//            gameProjection.board.tiles.map { it.hexCoordinate }
//        )
//    private var activeFrontierMap: Map<out Coordinates, PCoord> = emptyMap()
//
//    /**
//     * Maps logic intersection coordinates to screen coordinates.
//     */
//    private var intersectionMap = BoardRenderUtils.mapNodes(
//        layout,
//        gameProjection.board.tiles.map { it.hexCoordinate }
//    ) // At this point this is highest size possible
//
//    /**
//     * Maps logic edge coordinates to screen coordinates.
//     *
//     */
//    private var edgeMap: Map<EdgeCoord, PCoord> = BoardRenderUtils.mapEdges(
//        layout,
//        gameProjection.board.tiles.map { it.hexCoordinate }
//    )
//
//    /**
//     * Map that tells the sprite batch what texture to render on a coordinate.
//     */
//    private var tileRenderMap: MutableMap<PCoord, TextureRegion> =
//        BoardRenderUtils.mapCompleteIsland(layout, assets, gameProjection.board.tiles, 3).toMutableMap()
//
//    private var debugHexPoints: MutableMap<HexCoord, PCoord> =
//        BoardRenderUtils.debugHexPoints(layout, gameProjection.board.tiles).toMutableMap()
//
//
//    /**
//     * Map that tells the sprite batch what token to render.
//     */
//    private var tokenRenderMap =
//        BoardRenderUtils.mapTokenTexture(gameProjection.board.tiles, layout, assets).toMutableMap()
//
//    /**
//     * TODO: Find way to add roads
//     */
//    private var roadRenderMap = mutableMapOf<PCoord, TextureRegion>()
//    private var villageRenderMap = mutableMapOf<PCoord, TextureRegion>()
//    private var robberLocation: Pair<HexCoord, PCoord> =
//        gameProjection.board.robberLocation to HexUtils.hexToPixel(layout, gameProjection.board.robberLocation)
//
//    /**
//     * The radius of the intersection hightlight clickable area in pixels.
//     */
//    private val intersectionZoneRadius = 20
//    private val intersectionZoneTexture: TextureRegion = assets.intersectionZoneMap[gameProjection.thisPlayer.color]!!
//
//
//    init {
//        registerOnPropertyChanges()
//    }
//
//    fun handleTouch(screenX: Int, screenY: Int, camera: OrthographicCamera) {
//        // Unproject: screen pixels -> world coordinates
//        val worldCoords = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
//
//        val hit = when {
//            viewModel.settlementPlacingMode -> findHit(settlementFrontierMap, worldCoords.x, worldCoords.y)
//            viewModel.cityPlacingMode -> findHit(cityFrontierMap, worldCoords.x, worldCoords.y)
//            viewModel.roadPlacingMode -> findHit(roadFrontierMap, worldCoords.x, worldCoords.y)
//            else -> null
//        }
//
//        hit?.let { (coordinates, _) ->
//            viewModel.onBoardTap(coordinates)
//        }
//    }
//
//    private fun findHit(
//        frontier: Map<out Coordinates, PCoord>,
//        worldX: Float,
//        worldY: Float
//    ): Map.Entry<Coordinates, PCoord>? {
//        return frontier.entries.find { (_, phys) ->
//            circleContains(phys.x.toFloat(), phys.y.toFloat(), intersectionZoneRadius.toFloat(), worldX, worldY)
//        }
//    }
//
//    private fun circleContains(cx: Float, cy: Float, radius: Float, px: Float, py: Float): Boolean {
//        val dx = px - cx
//        val dy = py - cy
//        return dx * dx + dy * dy < radius * radius  // squared distance -- avoids sqrt, same result
//    }


//    /**
//     * TODO: Implement for roads!
//     */
//    fun handleTouch(x: Int, y: Int, camera: Camera) {
//        println("TOUCH: $x, $y")
//
//        val checkContains: (Map<out Coordinates, PCoordinate>) -> Coordinates? = { map ->
//            map.entries.find { (_, phys) ->
//                circleContains(
//                    phys.x.toInt(),
//                    phys.y.toInt(),
//                    intersectionZoneRadius,
//                    x, // closure
//                    y
//                )
//            }?.key
//        }
//
//        if (viewModel.settlementPlacingMode) {
//            checkContains(settlementFrontierMap).let {
//                viewModel.handleBuild(BuildKind.Village(VillageKind.SETTLEMENT), it!!)
//            }
//        } else if (viewModel.cityPlacingMode) {
//            checkContains(cityFrontierMap).let {
//                viewModel.handleBuild(BuildKind.Village(VillageKind.CITY), it!!)
//            }
//        }
//    }
//
//    private fun drawAll(
//        coordinates: List<PCoord>,
//        texture: TextureRegion,
//        functionX: (PCoord, TextureRegion) -> Float = { coord, tex ->
//            coord.x.roundToInt().toFloat() - tex.regionWidth / 2
//        },
//        functionY: (PCoord, TextureRegion) -> Float = { coord, tex ->
//            coord.y.roundToInt().toFloat() - tex.regionHeight / 2
//        }
//    ) {
//        coordinates.forEach {
//            batch.draw(
//                texture,
//                functionX(it, texture),
//                functionY(it, texture)
//            )
//        }
//    }
//
//    fun render() {
//        batch.begin()
//        drawAll(tileRenderMap)
//        drawAll(
//            tokenRenderMap,
//            functionY = { coord, tex -> coord.y.roundToInt().toFloat() - (tex.regionHeight / 2) + 5 })
//        drawAll(roadRenderMap)
//        drawAll(villageRenderMap)
//        drawAll(
//            activeFrontierMap.values.toList(),
//            intersectionZoneTexture
//        )
//        // DEBUG
//        debugHexPoints.forEach { (a, b) ->
//            debugFont.draw(batch, "${a.q},${a.r}", b.x.toFloat(), b.y.toFloat())
//        }
//
//        batch.end()
//    }
//
//    /**
//     *
//     * @param functionX Offset function
//     * @param functionY Offset function
//     */
//    private fun drawAll(
//        map: Map<PCoord, TextureRegion>,
//        functionX: (PCoord, TextureRegion) -> Float = { coord, tex ->
//            coord.x.roundToInt().toFloat() - tex.regionWidth / 2
//        },
//        functionY: (PCoord, TextureRegion) -> Float = { coord, tex ->
//            coord.y.roundToInt().toFloat() - tex.regionHeight / 2
//        }
//    ) {
//        map.forEach { (coord, tex) ->
//            batch.draw(tex, functionX(coord, tex), functionY(coord, tex))
//        }
//    }

//    private fun mapIntersectionCoordinates(
//        layout: Layout,
//        hexCoordinates: List<HexCoordinates>
//    ): Map<ICoordinates, PCoordinate> {
//        return HexUtils.intersectionCoordinates(layout, hexCoordinates)
//    }
//
//    private fun mapEdgeCoordinates(
//        layout: Layout,
//        hexCoordinates: List<HexCoordinates>
//    ): Map<ICoordinates, PCoordinate> {
//        return HexUtils.edgeCoordinates(layout, hexCoordinates)
//    }
//
//
//    /**
//     * Maps Island textures. Tiles as well as tokens.
//     */
//    private fun mapIslandTextures(): Map<PCoordinate, TextureRegion> {
//        val hexTextures = mutableMapOf<PCoordinate, TextureRegion>()
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
//        viewModel.state.tiles.forEach { tile ->
//            val resource = tile.resource ?: Resource.NON_RESOURCE
//            val pCoord = HexUtils.hexToPixel(layout, tile.hexCoordinate)
//            hexTextures[pCoord] = assets.tileTextureMap[textureMap[resource]!!]!!
//            if (resource != Resource.NON_RESOURCE) {
//                tokenRenderMap[pCoord] = assets.tokenTextureMap[tile.rollListenValue]!!
//            }
//        }
//        return hexTextures
//    }
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
//    private fun mapSeaShoreTextures(width: Int): Map<PCoordinate, TextureRegion> {
//        val surroundingSeaShore = HexUtils.hexRing(width)
//        val seaShoretextures = mutableMapOf<PCoordinate, TextureRegion>()
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
//    private fun mapSeaTextures(width: Int): Map<PCoordinate, TextureRegion> {
//        val seaTextureMap = mutableMapOf<PCoordinate, TextureRegion>()
//        for (j in 0..<5) {
//            val seaRing = HexUtils.hexRing(width + j)
//            for (hex in seaRing) {
//                seaTextureMap[HexUtils.hexToPixel(layout, hex)] = assets.tileTextureMap[Assets.Asset.SEA]!!
//            }
//        }
//        return seaTextureMap
//    }
//
//    override fun registerOnPropertyChanges() {
//
//        viewModel.onPropertyChange(GameViewModel::settlementPlacingMode) { active ->
//            activeFrontierMap = if (active) settlementFrontierMap else emptyMap()
//        }
//        viewModel.onPropertyChange(GameViewModel::cityPlacingMode) { active ->
//            activeFrontierMap = if (active) cityFrontierMap else emptyMap()
//        }
//        viewModel.onPropertyChange(GameViewModel::roadPlacingMode) { active ->
//            activeFrontierMap = if (active) roadFrontierMap else emptyMap()
//        }
//        viewModel.onPropertyChange(GameViewModel::initSettlementPlacingMode) { active ->
//            logger.debug { "viewModel.initSettlementPlacingMode PropertyNotify" }
//            activeFrontierMap = if (active) setupSettlementFrontierMap else emptyMap()
//        }
//
//        viewModel.onPropertyChange(GameViewModel::settlementPlacingMode) {
//            logger.debug { "viewModel.settlementPlacingMode PropertyNotify" }
//
//        }
//
//        viewModel.onPropertyChange(GameViewModel::projection) { proj ->
//            logger.debug { "viewModel.projection PropertyNotify" }
//
//
//            proj.board.intersections.forEach {
//                val color = proj.colors[it.village.owner]
//                villageRenderMap[intersectionMap[it.coordinate]!!] = assets.villageTextureMap[color]!!
//            }
//
//            proj.board.paths.forEach {
//                val color = proj.colors[it.road.owner]
//                villageRenderMap[edgeMap[it.coordinate]!!] = assets.roadTexture[color]!!
//            }
//
//            robberLocation = proj.board.robberLocation to HexUtils.hexToPixel(layout, proj.board.robberLocation)
//
//
//            // Update placing mode maps.
//
//            setupSettlementFrontierMap = intersectionMap
//                .filterKeys { key -> key in proj.board.setupFrontier }
//
//            settlementFrontierMap = intersectionMap
//                .filterKeys { key -> key in proj.board.settlementFrontier }
//
//            cityFrontierMap = intersectionMap
//                .filterKeys { key -> key in proj.board.cityFrontier }
//
//            roadFrontierMap = edgeMap
//                .filterKeys { key -> key in proj.board.roadFrontier }
//        }
//    }
//}
