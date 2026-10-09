package io.github.petvat.katan.ui.ktx.screen

import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.GroupView
import io.github.petvat.katan.ui.viewmodel.GroupViewModel
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors


class GroupScreen(game: KtxKatan, bus: EventSystem) : AbstractScreen(game, bus) {

    override lateinit var viewModel: GroupViewModel

    override fun buildStage() {

        viewModel = GroupViewModel(ktxCtx.model.state, ktxCtx.model.commands.group, ktxCtx.transitionService)
        stage.actors {
            stage.addActor( // NOTE: ?
                GroupView(
                    viewModel,
                    Scene2DSkin.defaultSkin
                )
            )
        }
    }
}

