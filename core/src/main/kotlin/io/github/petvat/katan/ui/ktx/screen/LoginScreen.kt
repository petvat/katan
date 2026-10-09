package io.github.petvat.katan.ui.ktx.screen

import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.loginView
import io.github.petvat.katan.ui.viewmodel.LoginViewModel
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors

class LoginScreen(game: KtxKatan, bus: EventSystem) : AbstractScreen(game, bus) {

    override val viewModel = LoginViewModel(game.model.commands.lobby, game.transitionService)

    override fun buildStage() {
        stage.actors {
            loginView(viewModel, Scene2DSkin.defaultSkin)
        }
    }


}
