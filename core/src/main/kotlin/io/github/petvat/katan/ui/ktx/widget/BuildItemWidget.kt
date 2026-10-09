package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.PlayerColor
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.ui.ASSETS
import io.github.petvat.katan.ui.KatanAssets
import ktx.scene2d.*

/**
 *
 */
@Scene2dDsl
class BuildItemWidget(
    title: String,
    cost: ResourceMap,
    vp: Int,
    skin: Skin,
    val assets: KatanAssets,
) : Table(skin), KTable {

    private var ore: Label? = null
    private var wood: Label? = null
    private var wool: Label? = null
    private var wheat: Label? = null
    private var brick: Label? = null

    // TODO: Add WOOL, BRICK (and GRAIN) to ASSETS.Resource + atlas, then use them here.
    private val resourceAsset = mapOf(
        Resource.WOOD to ASSETS.Resource.LUMBER,
        Resource.ORE to ASSETS.Resource.ORE,
        Resource.WOOL to ASSETS.Resource.LUMBER,
        Resource.BRICK to ASSETS.Resource.ORE,
        Resource.WHEAT to ASSETS.Resource.LUMBER,
    )

    private fun resourceIcon(resource: Resource): Image =
        Image(assets.region(resourceAsset.getValue(resource)))

    // TODO: Player color is currently hardcoded to red inside KatanAssets.
    private val settlImg = Image(assets.village(PlayerColor.CLR1, VillageKind.SETTLEMENT))

    init {
        touchable = Touchable.enabled
        background = skin.getDrawable("slot")

        // Ugly but works for now.
        if (cost[Resource.WOOD] > 0) wood = scene2d.label("x${cost[Resource.WOOD]}")
        if (cost[Resource.ORE] > 0) ore = scene2d.label("x${cost[Resource.ORE]}")
        if (cost[Resource.WHEAT] > 0) wheat = scene2d.label("x${cost[Resource.WHEAT]}")
        if (cost[Resource.BRICK] > 0) brick = scene2d.label("x${cost[Resource.BRICK]}")
        if (cost[Resource.WOOL] > 0) wool = scene2d.label("x${cost[Resource.WOOL]}")


        val titleLabel = scene2d.label(title, defaultStyle, skin) {
            // setEllipsis(true) // it? But not available.
            // setEllipsis("...")
        }

        align(Align.left)

        add(titleLabel).colspan(9).top().left()
        row()

        wood?.let {
            add(resourceIcon(Resource.WOOD))
            add(it).pad(5f)
        }
        ore?.let {
            add(resourceIcon(Resource.ORE))
            add(it).pad(5f)
        }
        wool?.let {
            add(resourceIcon(Resource.WOOL))
            add(it).pad(5f)
        }
        brick?.let {
            add(resourceIcon(Resource.BRICK))
            add(it).pad(5f)
        }
        wheat?.let {
            add(it).pad(5f)
        }

        // TODO: No victory-point icon in the atlas yet; the "+vp" label carries the info.
        add(scene2d.label("+$vp")).pad(5f)

        add(settlImg).expandX().right()
    }
}

@Scene2dDsl
fun <S> KWidget<S>.buildItem(
    title: String,
    cost: ResourceMap,
    vp: Int,
    skin: Skin,
    assets: KatanAssets,
    init: (@Scene2dDsl BuildItemWidget).(S) -> Unit = {},
): BuildItemWidget = actor(BuildItemWidget(title, cost, vp, skin, assets), init)
