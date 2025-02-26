package io.github.petvat.core.ui.ktx.widget

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.List
import ktx.actors.onKeyUp
import ktx.scene2d.*

@Scene2dDsl
class ChatWidget(
    messages: kotlin.collections.List<Pair<String, String>>?, // TODO: Use! Some day.
    callback: (String) -> Unit,
    skin: Skin
) : Table(skin), KTable {

    private val scroll: ScrollPane
    private val messageList: VerticalGroup
    private val textField: TextField

    init {
        debug = true
        // setFillParent(true)
        scroll = scrollPane {
            this@ChatWidget.messageList = verticalGroup { }
            actor = this@ChatWidget.messageList
        }

        textField = textField {
            onKeyUp {
                if (it == Input.Keys.ENTER) {
                    println(text)
                    callback(text)
                    text = "" // reset
                }
            }
        }

        messages?.let { update(it) }
    }

//    fun addMessage(from: String, message: String) {
//        // TODO: Capacity!
//
//        val messages = messageList.items
//        messages.add(MessageWidget(from, message, skin))
//        messageList.setItems(messages)
//    }

    /**
     * Testing version. Update whole message list at once.
     */
    fun update(messages: kotlin.collections.List<Pair<String, String>>) {

        messageList.clear()

        // val elements = groupList.items
        messages.forEach { message ->
            val name = "name"
            val element = MessageWidget(
                from = message.first,
                message = message.second,
                skin,
            )
            messageList.addActor(element)
        }
    }
}

/**
 * Represents a single chat message.
 */
@Scene2dDsl
class MessageWidget(
    from: String,
    message: String,
    skin: Skin
) : Table(skin), KTable {

    init {
        label("$from: $message") {
            it.left().padLeft(5f)
        }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.chat(
    messages: kotlin.collections.List<Pair<String, String>> = mutableListOf(),
    skin: Skin,
    callback: (String) -> Unit,
    init: ChatWidget.(S) -> Unit = {}
): ChatWidget = actor(ChatWidget(messages, callback, skin), init)



