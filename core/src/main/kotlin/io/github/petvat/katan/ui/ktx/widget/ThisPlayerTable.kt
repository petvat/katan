package io.github.petvat.katan.ui.ktx.widget

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

    private val turn: Label

    init {
        wood = label("Wood: ${thisPlayerViewModel.inventory[Resource.WOOD]}")
        ore = label("Ore: ${thisPlayerViewModel.inventory[Resource.ORE]}")
        wool = label("Wool: ${thisPlayerViewModel.inventory[Resource.WOOL]}")
        wheat = label("Lumber: ${thisPlayerViewModel.inventory[Resource.WHEAT]}")
        brick = label("Lumber: ${thisPlayerViewModel.inventory[Resource.BRICK]}")
        row()
        vp = label("Victory points: ${thisPlayerViewModel.victoryPoints}")
        turn = label("")
    }

    private fun setResource(label: Label, resource: Resource, map: ResourceMap) {
        label.setText("${resource.name}: ${map.getMap()[resource]}")
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

