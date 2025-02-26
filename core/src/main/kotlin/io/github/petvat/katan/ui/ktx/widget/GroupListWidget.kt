package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.model.GroupModel
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
        nameL = label(groupName) {
            setAlignment(Align.center)
            it.growX()
            it.padRight(4f)
            it.padLeft(4f)
        }
        clientsL = label("$numClients / $maxClients") {
            it.expand()
        }
        modeL = label(mode) {
            setAlignment(Align.center)
            it.growX()
            it.padRight(4f)
            it.padLeft(4f)
        }
        joinButton = textButton("Join") {
            it.growX()
            it.padRight(4f)
            it.padLeft(4f)
            onChangeEvent {
                callback()
            }
        }
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
class ScrollPaneWidget<T : Actor>(val skin: Skin) : ScrollPane(null, skin), KGroup {
    private val contentTable: Table

    init {
        fadeScrollBars = false
        setScrollingDisabled(true, false)
        contentTable = scene2d.table {

        }
        actor = contentTable
    }

    fun modify(element: T) {
        TODO("Should modify the element if it exists in the table.")
    }

    fun add(element: T) {
        val cell = contentTable.add(element)
        cell
            .growX()
            .row()
    }
}


class GroupListWidget(skin: Skin, val callback: (String, String) -> Unit) : Table(skin), KTable {
    private val scrollPaneWidget: ScrollPaneWidget<GroupListElementWidget>
    private val widgetLabel: Label

    init {
        // setFillParent(true)
        align(Align.center)

        widgetLabel = scene2d.label("Lobby") {
            setAlignment(Align.center)
        }
        scrollPaneWidget = scene2d.scrollWidget(skin) { }

        add(widgetLabel)
        row()
        add(scrollPaneWidget).growX()

    }

    fun update(groups: List<GroupModel>) {
        groups.forEach { group ->
            val name = "name"
            val element = scene2d.groupElement( // NOTE: need scene2d else does not display correctly (rtfm ...)
                name,
                group.mode.name,
                group.numClients.toString(),
                "?",
                skin,
                { this@GroupListWidget.callback(group.id, name) })

            scrollPaneWidget.add(element)
        }
    }

}


/**
 * NOTE: Tested. Alignment problems when wrapped in Table.
 */
class GroupListTable(val skin: Skin, val callback: (String, String) -> Unit) : ScrollPane(null, skin), KGroup {
    private val contentTable: Table

    init {
        setFillParent(true)
        fadeScrollBars = false
        setScrollingDisabled(true, false)

        contentTable = scene2d.table(skin) {

        }
        actor = contentTable
    }

    fun update(groups: List<GroupModel>) {
        contentTable.clear()
        groups.forEach { group ->
            val name = "name"
            val element = scene2d.groupElement( // NOTE: need scene2d else does not display correctly (rtfm ...)
                name,
                group.mode.name,
                group.numClients.toString(),
                "?",
                skin,
                { this@GroupListTable.callback(group.id, name) })

            val cell = contentTable.add(element)
            cell.growX()
            cell.row()
        }
    }
}


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
//            ) { this@GroupListWidget.callback(group.id, name) }
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
    init: ScrollPaneWidget<T>.(S) -> Unit = {}
): ScrollPaneWidget<T> = actor(ScrollPaneWidget(skin), init)


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
    callback: (String, String) -> Unit,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: GroupListWidget.(S) -> Unit = {}
): GroupListWidget = actor(GroupListWidget(skin, callback), init)
