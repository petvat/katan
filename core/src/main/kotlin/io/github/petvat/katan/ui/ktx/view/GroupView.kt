package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.ktx.widget.ChatWidget
import io.github.petvat.katan.ui.ktx.widget.chat
import io.github.petvat.katan.ui.viewmodel.GroupViewModel
import ktx.actors.onChange
import ktx.scene2d.KTable
import ktx.scene2d.scene2d
import ktx.scene2d.textButton

class GroupView(
    val viewModel: GroupViewModel,
    skin: Skin
) : KtxView(skin), KTable {

    // TODO: Fill with view models chat log copy.
    private val chatWidget: ChatWidget
    private val startBtn: TextButton

    var renderedCount = 0

    init {
        setFillParent(true)
        //background("area")
        align(Align.center)
        debug = true

        chatWidget = scene2d.chat(
            skin = skin,
            callback = viewModel::handleChat
        ) { }
        startBtn = scene2d.textButton("Start game") {
            onChange { this@GroupView.viewModel.handleInit() }
        }

        add(chatWidget).growX()
        row()
        add(startBtn)

        registerOnPropertyChanges()
    }

    override fun registerOnPropertyChanges() {
        viewModel.onPropertyChange(GroupViewModel::chat) { c ->
            val fresh = c.chatlog.drop(renderedCount)
            fresh.forEach { chatWidget.addMessage(it.first, it.second) }
            renderedCount = c.chatlog.size
        }
    }
}
