package io.github.petvat.katan.ui.ktx.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.ui.ktx.widget.createWidget
import io.github.petvat.katan.ui.ktx.widget.groupsWidget
import io.github.petvat.katan.ui.model.LobbyViewModel
import io.github.petvat.katan.ui.model.View
import ktx.actors.onChange
import ktx.scene2d.KTable
import ktx.scene2d.textButton

class LobbyView(
    viewModel: LobbyViewModel,
    skin: Skin
) : KTable, View<LobbyViewModel>(skin, viewModel) {

    private val groupWidget = groupsWidget(viewModel, skin)

    init {
        setFillParent(true)
        //background("area")
        align(Align.center)
        debug = true

        groupWidget
        createWidget(viewModel, skin)

        row()
            //.growX()
            .colspan(2)
        textButton("Back").onChange { print("Back - not impl.") }
    }

    fun addGroup(displayText: String, id: String) {
        groupWidget.addGroup(displayText, id = id)
    }

    override fun onEvent(event: Event) {
        TODO("Not yet implemented")
    }

    override fun registerOnPropertyChanges() {
        TODO("Not yet implemented")
    }
}
