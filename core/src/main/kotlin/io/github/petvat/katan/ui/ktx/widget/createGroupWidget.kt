package io.github.petvat.katan.ui.ktx.widget

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.model.LobbyViewModel
import ktx.actors.onChange
import ktx.scene2d.*


class CreateGroupWidget(
    viewModel: LobbyViewModel,
    private val skin: Skin
) : Table(skin), KTable {

    init {
        label("Create group:")
        row()
        label("Settings placeholder")
        row().growY()
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



