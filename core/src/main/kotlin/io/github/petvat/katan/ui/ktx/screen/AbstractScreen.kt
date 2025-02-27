package io.github.petvat.katan.ui.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.event.*
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.widget.createErrorWindow
import io.github.petvat.katan.ui.model.ViewModel
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin

abstract class AbstractScreen(val game: KtxKatan) : KtxScreen, EventListener {
    private val vp = ScreenViewport()
    protected val stage = Stage(vp, game.batch)

    abstract val viewModel: ViewModel


    init {
        vp.camera.position.y = 10f
        stage.isDebugAll = true
    }

    val logger = KotlinLogging.logger { }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    /**
     * TODO: Might be possible to do EventBus += this, have not checked.
     */
    override fun show() {
        val multiplexer = InputMultiplexer();
        multiplexer.addProcessor(stage)
        Gdx.input.inputProcessor = multiplexer;

        logger.debug { "Building stage." }
        stage.clear()
        buildStage()
        EventBus += this
        EventBus += viewModel
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

    private fun showError(message: String) {
        stage.addActor(createErrorWindow(Scene2DSkin.defaultSkin, message))
    }

    protected abstract fun buildStage()

//    fun buildS(vararg views: View<*>) {
//        views.forEach { view -> stage.addActor(view(Scene2DSkin.defaultSkin, viewModel) }
//    }

    override fun onEvent(event: Event) {
        when (event) {
            is ErrorEvent -> {
                showError(event.reason)
            }

            else -> Unit
        }
    }

    override fun dispose() {
        EventBus -= viewModel
        EventBus -= this
        stage.dispose()
    }
}
