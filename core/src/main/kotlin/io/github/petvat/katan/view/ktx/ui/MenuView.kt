package io.github.petvat.katan.view.ktx.ui

import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.utils.Align
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.controller.RequestController
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.view.ktx.KtxKatan
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.scene2d.*


/**
 * Main menu widget.
 */
class MenuView(
    game: KtxKatan,
    skin: Skin
) : Table(skin), KTable {

    init {
        setFillParent(true)
        //background("area")
        align(Align.center)
        debug = true
        textButton("Connect to host").onChange {
            if (game.controller.connectClient(null, null)) {
                game.showLobbyView()
            } else {
                game.showErrorView("Could not connect to server!")
            }
            // game.showLobbyView()
        }
        row()
        textButton("Settings").onChange { println("clicked") }
        row()
        textButton("Placeholder").onChange { println("clicked") }
    }
}

class LobbyView(
    game: KtxKatan,
    skin: Skin
) : Table(skin), KTable {

    private val groupWidget = groupsWidget(game, skin)

    init {

        setFillParent(true)
        //background("area")
        align(Align.center)
        debug = true

        groupWidget

        createView(game, skin)

        row()
            //.growX()
            .colspan(2)
        textButton("Back").onChange { print("Back - not impl.") }
    }

    fun addGroup(displayText: String, id: String) {
        groupWidget.addGroup(displayText, id)
    }
}


class GroupView(
    game: KtxKatan,
    skin: Skin
) : Table(skin), KTable {

    init {
        setFillParent(true)
        //background("area")
        align(Align.center)
        debug = true
        textButton("Start game").onChange {
            game.controller.handleInit()
        }
    }
}


class CreateGroupWidget(
    game: KtxKatan,
    private val skin: Skin
) : Table(skin), KTable {

    init {
        label("Create group:")
        row()
        label("Settings placeholder")
        row().growY()
        textButton("Create").onChange {
            game.controller.handleCreate(Settings())
        }

    }
}


class GroupsWidget(
    val game: KtxKatan,
    private val skin: Skin
) : Table(), KTable {

    private val groupList = List<Label>(skin)

    init {
        addGroup("Placeholder", "1")
        label("Groups")
        row()
        scrollPane {
            actor = this@GroupsWidget.groupList
        }
    }

    fun addGroup(display: String, id: String) {
        val label = Label(display, skin)
        label.onClick {
            game.controller.handleJoin(id)
        }
        val groups = groupList.items
        groups.add(label)
        groupList.setItems(label)
    }
}

class ChatUI(
    game: KtxKatan,
    skin: Skin
) : Table(skin), KTable {

    init {

    }
}


class GameHUD(
    game: KtxKatan,
    skin: Skin,
) : Table(skin), KTable {
    init {

    }
}

@Scene2dDsl
fun <S> KWidget<S>.groupsWidget(
    game: KtxKatan,
    skin: Skin,
    init: GroupsWidget.(S) -> Unit = {}
): GroupsWidget = actor(GroupsWidget(game, skin), init)


@Scene2dDsl
fun <S> KWidget<S>.gameView(
    game: KtxKatan,
    skin: Skin,
    init: MenuView.(S) -> Unit = {}
): MenuView = actor(MenuView(game, skin), init)

@Scene2dDsl
fun KTable.createView(
    game: KtxKatan,
    skin: Skin,
): CreateGroupWidget = actor(CreateGroupWidget(game, skin))


fun main() {

}
