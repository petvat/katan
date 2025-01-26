package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.event.ChatEvent
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.EventListener
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.widget.chat
import io.github.petvat.katan.ui.model.GroupViewModel
import io.github.petvat.katan.ui.model.View
import ktx.actors.onChange
import ktx.scene2d.KTable
import ktx.scene2d.textButton

class GroupView(
    viewModel: GroupViewModel,
    skin: Skin
) : View<GroupViewModel>(skin, viewModel), KTable {

    // TODO: Fill with view models chat log copy.
    private val chatWidget = chat(skin = skin)

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

    override fun onEvent(event: Event) {
        if (event is ChatEvent) {
            chatWidget.update(
                this@GroupView.viewModel.getChatView()
            )
        }
    }

    override fun registerOnPropertyChanges() {
        TODO("Not yet implemented")
    }


}
