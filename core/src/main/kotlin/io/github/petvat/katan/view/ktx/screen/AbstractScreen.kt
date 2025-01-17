package io.github.petvat.katan.view.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.view.KatanView
import io.github.petvat.katan.view.ktx.KtxKatan
import io.github.petvat.katan.view.ktx.ui.LobbyView
import ktx.app.KtxScreen
import ktx.log.logger
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors

abstract class AbstractScreen(val game: KtxKatan) : KtxScreen {
    val vp = ScreenViewport()
    protected val stage = Stage(vp, game.batch)


    init {
        vp.camera.position.y = 10f
        stage.isDebugAll
    }

    val logger = KotlinLogging.logger { }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun show() {
        val multiplexer = InputMultiplexer();
        multiplexer.addProcessor(stage)
        Gdx.input.inputProcessor = multiplexer;

        logger.debug { "Building stage." }
        stage.clear()
        buildStage()
    }

    override fun hide() {
        logger.debug { "Hid something" }
        Gdx.input.inputProcessor = null // ???
        stage.clear()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act()
        stage.draw()
    }

    protected abstract fun buildStage()
}
