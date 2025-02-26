package io.github.petvat.core.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.ui.ktx.widget.chat
import io.github.petvat.katan.ui.model.GroupViewModel
import ktx.actors.onChange
import ktx.scene2d.KTable
import ktx.scene2d.textButton

class GroupView(
    viewModel: GroupViewModel,
    skin: Skin
) : View<GroupViewModel>(skin, viewModel), KTable {

    // TODO: Fill with view models chat log copy.
    private val chatWidget = chat(
        skin = skin,
        callback = { message: String -> viewModel.handleChat(message) })

    init {
        setFillParent(true)
        //background("area")
        align(Align.center)
        debug = true

        chatWidget
        row()
        textButton("Start game").onChange {
            this@GroupView.viewModel.handleInit()
        }
    }

    override fun registerOnPropertyChanges() {
        viewModel.onPropertyChange(GroupViewModel::lastGroupMessage) {
            // chatWidget.addMessage(it.first, it.second)

        }
    }


}
