package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.shared.protocol.dto.GroupExternal
import ktx.actors.onChangeEvent
import ktx.scene2d.*

private typealias GdxList<T> = com.badlogic.gdx.scenes.scene2d.ui.List<T>


/**
 *
 * NOTE: Tested.
 */
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
        background = skin.getDrawable("slot")
        nameL = scene2d.label(groupName) {
            setAlignment(Align.center)
//            it.expandX()
//            it.padRight(4f)
//            it.padLeft(4f)
        }
        clientsL = scene2d.label("$numClients / $maxClients") {
            // it.expand()
        }
        modeL = scene2d.label(mode) {
            setAlignment(Align.center)
//            it.growX()
//            it.padRight(4f)
//            it.padLeft(4f)
        }
        joinButton = scene2d.textButton("Join") {
//            it.growX()
//            it.padRight(4f)
//            it.padLeft(4f)
            onChangeEvent {
                callback()
            }
        }

        add(nameL).growX().padRight(5f).padLeft(5f)
        add(clientsL).expandX().padRight(5f).padLeft(5f)
        add(modeL).expandX().padRight(5f).padLeft(5f)
        add(joinButton).expandX().padRight(5f).padLeft(5f)
    }

    fun update(groupName: String?, mode: String?, numClients: String?, maxClients: String?) {
        groupName?.let { nameL.setText(it) }
        mode?.let { modeL.setText(it) }
        numClients?.let { clientsL.setText("$it / $maxClients") }
    }
}


/**
 * TODO: Move this to separate file.
 */
class ScrollPaneWidget<T : Actor>(val skin: Skin, labelOnEmpty: String? = null) : ScrollPane(null, skin), KGroup {
    private val contentTable: Table
    private val emptyLabel: Label
    private var elementCount = 0

    init {
        fadeScrollBars = false
        setScrollingDisabled(true, false)
        contentTable = scene2d.table {
            align(Align.top)
        }
        emptyLabel = scene2d.label(labelOnEmpty ?: "") {
            setAlignment(Align.center)
        }
        contentTable.add(emptyLabel).padTop(20f).row()

        actor = contentTable
    }

    fun modify(element: T) {
        TODO("Should modify the element if it exists in the table.")
    }

    fun add(element: T) {
        val cell = contentTable.add(element).space(4f).growX().row()
        elementCount++
        updateEmptyState()
    }

    fun remove(element: T) {
        element.remove()
        elementCount--
        updateEmptyState()
    }

    private fun updateEmptyState() {
        emptyLabel.isVisible = elementCount == 0
    }
}


class GroupListWidget(skin: Skin, val callback: (String) -> Unit) : Table(skin), KTable {
    private val scrollPaneWidget: ScrollPaneWidget<GroupListElementWidget>
    private val widgetLabel: Label

    init {
        // setFillParent(true)
        align(Align.center)

        widgetLabel = scene2d.label("Lobby") {
            setFontScale(1.5f)
            setAlignment(Align.top)
        }
        scrollPaneWidget = scene2d.scrollWidget(skin) { }

        add(widgetLabel).top()
        row()
        add(scrollPaneWidget).expandX().padTop(10f)

    }

    fun update(groups: List<GroupExternal>) {
        groups.forEach { group ->
            val name = "New group"
            val element = scene2d.groupElement( // NOTE: need scene2d else does not display correctly (rtfm ...)
                name,
                group.mode.toString() ?: "Not specified", // TODO: CAST
                group.memberCount.toString(),
                group.capacity.toString(),
                skin,
                { this@GroupListWidget.callback(group.id) })

            scrollPaneWidget.add(element)
        }
    }

}


/**
 * NOTE: Tested. Alignment problems when wrapped in Table.
 */
//class GroupListTable(val skin: Skin, val callback: (String, String) -> Unit) : ScrollPane(null, skin), KGroup {
//    private val contentTable: Table
//
//    init {
//        setFillParent(true)
//        fadeScrollBars = false
//        setScrollingDisabled(true, false)
//
//        contentTable = scene2d.table(skin) {
//
//        }
//        actor = contentTable
//    }
//
//    fun update(groups: List<GroupModel>) {
//        contentTable.clear()
//        groups.forEach { group ->
//            val name = "name"
//            val element = scene2d.groupElement( // NOTE: need scene2d else does not display correctly (rtfm ...)
//                name,
//                group.mode.name,
//                group.numClients.toString(),
//                "?",
//                skin,
//                { this@GroupListTable.callback(group.clientId, name) })
//
//            val cell = contentTable.add(element)
//            cell.growX().spaceRight(5f)
//            cell.row()
//        }
//    }
//}


//
//class GroupListWidget(
//    private val callback: (String, String) -> Unit,
//    private val skin: Skin
//) : Table(skin), KTable {
//
//    private var groupList = Table(skin)
//
//    init {
//
//        scrollPane {
//            this@GroupListWidget.groupList
//        }
//    }
//
//    fun updateGroupList(groups: List<GroupModel>) {
//        groupList.clear()
//        // val elements = groupList.items
//        // elements.clear()
//        groups.forEach { group ->
//            val name = "name"
//            val element = GroupListElementWidget(
//                name,
//                group.mode.name,
//                group.numClients.toString(),
//                "?",
//                skin,
//            ) { this@GroupListWidget.callback(group.clientId, name) }
//            val cell = groupList.add(element)
//            cell.row()
//            // elements.add(element)
//        }
//        // scroll = scrollPane { actor = this@GroupListWidget.groupList } // NOTE: Necessary?
//        // groupList.setItems(elements)
//    }
//
//}


@Scene2dDsl
fun <S, T : Actor> KWidget<S>.scrollWidget(
    skin: Skin = Scene2DSkin.defaultSkin,
    labelOnEmpty: String? = null,
    init: ScrollPaneWidget<T>.(S) -> Unit = {}
): ScrollPaneWidget<T> = actor(ScrollPaneWidget(skin, labelOnEmpty), init)


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
    callback: (String) -> Unit,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: GroupListWidget.(S) -> Unit = {}
): GroupListWidget = actor(GroupListWidget(skin, callback), init)
