package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.actors.onKeyUp
import ktx.scene2d.*


private typealias gdxList<T> = com.badlogic.gdx.scenes.scene2d.ui.List<T>

//
//@Scene2dDsl
//class ChatWidget(
//    messages: List<Pair<String, String>>,
//    callback: (String) -> Unit,
//    val skin: Skin
//) : ScrollPane(null, skin), KGroup {
//
//    private val contentTable: Table
//
//    init {
//        setFillParent(true)
//        fadeScrollBars = false
//        setScrollingDisabled(true, false)
//
//        contentTable = scene2d.table(skin) { }
//        actor = contentTable
//
////        textField = textField {
////            onKeyUp {
////                if (it == Input.Keys.ENTER) {
////                    println(text)
////                    callback(text)
////                    text = "" // reset
////                }
////            }
////        }
//        update(messages)
//    }
//
//    fun update(messages: List<Pair<String, String>>) {
//        contentTable.clear()
//        messages.forEach {
//            val element = MessageWidget(it.first, it.second, skin)
//            val cell = contentTable.add(element)
//            cell.growX()
//            cell.row()
//        }
//    }
//}


@Scene2dDsl
class ChatWidget(
    messages: List<Pair<String, String>>,
    callback: (String) -> Unit,
    skin: Skin
) : Table(skin), KTable {
    private val showToggle: TextButton
    private var toggle = true
    private val scrollPaneWidget: ScrollPaneWidget<MessageWidget>
    private val txtField: TextField
    private val sendBtn: TextButton

    init {
        //setFillParent(true) ONLY Parent fills!
        align(Align.bottomLeft)

        scrollPaneWidget = scene2d.scrollWidget(skin) {

        }

        showToggle = scene2d.textButton("Toggle") {
            onChange {
                this@ChatWidget.toggle = !this@ChatWidget.toggle
                this@ChatWidget.scrollPaneWidget.isVisible = this@ChatWidget.toggle
            }
        }

        txtField = scene2d.textField {
            onKeyUp {
                if (it == Input.Keys.ENTER && text.isNotBlank()) {
                    println(text)
                    callback(text)
                    text = "" // reset
                }
            }
        }

        sendBtn = scene2d.textButton("Send") {
            onClick {
                println(this@ChatWidget.txtField.text)
                callback(this@ChatWidget.txtField.text)
                this@ChatWidget.txtField.text = ""
            }
        }

        // Fill table

//        add(showToggle)
//        row()

        add(scrollPaneWidget)
            .growX()
            .colspan(2) // Make room for 2 cells underneath
            //.minHeight(150f)
            .maxHeight(150f)

        row().padTop(5f)
        add(txtField).growX().fillY().spaceRight(10f)
        add(sendBtn).minWidth(50f)

        addAll(messages)

    }

    fun addMessage(from: String, message: String) {
        val element = scene2d.messageWidget(from, message, skin)
        scrollPaneWidget.add(element)
    }

    fun addAll(messages: List<Pair<String, String>>) {
        messages.forEach {
            addMessage(it.first, it.second)
        }
    }
}
//
//
//@Scene2dDsl
//class ChatWidget(
//    messages: List<Pair<String, String>>?, // TODO: Use! Some day.
//    callback: (String) -> Unit,
//    skin: Skin
//) : Table(skin), KTable {
//
//    private val messageList: VerticalGroup
//    private val textField: TextField
//
//    init {
//        // setFillParent(true)
//
//
//        scroll = scrollPane {
//            this@ChatWidget.messageList = verticalGroup { }
//            actor = this@ChatWidget.messageList
//        }
//
//        textField = textField {
//            onKeyUp {
//                if (it == Input.Keys.ENTER) {
//                    println(text)
//                    callback(text)
//                    text = "" // reset
//                }
//            }
//        }
//
//        messages?.let { update(it) }
//    }
//
////    fun addMessage(from: String, message: String) {
////        // TODO: Capacity!
////
////        val messages = messageList.items
////        messages.add(MessageWidget(from, message, skin))
////        messageList.setItems(messages)
////    }
//
//    /**
//     * Testing version. Update whole message list at once.
//     */
//    fun update(messages: kotlin.collections.List<Pair<String, String>>) {
//
//        messageList.clear()
//
//        // val elements = groupList.items
//        messages.forEach { message ->
//            val name = "name"
//            val element = MessageWidget(
//                from = message.first,
//                message = message.second,
//                skin,
//            )
//            messageList.addActor(element)
//        }
//    }
//}

/**
 * Represents a single chat message.
 */
@Scene2dDsl
class MessageWidget(
    from: String,
    message: String,
    skin: Skin
) : Table(skin), KTable {
    val messageLabel: Label

    init {
        align(Align.left)
        // TODO: Use smaller font
        messageLabel = scene2d.label("$from: $message") {
            wrap = true

        }

        add(messageLabel).growX().pad(3f)
    }
}

@Scene2dDsl
fun <S> KWidget<S>.messageWidget(
    from: String,
    message: String,
    skin: Skin,
    init: MessageWidget.(S) -> Unit = {}
): MessageWidget = actor(MessageWidget(from, message, skin), init)


//@Scene2dDsl
//fun <S> KWidget<S>.chat2(
//    messages: List<Pair<String, String>> = mutableListOf(),
//    skin: Skin,
//    callback: (String) -> Unit,
//    init: ChatWidget.(S) -> Unit = {}
//): ChatWidget = actor(ChatWidget(messages, callback, skin), init)
//

@Scene2dDsl
fun <S> KWidget<S>.chat(
    messages: List<Pair<String, String>> = mutableListOf(),
    skin: Skin,
    callback: (String) -> Unit,
    init: ChatWidget.(S) -> Unit = {}
): ChatWidget = actor(ChatWidget(messages, callback, skin), init)



