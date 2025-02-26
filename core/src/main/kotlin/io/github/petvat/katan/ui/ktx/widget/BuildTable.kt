package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.PlaceBuildingCommand
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import ktx.actors.onChangeEvent
import ktx.scene2d.*

@Scene2dDsl
class BuildTable(
    skin: Skin,
    val callback: (Event) -> Unit, // TODO: Command
) : Table(skin), KTable {

    init {
        addBuildItem("Settlement", VillageKind.SETTLEMENT.cost, BuildKind.Village(VillageKind.SETTLEMENT))
        row()
        addBuildItem("City", VillageKind.CITY.cost, BuildKind.Village(VillageKind.SETTLEMENT))
    }

    private fun addBuildItem(title: String, cost: ResourceMap, buildKind: BuildKind): BuildItemWidget {
        return scene2d.buildItem(title, cost, skin) {
            onChangeEvent { this@BuildTable.callback(PlaceBuildingCommand(buildKind)) }
        }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.buildTable(
    skin: Skin,
    callback: (Event) -> Unit,
    init: BuildTable.(S) -> Unit = {}
): BuildTable = actor(BuildTable(skin, callback), init)



