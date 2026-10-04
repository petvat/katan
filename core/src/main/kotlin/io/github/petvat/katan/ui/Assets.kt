package io.github.petvat.katan.ui

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.petvat.katan.shared.hexlib.EdgeCoord
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.PlayerColor


enum class RoadOrientation { VERT, HOR1, HOR2 }

/**
 * Infers road orientation from the edge's doubled coordinate parity.
 * Edge coords are the single-axial coords of the hex across the edge, so:
 *  - q even, r odd  → "\" (e.g. top-right / bottom-left edges)
 *  - q odd,  r even → "/" (e.g. bottom-right / top-left edges)
 *  - q odd,  r odd  → "|" (e.g. left / right edges)
 */
fun EdgeCoord.roadOrientation(): RoadOrientation = when {
    q % 2 == 0 -> RoadOrientation.HOR1  // "\"
    r % 2 == 0 -> RoadOrientation.HOR2  // "/"
    else -> RoadOrientation.VERT        // "|"
}

sealed interface ASSETS {
    enum class Board {
        MOUNTAINS, FIELDS, PASTURE,
        FOREST, DESERT, HILLS,

        TOKEN_2, TOKEN_3, TOKEN_4,
        TOKEN_5, TOKEN_6, TOKEN_8,
        TOKEN_9, TOKEN_10, TOKEN_11, TOKEN_12,

        // D = Down, U = Up, R = Right, L = Left, A = And
        SEA_DAL, SEA_DAR, SEA_DL, SEA_DR, SEA_L, SEA_R,
        SEA_UAL, SEA_UAR, SEA_UL, SEA_UR, SEA_U, SEA_D,
        SEA;

        val path = name.lowercase()

        companion object {
            fun token(value: Int): Board = valueOf("TOKEN_$value")
        }
    }

    enum class Player {
        SETTLEMENT, CITY, ROAD_VERT, ROAD_HOR1, ROAD_HOR2, SELECT_ZONE;

        /**
         * Player specific assets becomes $enum_$color
         */
        fun path(color: PlayerColor) =
            "${name.lowercase()}_${color.name.lowercase()}"
    }
}


/**
 * Game assets.
 */
class Assets {

    private val manager = AssetManager()

    /**
     * load() must be called before using this.
     */
    private lateinit var boardAtlas: TextureAtlas

    init {
        load()
    }

    private inline fun <reified E : Enum<E>> regionsOf(nameOf: (E) -> String): Map<E, TextureRegion> =
        enumValues<E>().associateWith { e ->
            requireNotNull(boardAtlas.findRegion(nameOf(e))) { "Atlas region missing: '${nameOf(e)}'" }
        }

    private val boardRegions: Map<ASSETS.Board, TextureRegion> by lazy {
        regionsOf<ASSETS.Board> { it.path }
    }

    private fun zone(color: PlayerColor): TextureRegion =
        requireNotNull(boardAtlas.findRegion(ASSETS.Player.SELECT_ZONE.path(color))) {
            "Atlas region missing: '${ASSETS.Player.SELECT_ZONE.path(color)}'"
        }

    private fun playerRegion(kind: ASSETS.Player, color: PlayerColor): TextureRegion =
        requireNotNull(boardAtlas.findRegion(kind.path(color))) {
            "Atlas region missing: '${kind.path(color)}'"
        }

    fun region(asset: ASSETS.Board) = boardRegions.getValue(asset)
    fun token(value: Int) = boardRegions.getValue(ASSETS.Board.token(value))
    fun nodeZone(color: PlayerColor) = zone(color)
    fun edgeZone(color: PlayerColor) = zone(color) // TODO: separate edge-zone asset

    fun village(color: PlayerColor, kind: VillageKind) = when (kind) {
        VillageKind.SETTLEMENT -> playerRegion(ASSETS.Player.SETTLEMENT, color)
        VillageKind.CITY -> playerRegion(ASSETS.Player.CITY, color)
    }

    fun road(color: PlayerColor, orientation: RoadOrientation) = when (orientation) {
        RoadOrientation.VERT -> playerRegion(ASSETS.Player.ROAD_VERT, color)
        RoadOrientation.HOR1 -> playerRegion(ASSETS.Player.ROAD_HOR1, color)
        RoadOrientation.HOR2 -> playerRegion(ASSETS.Player.ROAD_HOR2, color)
    }

    companion object {
        const val KATAN_GRAPHICS_F = "./katan-graphics-v3.atlas"
        //const val KATAN_UI_F = "./katan-ui-001.json"
    }

    private fun load() {
        manager.load(KATAN_GRAPHICS_F, TextureAtlas::class.java)
        manager.finishLoading()
        boardAtlas = manager.get(KATAN_GRAPHICS_F)

        for (texture in boardAtlas.textures) {
            texture.setFilter(
                Texture.TextureFilter.Linear,
                Texture.TextureFilter.Nearest
            )
        }
    }

    fun dispose() {
        manager.dispose()
    }
}
