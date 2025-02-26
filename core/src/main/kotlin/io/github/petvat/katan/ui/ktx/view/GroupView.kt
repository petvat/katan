package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.ktx.widget.ChatWidget
import io.github.petvat.katan.ui.ktx.widget.chat
import io.github.petvat.katan.ui.model.GroupViewModel
import ktx.actors.onChange
import ktx.scene2d.KTable
import ktx.scene2d.scene2d
import ktx.scene2d.textButton

class GroupView(
    viewModel: GroupViewModel,
    skin: Skin
) : View<GroupViewModel>(skin, viewModel), KTable {

    // TODO: Fill with view models chat log copy.
    private val chatWidget: ChatWidget
    private val startBtn: TextButton

    init {
        setFillParent(true)
        //background("area")
        align(Align.center)
        debug = true

        chatWidget = scene2d.chat(
            skin = skin,
            callback = viewModel::handleChat
        )
        startBtn = scene2d.textButton("Start game") {
            onChange { this@GroupView.viewModel.handleInit() }
        }

        add(chatWidget).growX()
        row()
        add(startBtn)

        registerOnPropertyChanges()
    }

    override fun registerOnPropertyChanges() {

        // More sophisticated
        viewModel.onPropertyChange(GroupViewModel::lastGroupMessage) {
            println("reached binder last message")
            chatWidget.addMessage(it.first, it.second)

        }

        // Dump
        viewModel.onPropertyChange(GroupViewModel::chatLogProperty) {
            // chatWidget.addMessage(it.first, it.second)
            println("reach")
            chatWidget.addAll(it)
        }
    }


}
