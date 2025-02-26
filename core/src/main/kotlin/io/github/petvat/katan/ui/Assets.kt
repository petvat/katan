package io.github.petvat.core.ui

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.ui.model.PlayerColor


/**
 * Game assets.
 */
class Assets {

    val manager = AssetManager()

    /**
     * load() must be called before using this.
     */
    private lateinit var boardAtlas: TextureAtlas

    init {
        load()
    }

    val villageTextureMap = mapOf<PlayerColor, TextureRegion>(
        PlayerColor.RED to boardAtlas.findRegion("TODO")
    )

    val roadTexture = mapOf<PlayerColor, TextureRegion>(
        PlayerColor.RED to boardAtlas.findRegion("TODO")
    )

    val tileResourceMap = mapOf<Resource?, Asset>(
        Resource.WOOL to Asset.PASTURE,
        Resource.WOOD to Asset.FOREST,
        Resource.WHEAT to Asset.GRAIN,
        Resource.ORE to Asset.MOUNTAINS,
        Resource.BRICK to Asset.HILLS,
        Resource.NON_RESOURCE to Asset.DESERT
    )

    // Raw images
    val tileTextureMap = mapOf<Asset, TextureRegion>(
        Asset.MOUNTAINS to boardAtlas.findRegion("mountain-katan"),
        Asset.PASTURE to boardAtlas.findRegion("katan-tile-pasture"),
        Asset.GRAIN to boardAtlas.findRegion("katan_grain_110px"),
        Asset.FOREST to boardAtlas.findRegion("katan-tile-forest"),
        Asset.SEA to boardAtlas.findRegion("sea"),
        Asset.SEA_DAL to boardAtlas.findRegion("sea_dal"),
        Asset.SEA_DAR to boardAtlas.findRegion("sea_dar"),
        Asset.SEA_DL to boardAtlas.findRegion("sea_dl"),
        Asset.SEA_DR to boardAtlas.findRegion("sea_dr"),
        Asset.SEA_L to boardAtlas.findRegion("sea_l"),
        Asset.SEA_R to boardAtlas.findRegion("sea_r"),
        Asset.SEA_UAL to boardAtlas.findRegion("sea_ual"),
        Asset.SEA_UAR to boardAtlas.findRegion("sea_uar"),
        Asset.SEA_UL to boardAtlas.findRegion("sea_ul"),
        Asset.SEA_UR to boardAtlas.findRegion("sea_ur"),
        Asset.SEA_U to boardAtlas.findRegion("sea_u"),
        Asset.SEA_D to boardAtlas.findRegion("sea_d")
    )
    val tokenTextureMap = mapOf<Int, TextureRegion>(
        2 to boardAtlas.findRegion("token-2"),
        3 to boardAtlas.findRegion("token-3"),
        4 to boardAtlas.findRegion("token-4"),
        5 to boardAtlas.findRegion("token-5"),
        6 to boardAtlas.findRegion("token-6"),
        8 to boardAtlas.findRegion("token-8"),
        9 to boardAtlas.findRegion("token-9"),
        10 to boardAtlas.findRegion("token-10"),
        11 to boardAtlas.findRegion("token-11"),
        12 to boardAtlas.findRegion("token-12"),
    )

    enum class Asset {
        GRAIN, FOREST, PASTURE, MOUNTAINS, HILLS, DESERT,
        SEA_DAL, SEA_DAR, SEA_DL, SEA_DR, SEA_L, SEA_R, SEA_UAL, SEA_UAR, SEA_UL, SEA_UR, SEA_U, SEA_D, SEA
    }

    companion object {
        const val PATH_PREFIX = "assets/"

        // val BOARD_ATLAS = TextureAtlas(Gdx.files.internal("assets/katan-board.atlas"))

        private val ASSET_PATHS = mapOf(
            Asset.PASTURE to "${PATH_PREFIX}katan-tile-pasture.png",
            Asset.GRAIN to "${PATH_PREFIX}katan_grain_110px.png",
            Asset.FOREST to "${PATH_PREFIX}katan-tile-forest.png",
            Asset.SEA_DAL to "${PATH_PREFIX}sea_dal.png",
            Asset.SEA_DAR to "${PATH_PREFIX}sea_dar.png",
            Asset.SEA_DL to "${PATH_PREFIX}sea_dl.png",
            Asset.SEA_DR to "${PATH_PREFIX}sea_dr.png",
            Asset.SEA_L to "${PATH_PREFIX}sea_l.png",
            Asset.SEA_R to "${PATH_PREFIX}sea_r.png",
            Asset.SEA_UAL to "${PATH_PREFIX}sea_ual.png",
            Asset.SEA_UAR to "${PATH_PREFIX}sea_uar.png",
            Asset.SEA_UL to "${PATH_PREFIX}sea_ul.png",
            Asset.SEA_UR to "${PATH_PREFIX}sea_ur.png",
            Asset.SEA_U to "${PATH_PREFIX}sea_u.png",
            Asset.SEA_D to "${PATH_PREFIX}sea_d.png"
        )


        val ASSET_DESCRIPTORS = ASSET_PATHS.mapValues { toDesc(it.value, Texture::class.java) }

        fun getDescriptor(asset: Asset): AssetDescriptor<Texture> {
            return ASSET_DESCRIPTORS[asset]!! // HACK: fix
        }

        private fun <T> toDesc(path: String, param: Class<T>): AssetDescriptor<T> {
            return AssetDescriptor(path, param)
        }
    }


    private fun load() {
        // ASSET_DESCRIPTORS.values.forEach { manager.load(it) }
        manager.load("./katan-board.atlas", TextureAtlas::class.java)
        manager.finishLoading()
        boardAtlas = manager.get("./katan-board.atlas")
    }

    fun dispose() {
        manager.dispose()
    }


}
