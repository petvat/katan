package io.github.petvat.core.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import io.github.petvat.katan.ui.model.GroupModel
import io.github.petvat.katan.ui.model.LobbyViewModel
import ktx.actors.onChangeEvent
import ktx.actors.onClick
import ktx.scene2d.*

@Scene2dDsl
class GroupListElementWidget(
    groupName: String,
    mode: String,
    numClients: String,
    maxClients: String,
    skin: Skin,
    callback: () -> Unit
) : Table(skin), KTable {

    private var nameL: Label
    private var clientsL: Label
    private var modeL: Label
    private var joinButton: TextButton

    init {
        setFillParent(true)
        nameL = label(groupName) {
            it.growX()
            it.padRight(4f)
            it.padLeft(4f)
        }
        clientsL = label("$numClients / $maxClients") {
            it.expand()
        }
        modeL = label(mode) {
            it.growX()
            it.padRight(4f)
            it.padLeft(4f)
        }
        joinButton = textButton("Join") {
            it.growX()
            it.padRight(2f)
            it.padLeft(2f)
            onChangeEvent { callback() }
        }
    }

    fun update(groupName: String?, mode: String?, numClients: String?, maxClients: String?) {
        groupName?.let { nameL.setText(it) }
        mode?.let { modeL.setText(it) }
        numClients?.let { clientsL.setText("$it / $maxClients") }
    }
}


class GroupListWidget(
    private val lobbyViewModel: LobbyViewModel, // <- TODO use callback
    private val skin: Skin
) : Table(skin), KTable {

    private var groupLabel: Label
    private var groupList: VerticalGroup
    private var scroll: ScrollPane

    init {
        groupLabel = label("Groups")
        row().growY()
        scroll = scrollPane {
            this@GroupListWidget.groupList = verticalGroup { }
            actor = this@GroupListWidget.groupList
        }
    }


    fun updateGroupList(groups: List<GroupModel>) {

        groupList.clear()

        // val elements = groupList.items
        groups.forEach { group ->
            val name = "name"
            val element = groupElement(
                name,
                group.mode.name,
                group.numClients.toString(),
                "?",
                skin,
                { this@GroupListWidget.lobbyViewModel.handleJoin(group.id, name) },
            )
            groupList.addActor(element)
        }
        // scroll = scrollPane { actor = this@GroupListWidget.groupList } // NOTE: Necessary?
        // groupList.setItems(elements)
    }

    /**
     * TODO: Use id for dynamic rendering. Iterate through the list and update. Annoyed!
     */
//    fun updateGroup(id: String, mode: String, groupName: String = "null", numClients: Int, level: String) {
//        val label = Label(mode, skin)
//        label.onClick {
//            lobbyViewModel.handleJoin(numClients.toString(), groupName)
//        }
//        val groups = groupList.items
//        groups.add(label)
//        groupList.setItems(groups)
//    }

//    fun addGroup(mode: String, groupName: String = "null", members: Int) {
//        val label = Label(mode, skin)
//        label.onClick {
//            lobbyViewModel.handleJoin(members.toString(), groupName)
//        }
//        val groups = groupList.items
//        groups.add(label)
//        groupList.setItems(label)
//    }
}


@Scene2dDsl
fun <S> KWidget<S>.groupElement(
    groupName: String,
    mode: String,
    numClients: String,
    maxClients: String,
    skin: Skin,
    cmd: () -> Unit,
    init: GroupListElementWidget.(S) -> Unit = {}
): GroupListElementWidget = actor(GroupListElementWidget(groupName, mode, numClients, maxClients, skin, cmd), init)


@Scene2dDsl
fun <S> KWidget<S>.groupsWidget(
    viewModel: LobbyViewModel,
    skin: Skin,
    init: GroupListWidget.(S) -> Unit = {}
): GroupListWidget = actor(GroupListWidget(viewModel, skin), init)
