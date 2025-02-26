package io.github.petvat.katan.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.petvat.katan.shared.model.game.GameMode
import io.github.petvat.katan.shared.protocol.PermissionLevel
import io.github.petvat.katan.shared.protocol.dto.PublicGroupDTO
import io.github.petvat.katan.ui.ktx.screen.loadVisUISkin
import io.github.petvat.katan.ui.ktx.view.LobbyView
import io.github.petvat.katan.ui.model.LobbyViewModel
import ktx.app.KtxApplicationAdapter

//import io.mockk.mockk
//import ktx.app.clearScreen
//import ktx.scene2d.Scene2DSkin
//import ktx.scene2d.actors
//
//
//fun main() {
//    println("Running Lobby test")
//    gdxTest("UI Lobby test", LobbyViewTest())
//}
//
//private class LobbyViewTest : KtxApplicationAdapter {
//
//    private val vp = ScreenViewport()
//
//    private val stage by lazy { Stage(vp) }
//
//    private lateinit var lobbyView: LobbyView
//
//    // private val inputMock = mockk<RequestController>()
//
//    private val groups = mutableListOf(
//        PublicGroupDTO("1", 3, 4, PermissionLevel.USER, GameMode.STANDARD),
//        PublicGroupDTO("1", 3, 4, PermissionLevel.USER, GameMode.STANDARD),
//        PublicGroupDTO("1", 3, 4, PermissionLevel.USER, GameMode.STANDARD),
//        PublicGroupDTO("1", 3, 4, PermissionLevel.USER, GameMode.STANDARD),
//    )
//
//    lateinit var lobbyViewModel: LobbyViewModel
//
//    init {
//
////        every { inputMock.handleGetGroup(any()) } answers {
////            lobbyViewModel.onEvent(
////                GetGroupsEvent(
////                    listOf(
////                        PublicGroupDTO("1", 1, 4, PermissionLevel.USER, GameMode.STANDARD)
////                    )
////                )
////            )
////        }
//    }
//
//    override fun create() {
//        loadVisUISkin()
//        lobbyViewModel = LobbyViewModel(mockk(), mockk(), groups)
//        stage.actors {
//            this@LobbyViewTest.lobbyView = LobbyView(lobbyViewModel, Scene2DSkin.defaultSkin)
//        }
//        stage.isDebugAll = true
//    }
//
//    override fun render() {
//        clearScreen(0f, 0f, 0f, 1f)
//        stage.act()
//        stage.draw()
//    }
//
//    override fun dispose() {
//        stage.dispose()
//    }
//}
