package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.ui.model.ThisPlayerViewModel
import ktx.scene2d.*

class ThisPlayerInfoTable(
    thisPlayerViewModel: ThisPlayerViewModel,
    skin: Skin
) : Table(skin), KTable {

    private val ore = label("Ore: ${thisPlayerViewModel.inventory[Resource.ORE]}")
    private val wood = label("Wood: ${thisPlayerViewModel.inventory[Resource.WOOD]}")
    private val wool = label("Wool: ${thisPlayerViewModel.inventory[Resource.WOOL]}")
    private val wheat = label("Lumber: ${thisPlayerViewModel.inventory[Resource.WHEAT]}")
    private val brick = label("Lumber: ${thisPlayerViewModel.inventory[Resource.BRICK]}")
    private val vp = label("Victory points: ${thisPlayerViewModel.victoryPoints}")

    private val turn = label("")

    init {
        ore
        wood
        wool
        wheat
        brick
        row()
        vp
        turn
    }

    private fun setText(label: Label, resource: Resource, map: ResourceMap) {
        label.setText("${resource.name}: ${map.get()[resource]}")
    }

    fun activateTurn() {
        turn.setText("TURN")
    }

    fun deactivateTurn() {
        turn.setText("")
    }

    fun update(resourceMap: ResourceMap?, victoryPoints: Int?) {
        resourceMap?.let {
            setText(ore, Resource.ORE, resourceMap)
            setText(wood, Resource.WOOD, resourceMap)
            setText(wool, Resource.WOOL, resourceMap)
            setText(wheat, Resource.WHEAT, resourceMap)

        }
        victoryPoints?.let { }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.thisPlayerInfo(
    thisPlayerViewModel: ThisPlayerViewModel,
    skin: Skin,
    init: ThisPlayerInfoTable.(S) -> Unit = {}
): ThisPlayerInfoTable = actor(ThisPlayerInfoTable(thisPlayerViewModel, skin), init)

