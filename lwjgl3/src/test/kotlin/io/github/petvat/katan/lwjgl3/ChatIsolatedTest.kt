package io.github.petvat.katan.lwjgl3

import io.github.petvat.katan.event.ChatEvent
import io.github.petvat.katan.event.EventBus
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.PermissionLevel
import io.github.petvat.katan.shared.protocol.dto.PrivateGroupDTO
import io.github.petvat.katan.ui.ktx.screen.loadVisUISkin
import io.github.petvat.katan.ui.ktx.view.GroupView
import io.github.petvat.katan.ui.ktx.widget.ChatWidget
import io.github.petvat.katan.ui.ktx.widget.chat
import io.github.petvat.katan.ui.model.GroupViewModel
import ktx.app.KtxGame
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors
import ktx.scene2d.scene2d
import ktx.scene2d.table

fun main() = gdxTest("Group test", GroupTestLauncher())


/**
 * TODO: Provide an abstraction between DTO and client state
 */

private class GroupTestLauncher : KtxGame<GroupTest>() {
    override fun create() {
        loadVisUISkin()
        addScreen(GroupTest())
        setScreen<GroupTest>()
    }
}

private class GroupTest() : AbstractTestScreen() {

    private val group =
        PrivateGroupDTO(
            "id",
            mutableMapOf("id" to "name"),
            PermissionLevel.USER,
            mutableListOf("name" to "Some message ..."),
            Settings()
        )

    override fun setup() {
        val viewModel = GroupViewModel(group, MockController(), { })

        stage.addActor(
            GroupView(viewModel, Scene2DSkin.defaultSkin)
        )

        EventBus += viewModel

        viewModel.onEvent(ChatEvent("Player1", "Hello"))
    }
}

private class ChatTestLauncher : KtxGame<ChatIsolatedTest>() {
    override fun create() {
        loadVisUISkin()
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
                add(ch).growX()
            }

            //}

        }
    }
}
