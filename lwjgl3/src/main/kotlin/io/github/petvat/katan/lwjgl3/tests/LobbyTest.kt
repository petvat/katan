package io.github.petvat.katan.lwjgl3.tests//import io.mockk.every
//import io.mockk.mockk
import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.event.GroupUpdateEvent
import io.github.petvat.katan.model.KatanClient
import io.github.petvat.katan.model.command.LobbyCommands
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.dto.GroupExternal
import io.github.petvat.katan.ui.ktx.view.LobbyView
import io.github.petvat.katan.ui.loadUISkin
import io.github.petvat.katan.ui.viewmodel.LobbyViewModel
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
        GroupExternal(id = "id", memberCount = 2, capacity = 4)
    )

    override fun setup() {
        val commands = object : LobbyCommands {
            override fun register(name: String) = null
            override fun resume(token: String) = null
            override fun join(channelId: String) = null
            override fun create(settings: Settings) = null
        }

        val client = KatanClient(events = EventSystem())
        val viewModel = LobbyViewModel(client.state, commands, {}, groups.toMutableList())
        stage.addActor(
            LobbyView(viewModel, Scene2DSkin.defaultSkin)
        )

        repeat(10) {
            viewModel.onEvent(
                GroupUpdateEvent(GroupExternal(id = "clientId $it", memberCount = 2, capacity = 4))
            )
        }
    }
}

