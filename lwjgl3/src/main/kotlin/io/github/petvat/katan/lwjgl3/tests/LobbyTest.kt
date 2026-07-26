package io.github.petvat.katan.lwjgl3.tests

import io.github.petvat.katan.event.GroupUpdateEvent
import io.github.petvat.katan.model.GroupSummary
import io.github.petvat.katan.ui.ktx.screen.loadUISkin
import io.github.petvat.katan.ui.ktx.view.LobbyView
import io.github.petvat.katan.ui.viewmodel.LobbyViewModel
//import io.mockk.every
//import io.mockk.mockk
import ktx.app.KtxGame
import ktx.scene2d.Scene2DSkin

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
        loadUISkin()
        addScreen(LobbyTest())
        setScreen<LobbyTest>()
    }
}


private class LobbyTest() : AbstractTestScreen() {

    private val groups = listOf(
        GroupSummary("id", 2, 4)
    )

    override fun setup() {
        val viewModel = LobbyViewModel(MockLobbyActions(), {}, groups.toMutableList())
        stage.addActor(
            LobbyView(viewModel, Scene2DSkin.defaultSkin)
        )

        repeat(10) {
            viewModel.onEvent(GroupUpdateEvent(GroupSummary("id $it", 2, 4)))
        }


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




