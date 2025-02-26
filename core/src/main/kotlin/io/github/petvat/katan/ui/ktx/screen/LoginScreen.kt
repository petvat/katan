package io.github.petvat.core.ui.ktx.screen

import io.github.petvat.katan.event.EventBus
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.loginView
import io.github.petvat.katan.ui.ktx.view.startView
import io.github.petvat.katan.ui.model.LoginViewModel
import io.github.petvat.katan.ui.model.StartMenuViewModel
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors

class LoginScreen(game: KtxKatan) : AbstractScreen(game) {

    override val viewModel = LoginViewModel(game.controller, game.transitionService)

    override fun show() {
        super.show()
        EventBus += this
        EventBus += viewModel
    }

    override fun buildStage() {
        stage.actors {
            loginView(viewModel, Scene2DSkin.defaultSkin)
        }
    }


}
