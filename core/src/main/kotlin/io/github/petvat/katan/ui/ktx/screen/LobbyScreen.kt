package io.github.petvat.katan.ui.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import io.github.petvat.katan.event.InEventBus
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.LobbyView
import io.github.petvat.katan.ui.ktx.widget.error
import io.github.petvat.katan.ui.model.GameViewModel
import io.github.petvat.katan.ui.model.LobbyViewModel
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors

class LobbyScreen(game: KtxKatan) : AbstractScreen(game) {

    private lateinit var baseView: LobbyView // Game must be init first!

//    fun updateGroups(groups: List<Pair<String, String>>) {
//        groups.forEach { (id, mode) ->
//            baseView.addGroup("Placeholder: $mode", id)
//        }
//    }


    override fun buildStage() {
        logger.debug { "Building lobby" }
        baseView = LobbyView(
            LobbyViewModel(game.controller, game.transitionService, game.model.groups),
            Scene2DSkin.defaultSkin
        )
        stage.actors {
            stage.addActor(baseView)
            stage.addActor(error(Scene2DSkin.defaultSkin))
        }

        // TODO: Event add stage to event listener somehow
        InEventBus += baseView
//        InEventBus +=
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act()
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }
}
