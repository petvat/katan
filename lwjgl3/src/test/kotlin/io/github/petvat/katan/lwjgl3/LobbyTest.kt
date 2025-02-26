package io.github.petvat.katan.lwjgl3

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Value.prefWidth
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.petvat.katan.controller.NioController
import io.github.petvat.katan.controller.RequestController
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.GameMode
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.PermissionLevel
import io.github.petvat.katan.shared.protocol.dto.PublicGroupDTO
import io.github.petvat.katan.ui.ktx.screen.loadVisUISkin
import io.github.petvat.katan.ui.ktx.view.LobbyView
import io.github.petvat.katan.ui.ktx.widget.GroupListTable
import io.github.petvat.katan.ui.ktx.widget.GroupListWidget
import io.github.petvat.katan.ui.ktx.widget.groupsWidget
import io.github.petvat.katan.ui.model.GroupModel
import io.github.petvat.katan.ui.model.LobbyViewModel
//import io.mockk.every
//import io.mockk.mockk
import ktx.app.KtxGame
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors

fun main() = gdxTest("UI Lobby test", LobbyViewTest())


//private class TestView<T : AbstractTestScreen> : KtxGame<T>() {
//
//    val screen: T
//
//    override fun create() {
//        loadVisUISkin()
//        addScreen(screen)
//    }
//}


private class LobbyViewTest : KtxGame<LobbyTest>() {
    override fun create() {
        loadVisUISkin()
        addScreen(LobbyTest())
        setScreen<LobbyTest>()
    }
}


private class LobbyTest() : AbstractTestScreen() {

    private val groups = List(15) {
        PublicGroupDTO("id", 4, 4, PermissionLevel.USER, GameMode.STANDARD)
    }

    override fun setup() {
        stage.addActor(
            LobbyView(LobbyViewModel(MockController(), {}, groups.toMutableList()), Scene2DSkin.defaultSkin)
        )
    }
}


//
//private class LobbyWidgetTest : AbstractTestScreen() {
//
//    private val groups = List(20) {
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER)
//    }
//
//    override fun setup() {
//        val groupsWgt: GroupListWidget
//
//        stage.actors {
//            groupsWgt = groupsWidget({ _, _ -> }) {
//            }
//
//        }
//        groupsWgt.update(groups)
//    }
//
//}

//
//private class LobbyViewTest2() : KtxGame<LobbyViewScreen>() {
//
//    override fun create() {
//        loadVisUISkin()
//        addScreen(LobbyViewScreen())
//        setScreen<LobbyViewScreen>()
//    }
//}
//
//private class LobbyViewScreen : KtxScreen {
//    private val vp = ScreenViewport()
//    private val batch by lazy { SpriteBatch() }
//    private val stage = Stage(vp, batch)
//
//    init {
//        vp.camera.position.y = 10f
//    }
//
//    private val groups = mutableListOf(
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//        GroupModel("1", GameMode.STANDARD, 4, PermissionLevel.USER),
//    )
//
//    override fun show() {
//
//        val multiplexer = InputMultiplexer();
//        multiplexer.addProcessor(stage)
//        Gdx.input.inputProcessor = multiplexer;
//
//        stage.isDebugAll = true
//
//        val groupsWgt: GroupListWidget
//
//        stage.actors {
//            groupsWgt = groupsWidget({ _, _ -> }) {
//            }
//
//        }
//        groupsWgt.update(groups)
//
//    }
//
//    override fun resize(width: Int, height: Int) {
//        stage.viewport.update(width, height, true)
//    }
//
//    override fun render(delta: Float) {
//        clearScreen(0f, 0f, 0f, 1f)
//        stage.act(delta)
//        stage.draw()
//    }
//
//    override fun dispose() {
//        stage.dispose()
//    }
//}




