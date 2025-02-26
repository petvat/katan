package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.ui.ktx.widget.*
import io.github.petvat.katan.ui.model.LobbyViewModel
import ktx.actors.onClick
import ktx.scene2d.KTable
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.textButton

class LobbyView(
    viewModel: LobbyViewModel,
    skin: Skin
) : View<LobbyViewModel>(skin, viewModel), KTable {

    private val logger = KotlinLogging.logger { }

    private val groupsWidget: GroupListWidget

    private val createWidget: CreateGroupWidget

    private val backBtn: TextButton

    init {
        setFillParent(true)
        align(Align.center)
        groupsWidget = scene2d.groupsWidget(viewModel::handleJoin, skin) { }
        createWidget = scene2d.createWidget(viewModel::handleCreate, skin) { }
        backBtn = scene2d.textButton("Back") {
            onClick { println("back - TODO") }
            align(Align.center)
        }

        add(groupsWidget).grow()
        add(createWidget).grow()
        row()
        add(backBtn).colspan(2)

//        textButton("Refresh") {
//            it.expandX()
//            onChangeEvent {
//                println("CLICKED GET GROUPS")
//                viewModel.handleGetGroups()
//            }
//        }

        groupsWidget.update(viewModel.groupModels.values.toList())

        registerOnPropertyChanges()
    }

    override fun registerOnPropertyChanges() {
        viewModel.onPropertyChange(LobbyViewModel::groupModels) {
            logger.debug { "groups update" }
            groupsWidget.update(it.values.toList())
        }
    }
}
