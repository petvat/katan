package io.github.petvat.katan.ui.ktx.screen

import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.startView
import io.github.petvat.katan.ui.viewmodel.StartMenuViewModel
import ktx.scene2d.*

class MenuScreen(game: KtxKatan, bus: EventSystem) : AbstractScreen(game, bus) {

    override val viewModel = StartMenuViewModel(game.networkSession, game.transitionService, bus)

    override fun buildStage() {
        stage.actors {
            startView(viewModel, Scene2DSkin.defaultSkin)
        }
    }
}
