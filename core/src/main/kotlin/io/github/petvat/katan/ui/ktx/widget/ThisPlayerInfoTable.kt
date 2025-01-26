package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.shared.model.game.Resource
import io.github.petvat.katan.shared.model.game.ResourceMap
import ktx.scene2d.*

class ThisPlayerInfoTable(
    inventory: ResourceMap,
    victoryPoints: Int,
    skin: Skin
) : Table(skin), KTable {


    private val ore = label("Ore: ${inventory[Resource.ORE]}")
    private val wood = label("Wood: ${inventory[Resource.WOOD]}")
    private val wool = label("Wool: ${inventory[Resource.WOOL]}")
    private val wheat = label("Lumber: ${inventory[Resource.WHEAT]}")
    private val brick = label("Lumber: ${inventory[Resource.BRICK]}")
    private val vp = label("Victory points: $victoryPoints")

    init {
        ore
        wood
        wool
        wheat
        brick
        row()
        vp
    }

    private fun setText(label: Label, resource: Resource, map: ResourceMap) {
        label.setText("${resource.name}: ${map.get()[resource]}")
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
    inventory: ResourceMap,
    victoryPoints: Int,
    skin: Skin,
    init: ThisPlayerInfoTable.(S) -> Unit = {}
): ThisPlayerInfoTable = actor(ThisPlayerInfoTable(inventory, victoryPoints, skin), init)

