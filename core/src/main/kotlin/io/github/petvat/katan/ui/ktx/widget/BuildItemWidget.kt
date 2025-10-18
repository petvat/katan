package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.shared.model.game.ResourceMap
import ktx.actors.onChangeEvent
import ktx.scene2d.*

/**
 *
 */
@Scene2dDsl
class BuildItemWidget(
    title: String,
    cost: ResourceMap,
    vp: Int,
    skin: Skin
) : Table(skin), KTable {

    private var ore: Label? = null
    private var wood: Label? = null
    private var wool: Label? = null
    private var wheat: Label? = null
    private var brick: Label? = null

    private var oreImg = Image(Texture(Gdx.files.internal("./ore-simple-tex.png")))
    private var brickImg = Image(Texture(Gdx.files.internal("./brick-simple-tex.png")))
    private var woolImg = Image(Texture(Gdx.files.internal("./wool-simple-tex.png")))
    private var woodImg = Image(Texture(Gdx.files.internal("./assets/wood-simple-tex.png")))
    private var settlImg = Image(Texture(Gdx.files.internal("./assets/settl-v001.png")))
    private var vpImg = Image(Texture(Gdx.files.internal("./assets/vp-v001.png")))

    init {
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
            add(woodImg)
            add(it).pad(5f)
        }
        ore?.let {
            add(oreImg)
            add(it).pad(5f)
        }
        wool?.let {
            add(woolImg)
            add(it).pad(5f)
        }
        brick?.let {
            add(brickImg)
            add(it).pad(5f)
        }
        wheat?.let {
            add(wheat).pad(5f)
        }

        add(vpImg).pad(5f)

        add(scene2d.label("+$vp"))

        add(settlImg).expandX().right()


//        add(oreImg)
//        add(ore).pad(5f)
//        add(woolImg)
//        add(wool).pad(5f)
//        add(brickImg)
//        add(brick).pad(5f)
//        add(wheat).pad(5f)
    }


//    init {
//        // skin.getDrawable(selectedBgd)
//        background = skin.getDrawable("slot")
//
//
//        // TODO: Image here!
//
//        // TODO: Replace with image!
////        val costLabel = scene2d.label(
////            "${
////                cost.getMap().forEach { (resource, amount) ->
////                    if (amount > 0) {
////                        "${resource.name} : $amount"
////                    }
////                }
////            }"
////        ) {
////            onChangeEvent { }
////        }
//
////        add(titleLabel).top().left().growX().padLeft(2f).padTop(2f)
////        row()
////        add(costLabel)
//    }

}


@Scene2dDsl
fun <S> KWidget<S>.buildItem(
    title: String,
    cost: ResourceMap,
    vp: Int,
    skin: Skin,
    init: (@Scene2dDsl BuildItemWidget).(S) -> Unit = {},
): BuildItemWidget = actor(BuildItemWidget(title, cost, vp, skin), init)
