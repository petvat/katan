package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.ui.model.LobbyViewModel
import ktx.actors.onClick
import ktx.scene2d.*

class GroupListWidget(
    private val lobbyViewModel: LobbyViewModel,
    private val skin: Skin
) : Table(), KTable {

    private val groupList = com.badlogic.gdx.scenes.scene2d.ui.List<Label>(skin)

    init {
        addGroup("Placeholder", id = "1")
        label("Groups")
        row()
        scrollPane {
            actor = this@GroupListWidget.groupList
        }
    }

    fun addGroup(display: String, groupName: String = "null", id: String) {
        val label = Label(display, skin)
        label.onClick {
            lobbyViewModel.handleJoin(id, groupName)
        }
        val groups = groupList.items
        groups.add(label)
        groupList.setItems(label)
    }
}


@Scene2dDsl
fun <S> KWidget<S>.groupsWidget(
    viewModel: LobbyViewModel,
    skin: Skin,
    init: GroupListWidget.(S) -> Unit = {}
): GroupListWidget = actor(GroupListWidget(viewModel, skin), init)
