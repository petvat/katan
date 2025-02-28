package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.ui.model.ThisPlayerViewModel
import ktx.scene2d.*


class ThisPlayerTable(
    thisPlayerViewModel: ThisPlayerViewModel,
    skin: Skin
) : Table(skin), KTable {

    private val ore: Label
    private val wood: Label
    private val wool: Label
    private val wheat: Label
    private val brick: Label
    private val vp: Label

    var oreImg = Image(Texture(Gdx.files.internal("./assets/ore-simple-tex.png")))
    var brickImg = Image(Texture(Gdx.files.internal("./assets/brick-simple-tex.png")))
    var woolImg = Image(Texture(Gdx.files.internal("./assets/wool-simple-tex.png")))
    var woodImg = Image(Texture(Gdx.files.internal("./assets/wood-simple-tex.png")))

    private val turn: Label

    init {
        background = skin.getDrawable("area")
        wood = scene2d.label("Wood: ${thisPlayerViewModel.inventory[Resource.WOOD]}")
        ore = scene2d.label("Ore: ${thisPlayerViewModel.inventory[Resource.ORE]}")
        wool = scene2d.label("Wool: ${thisPlayerViewModel.inventory[Resource.WOOL]}")
        wheat = scene2d.label("Wheat: ${thisPlayerViewModel.inventory[Resource.WHEAT]}")
        brick = scene2d.label("Brick: ${thisPlayerViewModel.inventory[Resource.BRICK]}")
        vp = scene2d.label("Victory points: ${thisPlayerViewModel.victoryPoints}")
        turn = scene2d.label("") // TODO: replace with something better

        add(woodImg)
        add(wood).pad(5f)
        add(oreImg)
        add(ore).pad(5f)
        add(woolImg)
        add(wool).pad(5f)
        add(brickImg)
        add(brick).pad(5f)
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
    thisPlayerViewModel: ThisPlayerViewModel,
    skin: Skin,
    init: ThisPlayerTable.(S) -> Unit = {}
): ThisPlayerTable = actor(ThisPlayerTable(thisPlayerViewModel, skin), init)

