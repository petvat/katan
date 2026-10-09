package io.github.petvat.katan.lwjgl3.tests

import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.model.command.GroupCommands
import io.github.petvat.katan.model.state.ClientState
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.Response
import io.github.petvat.katan.shared.protocol.dto.UserData
import io.github.petvat.katan.ui.loadUISkin
import io.github.petvat.katan.ui.ktx.view.GroupView
import io.github.petvat.katan.ui.ktx.widget.ChatWidget
import io.github.petvat.katan.ui.ktx.widget.chat
import io.github.petvat.katan.ui.viewmodel.GroupViewModel
import ktx.app.KtxGame
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors
import ktx.scene2d.scene2d
import ktx.scene2d.table

fun main() = gdxTest("Group test", GroupTestLauncher())


private class MockGroupCommands : GroupCommands {
    override fun chat(message: String) = null
    override fun leave() = null
    override fun init() = null
}

private class GroupTestLauncher : KtxGame<GroupTest>() {
    override fun create() {
        loadUISkin()
        addScreen(GroupTest())
        setScreen<GroupTest>()
    }
}

private class GroupTest : AbstractTestScreen() {

    val eventBus = EventSystem()

    /**
     * ClientState mutators are private-set, so seed the session through
     * the public InboundRouter-style handlers instead of direct assignment.
     */
    private val state = ClientState().apply {
        onGroupCreated(
            Response.GroupCreated(groupId = "1", settings = Settings(), chatId = "1")
        )
        onUserJoined(Response.UserJoined(groupId = "1", UserData(userId = "1", name = "P1")))
        onUserJoined(Response.UserJoined(groupId = "1", UserData(userId = "2", name = "P2")))
    }

    override fun setup() {
        val viewModel = GroupViewModel(
            state,
            MockGroupCommands(),
            transitionService = { } // no-op screen transition for the test
        )

        stage.addActor(
            GroupView(viewModel, Scene2DSkin.defaultSkin)
        )

        eventBus += viewModel
    }
}

private class ChatTestLauncher : KtxGame<ChatIsolatedTest>() {
    override fun create() {
        loadUISkin()
        addScreen(ChatIsolatedTest())
        setScreen<ChatIsolatedTest>()
    }
}

private class ChatIsolatedTest : AbstractTestScreen() {

    val messages = List(20) { "Name" to "Hello" }

    override fun setup() {
        val ch: ChatWidget

        stage.actors {
            table {
                setFillParent(true)
                ch = scene2d.chat(messages, Scene2DSkin.defaultSkin, {}) {
                }
                add(ch).grow()
            }
        }
    }
}
