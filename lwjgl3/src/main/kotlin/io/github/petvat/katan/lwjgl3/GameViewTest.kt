package io.github.petvat.katan.lwjgl3

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.lwjgl3.tests.AbstractTestScreen
import io.github.petvat.katan.lwjgl3.tests.MockChatActions
import io.github.petvat.katan.lwjgl3.tests.MockGameActions
import io.github.petvat.katan.lwjgl3.tests.gdxTest
import io.github.petvat.katan.model.GameState
import io.github.petvat.katan.shared.hexlib.Layout
import io.github.petvat.katan.shared.hexlib.PCoordinate
import io.github.petvat.katan.shared.model.board.BoardGenerator
import io.github.petvat.katan.shared.model.game.*
import io.github.petvat.katan.ui.Assets
import io.github.petvat.katan.ui.control.DragCameraController
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.screen.MainGameScreen.Companion.TEX_HEIGHT
import io.github.petvat.katan.ui.ktx.screen.MainGameScreen.Companion.TEX_WIDTH
import io.github.petvat.katan.ui.ktx.screen.loadUISkin
import io.github.petvat.katan.ui.ktx.view.BoardView
import io.github.petvat.katan.ui.ktx.view.GameView
import io.github.petvat.katan.ui.projection.GameProjector
import io.github.petvat.katan.ui.viewmodel.GameVM
import ktx.app.KtxGame
import ktx.scene2d.Scene2DSkin
import kotlin.math.sqrt

fun main() = gdxTest("UI Game test", GameViewTest())


private class GameViewTest : KtxGame<GameTest>() {
    override fun create() {
        loadUISkin()
        addScreen(GameTest())
        setScreen<GameTest>()
    }
}


private class GameTest() : AbstractTestScreen() {

    val board = BoardGenerator.generateBoard(Settings())


    val viewport = ExtendViewport(KtxKatan.VW, KtxKatan.VH)
    val camera = viewport.camera as OrthographicCamera
    val assets = Assets()
    val batch = SpriteBatch()

    val game = GameState(
        id = "game",
        player = 1,
        otherPlayers = listOf(2, 3, 4),
        board = board,
        colors = mapOf(
            1 to PlayerColor.RED,
            2 to PlayerColor.BLUE,
            3 to PlayerColor.WHITE,
            4 to PlayerColor.ORANGE
        ),
        resources = ResourceMapData(1, 1, 1, 1, 1),
        otherResources = mapOf(2 to 2, 3 to 3, 4 to 4),
        phase = Phase.SETUP,
        turnOrder = listOf(1, 2, 3, 4),
        turnPlayer = 1,
        victoryPoints = mapOf(1 to 0, 2 to 0, 3 to 0, 4 to 0)
    )

    val movementController = DragCameraController(
        camera = camera,
        viewport = viewport,
        onTap = { screenX, screenY ->
            boardRenderer.handleTouch(screenX, screenY, camera)

        })


    init {

        board.tiles.forEach { println(it.hexCoordinate) }

        camera.position.set(0f, 0f, 0f)
        camera.update()
    }

    /**
     * Hex layout for gameState window
     */
    val layout = Layout(
        PCoordinate(TEX_WIDTH / sqrt(3.0), (TEX_HEIGHT / 2) + 2), // Inradius width and height
        PCoordinate(0.0, 0.0) // Origin hex relative to viewport
    )

    val viewModel = GameVM(game, MockGameActions(), MockChatActions())

    val eventSystem = EventSystem()


    val boardRenderer = BoardView(
        viewModel,
        viewModel.projection,
        batch,
        assets,
        layout
    )

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)          // updates stage viewport
        viewport.update(width, height, true) // updates board's ExtendViewport
        camera.update()
    }

    override fun setup() {

        val multiplexer = InputMultiplexer(stage, movementController)
        Gdx.input.inputProcessor = multiplexer
        stage.addActor(
            GameView(viewModel.projection, viewModel, Scene2DSkin.defaultSkin)
        )
        super.clearScreen = false
        eventSystem += viewModel

        viewModel.initSettlementPlacingMode = true //
    }


    override fun render(delta: Float) {
        camera.update()

        Gdx.gl.glClearColor(0f, 0.2f, 0.3f, 0.8f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        batch.projectionMatrix = camera.combined

        boardRenderer.render()

        stage.viewport.apply() // Apply the stage viewport to render the UI correctly.
        stage.act()
        stage.draw()

//        clearScreen(0f, 0f, 0f, 1f)
//        boardRenderer.render()
//        super.render(delta)
    }
}

