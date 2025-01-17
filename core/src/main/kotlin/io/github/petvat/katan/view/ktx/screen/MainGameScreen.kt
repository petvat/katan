package io.github.petvat.katan.view.ktx.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ExtendViewport
import io.github.petvat.katan.shared.hexlib.PCoordinate
import io.github.petvat.katan.shared.hexlib.Layout
import io.github.petvat.katan.shared.model.board.Tile
import io.github.petvat.katan.view.ktx.BoardRenderManager
import io.github.petvat.katan.view.ktx.KtxKatan
import kotlin.math.sqrt

class MainGameScreen(game: KtxKatan) : AbstractScreen(game) {

    companion object {
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

    private lateinit var tiles: MutableList<Tile>

    private lateinit var boardRenderer: BoardRenderManager

    override fun show() {
        layout = Layout(
            PCoordinate(TEX_WIDTH / sqrt(3.0), (TEX_HEIGHT / 2) + 2), // Inradius width and height
            PCoordinate(0.0, 0.0) // Origin hex relative to viewport
        )
        // TODO:
        boardRenderer = BoardRenderManager(
            game.model.gameState!!.board,
            game.batch,
            game.assets,
            layout
        )
        tiles = game.model.gameState!!.board.tiles.toMutableList()
    }

    override fun buildStage() {
        TODO("Not yet implemented")
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
    }

    fun drawRollDice() {

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
    }
}
