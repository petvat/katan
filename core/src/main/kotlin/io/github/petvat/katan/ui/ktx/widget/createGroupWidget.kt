package io.github.petvat.core.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.ui.model.LobbyViewModel
import ktx.actors.onChange
import ktx.scene2d.*

class CreateGroupWidget(
    viewModel: LobbyViewModel,
    skin: Skin
) : Table(skin), KTable {

    init {
        label("Create group")
        row()
            .growY()
        label("Settings placeholder").onChange {
            println("TODO: Settings not implemented!")
        }
        row()
            .growY()
        textButton("Create").onChange {
            viewModel.handleCreate()
        }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.createWidget(
    viewModel: LobbyViewModel,
    skin: Skin,
    init: CreateGroupWidget.(S) -> Unit = {}
): CreateGroupWidget = actor(CreateGroupWidget(viewModel, skin), init)



