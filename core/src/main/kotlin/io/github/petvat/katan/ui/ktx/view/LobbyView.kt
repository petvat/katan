package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.ui.ktx.widget.*
import io.github.petvat.katan.ui.viewmodel.LobbyViewModel
import ktx.actors.onClick
import ktx.scene2d.*
import kotlin.collections.toList

class LobbyView(
    val viewModel: LobbyViewModel,
    skin: Skin
) : KtxView(skin), KTable {

    private val logger = KotlinLogging.logger { }

    private val groupsWidget: GroupListWidget

    private val createWidget: CreateGroupWidget

    private val backBtn: TextButton

    init {
        setFillParent(true)
        align(Align.center)

        val innertlb = scene2d.table {
            background = skin.getDrawable("area")
            this@LobbyView.groupsWidget = scene2d.groupsWidget(this@LobbyView.viewModel::handleJoin, skin) { }
            this@LobbyView.createWidget = scene2d.createWidget(this@LobbyView.viewModel::handleCreate, skin) { }
            this@LobbyView.backBtn = scene2d.textButton("Back") {
                onClick { println("back - TODO") }
                align(Align.center)
            }
        }

        innertlb.add(groupsWidget).grow().spaceRight(20f)
        innertlb.add(createWidget).grow()
        innertlb.row()
        innertlb.add(backBtn).colspan(2).padTop(10f)

        add(innertlb).minWidth(100f).minHeight(100f).maxWidth(1500f).maxHeight(500f)

//        textButton("Refresh") {
//            it.expandX()
//            onChangeEvent {
//                println("CLICKED GET GROUPS")
//                viewModel.handleGetGroups()
//            }
//        }

        groupsWidget.update(viewModel.groupSummaries.values.toList())

        registerOnPropertyChanges()
    }

    override fun registerOnPropertyChanges() {
        viewModel.onPropertyChange(LobbyViewModel::groupSummaries) {
            logger.debug { "groups update" }
            groupsWidget.update(listOf(it.values.last())) // TODO: Fix this!
        }
    }
}
