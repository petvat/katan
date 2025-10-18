package io.github.petvat.katan.ui.ktx.screen

import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.GroupView
import io.github.petvat.katan.ui.model.GroupViewModel
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors


class GroupScreen(game: KtxKatan) : AbstractScreen(game) {

    override lateinit var viewModel: GroupViewModel

    override fun buildStage() {

        viewModel = GroupViewModel(game.model.groupModel, game.controller, game.transitionService)
        stage.actors {
            stage.addActor(
                GroupView(
                    viewModel,
                    Scene2DSkin.defaultSkin
                )
            )
        }
    }
}

