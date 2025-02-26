package io.github.petvat.katan.ui.ktx.screen

import io.github.petvat.katan.event.EventBus
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.startView
import io.github.petvat.katan.ui.model.StartMenuViewModel
import ktx.scene2d.*

class MenuScreen(game: KtxKatan) : AbstractScreen(game) {

    override val viewModel = StartMenuViewModel(game.controller, game.transitionService)


    override fun show() {
        super.show()
        EventBus += this
        EventBus += viewModel
    }

    override fun buildStage() {
        stage.actors {
            startView(viewModel, Scene2DSkin.defaultSkin)
        }
    }
}
