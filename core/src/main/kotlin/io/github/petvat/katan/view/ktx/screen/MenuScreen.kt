package io.github.petvat.katan.view.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.petvat.katan.view.ktx.KtxKatan
import io.github.petvat.katan.view.ktx.ui.MenuView
import ktx.scene2d.*

class MenuScreen(game: KtxKatan) : AbstractScreen(game) {

//    init {
//        loadSkin()
//        Gdx.app.log("MenuScreen", "Skin loaded successfully")
//    }

    override fun buildStage() {
        stage.actors {
            stage.addActor(MenuView(game, Scene2DSkin.defaultSkin))
        }
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
