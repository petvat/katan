package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.ui.ASSETS
import io.github.petvat.katan.ui.KatanAssets
import io.github.petvat.katan.ui.projection.ThisPlayerProjection
import ktx.scene2d.*


class ThisPlayerTable(
    thisPlayerVM: ThisPlayerProjection,
    skin: Skin,
    val assets: KatanAssets
) : Table(skin), KTable {

    private val ore: Label
    private val wood: Label
    private val wool: Label
    private val wheat: Label
    private val brick: Label
    private val vp: Label

    // var oreImg = Image(Texture(Gdx.files.internal("./ore-simple-tex.png")))
    //var brickImg = Image(Texture(Gdx.files.internal("./brick-simple-tex.png")))
    // var woolImg = Image(Texture(Gdx.files.internal("./wool-simple-tex.png")))
    // var woodImg = Image(Texture(Gdx.files.internal("./assets/wood-simple-tex.png")))

    private val turn: Label

    private fun resourceIcon(resource: Resource): Image =
        Image(assets.region(assetOf(resource)))

    private fun assetOf(resource: Resource): ASSETS.Resource = when (resource) {
        Resource.WOOD -> ASSETS.Resource.LUMBER
        Resource.ORE -> ASSETS.Resource.ORE
        else -> ASSETS.Resource.LUMBER
    }

    init {
        background = skin.getDrawable("area")
        wood = scene2d.label("x${thisPlayerVM.inventory[Resource.WOOD]}")
        ore = scene2d.label("x${thisPlayerVM.inventory[Resource.ORE]}")
        wool = scene2d.label("x${thisPlayerVM.inventory[Resource.WOOL]}")
        wheat = scene2d.label("x${thisPlayerVM.inventory[Resource.WHEAT]}")
        brick = scene2d.label("x${thisPlayerVM.inventory[Resource.BRICK]}")
        vp = scene2d.label("Victory points: ${thisPlayerVM.victoryPoints}")
        turn = scene2d.label("") // TODO: replace with something better

        add(resourceIcon(Resource.WOOD))
        add(wood).pad(5f)
        add(resourceIcon(Resource.ORE))
        add(ore).pad(5f)
        add(resourceIcon(Resource.WOOL))
        add(wool).pad(5f)
        add(resourceIcon(Resource.BRICK))
        add(brick).pad(5f)
        add(resourceIcon(Resource.WHEAT))
        add(wheat).pad(5f)
        row().colspan(9)
        add(vp).pad(5f)


    }

    private fun setResource(label: Label, resource: Resource, map: ResourceMap) {
        label.setText("${resource.name}: ${map[resource]}")
    }

    private fun setVictoryPoints(victoryPoints: Int) {
        vp.setText(victoryPoints.toString())
    }

    fun activateTurn() {
        turn.setText("Your turn!")
    }

    fun deactivateTurn() {
        turn.setText("")
    }

    fun update(resourceMap: ResourceMap?, victoryPoints: Int?) {
        resourceMap?.let {
            setResource(ore, Resource.ORE, resourceMap)
            setResource(wood, Resource.WOOD, resourceMap)
            setResource(wool, Resource.WOOL, resourceMap)
            setResource(wheat, Resource.WHEAT, resourceMap)

        }
        victoryPoints?.let {
            setVictoryPoints(it)
        }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.thisPlayerTable(
    thisPlayerProjection: ThisPlayerProjection,
    skin: Skin,
    assets: KatanAssets,
    init: ThisPlayerTable.(S) -> Unit = {}
): ThisPlayerTable = actor(ThisPlayerTable(thisPlayerProjection, skin, assets), init)

