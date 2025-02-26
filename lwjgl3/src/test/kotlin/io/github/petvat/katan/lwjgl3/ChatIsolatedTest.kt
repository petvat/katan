package io.github.petvat.katan.lwjgl3

import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.ktx.screen.loadVisUISkin
import io.github.petvat.katan.ui.ktx.widget.ChatWidget
import io.github.petvat.katan.ui.ktx.widget.chat
import ktx.app.KtxGame
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors
import ktx.scene2d.scene2d
import ktx.scene2d.table

fun main() = gdxTest("Group test", GroupViewTest())

private class GroupViewTest : KtxGame<GroupTest>() {
    override fun create() {
        loadVisUISkin()
        addScreen(GroupTest())
        setScreen<GroupTest>()
    }
}

private class GroupTest : AbstractTestScreen() {

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
