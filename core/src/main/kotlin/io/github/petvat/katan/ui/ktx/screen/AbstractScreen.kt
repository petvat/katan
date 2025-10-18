package io.github.petvat.katan.ui.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.event.*
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.widget.error
import io.github.petvat.katan.ui.model.ViewModel
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d

abstract class AbstractScreen(val game: KtxKatan) : KtxScreen, EventListener {
    private val logger = KotlinLogging.logger { }
    private val vp = ScreenViewport()
    private val scaleFactor = 3
    protected val stage: Stage
    abstract val viewModel: ViewModel
    protected lateinit var inputMultiplexer: InputMultiplexer

    init {
        vp.unitsPerPixel = 1f / scaleFactor
        stage = Stage(vp, game.batch)
        // stage.isDebugAll = true
    }
    
    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    /**
     */
    override fun show() {
        inputMultiplexer = InputMultiplexer()
        inputMultiplexer.addProcessor(stage)
        Gdx.input.inputProcessor = inputMultiplexer

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
        stage.addActor(scene2d.error(message, Scene2DSkin.defaultSkin))
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
