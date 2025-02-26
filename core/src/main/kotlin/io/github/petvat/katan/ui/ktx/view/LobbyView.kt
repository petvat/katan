package io.github.petvat.core.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.ui.ktx.widget.GroupListWidget
import io.github.petvat.katan.ui.ktx.widget.createWidget
import io.github.petvat.katan.ui.ktx.widget.groupsWidget
import io.github.petvat.katan.ui.model.LobbyViewModel
import ktx.actors.onChange
import ktx.actors.onChangeEvent
import ktx.scene2d.KTable
import ktx.scene2d.textButton

class LobbyView(
    viewModel: LobbyViewModel,
    skin: Skin
) : KTable, View<LobbyViewModel>(skin, viewModel) {

    private val logger = KotlinLogging.logger { }

    private val groupsWidget: GroupListWidget

    init {
        setFillParent(true)
        //background("area")
        align(Align.center)

        groupsWidget = groupsWidget(viewModel, skin) { /*it.expand()*/ }
        createWidget(viewModel, skin)
        row()
        textButton("Refresh") {
            it.expandX()
            onChangeEvent {
                println("CLICKED GET GROUPS")
                viewModel.handleGetGroups()
            }
        }
        row()
            //.growX()
            .colspan(2)
        textButton("Back").onChange { print("Back - not impl.") }

        registerOnPropertyChanges()
    }

    override fun registerOnPropertyChanges() {
        viewModel.onPropertyChange(LobbyViewModel::groupModels) {
            logger.debug { "groups update" }
            groupsWidget.updateGroupList(it.values.toList())
        }
    }
}
