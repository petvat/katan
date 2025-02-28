package io.github.petvat.katan.lwjgl3

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.petvat.katan.event.EventBus
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.model.ViewModel
import ktx.app.KtxScreen
import ktx.app.clearScreen

abstract class AbstractTestScreen : KtxScreen {
    //    private val viewport = ExtendViewport(KtxKatan.VW, KtxKatan.VH)
//    private val camera = viewport.camera as OrthographicCamera
//    private val batch by lazy { SpriteBatch() }
//    val stage: Stage
    private val viewport = ScreenViewport()
    private val batch by lazy { SpriteBatch() }
    val stage: Stage
    private val scaleFactor = 2

    protected var clearScreen = true

    init {
        viewport.unitsPerPixel = (1f / scaleFactor)
        stage = Stage(viewport, batch)
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
        // camera.update()

        if (clearScreen) clearScreen(0f, 0f, 0f)

        viewport.apply()
        // batch.projectionMatrix = camera.combined

        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }
}


