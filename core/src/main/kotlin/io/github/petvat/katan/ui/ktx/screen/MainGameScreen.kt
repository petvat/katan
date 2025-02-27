package io.github.petvat.katan.ui.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ExtendViewport
import io.github.petvat.katan.event.EventBus
import io.github.petvat.katan.shared.hexlib.PCoordinate
import io.github.petvat.katan.shared.hexlib.Layout
import io.github.petvat.katan.ui.ktx.view.BoardGraphic
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.view.gameView
import io.github.petvat.katan.ui.ktx.view.tradeView
import io.github.petvat.katan.ui.model.GameViewModel
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors
import kotlin.math.sqrt

class MainGameScreen(game: KtxKatan) : AbstractScreen(game) {

    companion object {
        /**
         * Hexagon height and width.
         */
        const val TEX_HEIGHT = 86.0
        const val TEX_WIDTH = 110.0
    }

    private val viewport = ExtendViewport(KtxKatan.VW, KtxKatan.VH)
    private val camera = viewport.camera as OrthographicCamera
    private val assets = game.assets
    private val batch = game.batch

    init {
        camera.position.set(0f, 0f, 0f)
        camera.update()
    }

    /**
     * Hex layout for gameState window
     */
    private lateinit var layout: Layout

    // private lateinit var tiles: MutableList<Tile>

    private lateinit var boardRenderer: BoardGraphic

    override lateinit var viewModel: GameViewModel

    override fun show() {
        super.show()
    }

    override fun buildStage() {

        layout = Layout(
            PCoordinate(TEX_WIDTH / sqrt(3.0), (TEX_HEIGHT / 2) + 2), // Inradius width and height
            PCoordinate(0.0, 0.0) // Origin hex relative to viewport
        )

        viewModel = GameViewModel(game.controller, game.model.group, game.model.game)

        boardRenderer = BoardGraphic(
            viewModel,
            batch,
            assets,
            layout
        )
        // TODO: Might be needed somewhere: tiles = game.model.gameManager!!.board.tiles.toMutableList() Break reason?

        stage.actors {
            tradeView(viewModel, Scene2DSkin.defaultSkin) { isVisible = false } // Overlay of trade system
            gameView(viewModel, Scene2DSkin.defaultSkin)
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun render(delta: Float) {
        handleInput()
        camera.update()

        Gdx.gl.glClearColor(0f, 0.2f, 0.3f, 0.8f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        batch.projectionMatrix = camera.combined

        boardRenderer.render()

        stage.act()
        stage.draw()
    }

    private fun handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (camera.zoom > 0.02f) { // Prevent flip transformation
                camera.zoom -= 0.02f
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.zoom += 0.02f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3f, 0f, 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3f, 0f, 0f);
        }

        // TODO BIG: Handle click input!

        if (Gdx.input.isTouched) {
            boardRenderer.handleTouch(Gdx.input.x, Gdx.input.y)
        }
    }
}
