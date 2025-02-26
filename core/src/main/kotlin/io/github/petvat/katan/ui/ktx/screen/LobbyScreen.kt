package io.github.petvat.core.ui.ktx.screen

import io.github.petvat.katan.event.EventBus
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.LobbyView
import io.github.petvat.katan.ui.ktx.widget.error
import io.github.petvat.katan.ui.model.LobbyViewModel
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors

class LobbyScreen(game: KtxKatan) : AbstractScreen(game) {

    override lateinit var viewModel: LobbyViewModel

    override fun show() {
        super.show()
        EventBus += viewModel
        EventBus += this
    }

    override fun buildStage() {
        viewModel = LobbyViewModel(game.controller, game.transitionService, game.model.groups)
        logger.debug { "Building lobby" }
        stage.actors {
            stage.addActor(LobbyView(viewModel, Scene2DSkin.defaultSkin))
            stage.addActor(error(Scene2DSkin.defaultSkin) { isVisible = false })
        }
    }
}
