package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.scene2d.*


//abstract class ListWidget<S, T>(
//    list: kotlin.collections.List<S>?,
//    skin: Skin
//) : Table(skin), KTable where T : Actor {
//
//    private val elements = List<Type>(skin)
//
//    init {
//
//        debug = true
//        // setFillParent(true)
//        scrollPane {
//            actor = this@ListWidget.elements
//        }
//
//        list?.forEach {
//            //addElement(it)
//        }
//    }

//    fun addElement(element: S) {
//        val elements = this.elements.items
//        elements.add(ElementWidget<S>(element, skin))
//    }
//}

//@Scene2dDsl
//abstract class ElementWidget<S>(
//    value: S,
//    skin: Skin
//) : Table(skin), KGroup


@Scene2dDsl
class ChatWidget(
    messages: kotlin.collections.List<Pair<String, String>>?,
    skin: Skin
) : Table(skin), KTable {

    private val messageList = List<MessageWidget>(skin) // TODO: init with messages

    init {

        debug = true
        // setFillParent(true)
        scrollPane {
            actor = this@ChatWidget.messageList
        }

        messages?.forEach {
            addMessage(it.first, it.second)
        }
    }

    fun addMessage(from: String, message: String) {
        // TODO: Capacity!

        val messages = messageList.items
        messages.add(MessageWidget(from, message, skin))
        messageList.setItems(messages)
    }

    /**
     * Testing version. Update whole message list at once.
     */
    fun update(messages: kotlin.collections.List<Pair<String, String>>) {
        val msgs = messageList.items
        messages.forEach {
            msgs.add(MessageWidget(it.first, it.second, skin))
        }
        messageList.setItems(msgs)
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
    init: ChatWidget.(S) -> Unit = {}
): ChatWidget = actor(ChatWidget(messages, skin), init)



