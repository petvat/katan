package io.github.petvat.katan.ui

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.petvat.katan.controller.RequestController
import io.github.petvat.katan.event.GetGroupsEvent
import io.github.petvat.katan.shared.model.game.GameMode
import io.github.petvat.katan.shared.protocol.PermissionLevel
import io.github.petvat.katan.shared.protocol.dto.PublicGroupDTO
import io.github.petvat.katan.ui.ktx.screen.loadVisUISkin
import io.github.petvat.katan.ui.ktx.view.LobbyView
import io.github.petvat.katan.ui.model.LobbyViewModel
import io.mockk.every
import io.mockk.mockk
import ktx.app.KtxApplicationAdapter
import ktx.app.clearScreen
import ktx.math.vec2
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors
import net.bytebuddy.matcher.ElementMatchers.any

fun gdxTest(title: String, testListener: ApplicationListener, windowSize: Vector2 = vec2(640F, 480F)) {
    Lwjgl3Application(testListener, Lwjgl3ApplicationConfiguration().apply {
        setTitle(title)
        setWindowedMode(windowSize.x.toInt(), windowSize.y.toInt())
    })
}


fun main() = gdxTest("UI Lobby test", LobbyViewTest())


private class LobbyViewTest : KtxApplicationAdapter {

    private val vp = ScreenViewport()

    private val stage by lazy { Stage(vp) }

    private lateinit var lobbyView: LobbyView

    private val inputMock = mockk<RequestController>()

    private val groups = mutableListOf(
        PublicGroupDTO("1", 3, 4, PermissionLevel.USER, GameMode.STANDARD),
        PublicGroupDTO("1", 3, 4, PermissionLevel.USER, GameMode.STANDARD),
        PublicGroupDTO("1", 3, 4, PermissionLevel.USER, GameMode.STANDARD),
        PublicGroupDTO("1", 3, 4, PermissionLevel.USER, GameMode.STANDARD),
    )

    private val lobbyViewModel: LobbyViewModel = LobbyViewModel(inputMock, mockk(), groups)

    init {

        every { inputMock.handleGetGroup(any()) } answers {
            lobbyViewModel.onEvent(
                GetGroupsEvent(
                    listOf(
                        PublicGroupDTO("1", 1, 4, PermissionLevel.USER, GameMode.STANDARD)
                    )
                )
            )
        }
    }

    override fun create() {
        loadVisUISkin()
        stage.actors {
            this@LobbyViewTest.lobbyView = LobbyView(lobbyViewModel, Scene2DSkin.defaultSkin)
        }

        stage.isDebugAll = true
    }

    override fun render() {
        clearScreen(0f, 0f, 0f, 1f)
        stage.act()
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }
}
