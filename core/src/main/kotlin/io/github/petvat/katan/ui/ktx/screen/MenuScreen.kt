package io.github.petvat.katan.ui.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import io.github.petvat.katan.event.EventListener
import io.github.petvat.katan.event.InEventBus
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.startView
import io.github.petvat.katan.ui.model.StartMenuViewModel
import ktx.scene2d.*

class MenuScreen(game: KtxKatan) : AbstractScreen(game) {

//    init {
//        loadSkin()
//        Gdx.app.log("MenuScreen", "Skin loaded successfully")
//    }

    override fun buildStage() {
        stage.actors {
            startView(StartMenuViewModel(game.controller, game.transitionService), Scene2DSkin.defaultSkin)
        }
        InEventBus += stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act()
        stage.draw()
    }

    override fun dispose() {
        logger.debug { "Disposed Menu" }
        stage.dispose()
        disposeSkin()
    }
}
