package io.github.petvat.katan.ui.ktx.screen

import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.LobbyView
import io.github.petvat.katan.ui.viewmodel.LobbyViewModel
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors

class LobbyScreen(game: KtxKatan, bus: EventSystem) : AbstractScreen(game, bus) {

    override lateinit var viewModel: LobbyViewModel

    override fun buildStage() {
        viewModel =
            LobbyViewModel(ktxCtx.lobbyService, ktxCtx.transitionService, ktxCtx.model.groupSummaries.toMutableList())
        logger.debug { "Building lobby" }
        stage.actors {
            stage.addActor(LobbyView(viewModel, Scene2DSkin.defaultSkin))
            // stage.addActor(error(Scene2DSkin.defaultSkin) { isVisible = false })
        }
    }
}
