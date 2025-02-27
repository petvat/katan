package io.github.petvat.katan.lwjgl3

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxScreen
import ktx.app.clearScreen

abstract class AbstractTestScreen : KtxScreen {
    private val vp = ScreenViewport()
    private val batch by lazy { SpriteBatch() }
    val stage = Stage(vp, batch)

    init {
        vp.camera.position.y = 10f
    }

    /**
     * Test.
     */
    abstract fun setup()

    override fun show() {

        val multiplexer = InputMultiplexer();
        multiplexer.addProcessor(stage)
        Gdx.input.inputProcessor = multiplexer;

        stage.isDebugAll = true

        setup() // Call implementation specific logic.

    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }
}


