package io.github.petvat.katan.ui.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.model.BoardHelpers
import io.github.petvat.katan.shared.hexlib.PCoord
import io.github.petvat.katan.shared.hexlib.Layout
import io.github.petvat.katan.ui.ktx.view.BoardView
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.gameView
import io.github.petvat.katan.ui.ktx.view.tradeView
import io.github.petvat.katan.ui.projection.BoardProjector
import io.github.petvat.katan.ui.viewmodel.GameScreenViewModel
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors
import kotlin.math.abs
import kotlin.math.sqrt

class MainGameScreen(game: KtxKatan, bus: EventSystem) : AbstractScreen(game, bus) {

    companion object {
        /**
         * Hexagon height and width.
         */
        const val TEX_HEIGHT = 86.0
        const val TEX_WIDTH = 110.0
    }

    private val viewport = ScreenViewport()
    private val camera = viewport.camera as OrthographicCamera
    private val assets = game.assets
    private val batch = game.batch
    var scaleFactors = listOf(1f, 1 / 2f, 1 / 3f, 1 / 4f)
    var scaleFactorIdx = 1

    init {
        //camera.position.set(0f, 0f, 0f)
        //camera.update()
        viewport.unitsPerPixel = scaleFactors[2]
    }

    /**
     * Hex layout for gameState window
     */
    private lateinit var layout: Layout

    // private lateinit var tiles: MutableList<Tile>

    private lateinit var boardRenderer: BoardView

    override lateinit var viewModel: GameScreenViewModel


    // TODO: USING THIS?
    var lastTouchX = 0f
    var lastTouchY = 0f
    val dragSensitivity = 0.5f
    val minX = -230f
    val minY = minX
    val maxX = abs(minX)
    val maxY = abs(minY)

    private val inputProcessor = object : InputAdapter() {
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            lastTouchX = screenX.toFloat()
            lastTouchY = screenY.toFloat()
            return true
        }

        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
            val deltaX = lastTouchX - screenX
            val deltaY = lastTouchY - screenY

            camera.translate(
                deltaX * camera.zoom * dragSensitivity,
                -deltaY * camera.zoom * dragSensitivity
            ) // Move the camera

            camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX)
            camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY)
            camera.update()

            lastTouchX = screenX.toFloat()
            lastTouchY = screenY.toFloat()

            return true
        }
    }


    override fun buildStage() {

        val game = requireNotNull(ktxCtx.model.state.game) {
            "MainGameScreen built before initialization."
        }

        layout = Layout(
            PCoord(TEX_WIDTH / sqrt(3.0), (TEX_HEIGHT / 2) + 2), // Inradius width and height
            PCoord(0.0, 0.0) // Origin hex relative to viewport
        )

        viewModel = GameScreenViewModel(ktxCtx.model.state, ktxCtx.model.commands, layout)

        boardRenderer = BoardView(
            viewModel.board,
            assets
        )

        inputMultiplexer.addProcessor(inputProcessor)

        stage.actors {
            tradeView(viewModel.game, Scene2DSkin.defaultSkin) { isVisible = false } // Overlay of trade system
            gameView(vm = viewModel.game, hud = viewModel.game.hud, Scene2DSkin.defaultSkin)
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        super.resize(width, height)
    }

    override fun render(delta: Float) {
        handleInput()

        Gdx.gl.glClearColor(0f, 0.2f, 0.3f, 0.8f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        batch.projectionMatrix = camera.combined
        camera.update()

        boardRenderer.render(batch)

        stage.viewport.apply() // Apply the stage viewport to render the UI correctly.

        stage.act()
        stage.draw()
    }

    // TODO: Why is this here? Move it to BoardView!
    private fun handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (camera.zoom > 0.02f) { // Prevent flip transformation
                // viewport.unitsPerPixel = scaleFactors[++scaleFactorIdx % (scaleFactors.size - 1)]
                viewport.unitsPerPixel *= 1.02f
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            viewport.unitsPerPixel *= 0.98f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3f, 0f, 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3f, 0f, 0f);
        }

        // TODO BIG: Handle click input!
//
//        if (Gdx.input.isTouched) {
//            boardRenderer.handleTouch(Gdx.input.x, Gdx.input.y)
//        }

    }
}



