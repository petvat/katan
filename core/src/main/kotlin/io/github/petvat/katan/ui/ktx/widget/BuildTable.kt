package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.PlaceBuildingCommand
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import ktx.actors.onChangeEvent
import ktx.actors.onClick
import ktx.log.logger
import ktx.scene2d.*

@Scene2dDsl
class BuildTable(
    skin: Skin,
    val callback: (PlaceBuildingCommand<*>) -> Unit,
) : Table(skin), KTable { // TODO: This should be a window! Then we need a command view -> screen, using eventbus.


    val logger = KotlinLogging.logger { }
    val scrollPaneWidget: ScrollPaneWidget<BuildItemWidget>

    init {

        scrollPaneWidget = scene2d.scrollWidget(skin) { }

        val settlItem =
            addBuildItem("Settlement", VillageKind.SETTLEMENT.cost, 1, BuildKind.Village(VillageKind.SETTLEMENT))
        val cityItem = addBuildItem("City", VillageKind.CITY.cost, 2, BuildKind.Village(VillageKind.SETTLEMENT))

        scrollPaneWidget.add(settlItem)
        scrollPaneWidget.add(cityItem)

        add(scrollPaneWidget).growX()

//        add(settlItem).growX()
//        row()
//        add(cityItem)
    }

    fun toggleActive() {
        isVisible = !isVisible
    }

    private fun addBuildItem(title: String, cost: ResourceMap, vp: Int, buildKind: BuildKind): BuildItemWidget {
        return scene2d.buildItem(title, cost, vp, skin) {
            onClick {
                this@BuildTable.logger.debug { "Clicked on addBuildItem" }
                this@BuildTable.callback(PlaceBuildingCommand(buildKind))
            }
        }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.buildTable(
    skin: Skin,
    callback: (PlaceBuildingCommand<*>) -> Unit,
    init: BuildTable.(S) -> Unit = {}
): BuildTable = actor(BuildTable(skin, callback), init)



