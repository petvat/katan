package io.github.petvat.katan.lwjgl3

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import io.github.petvat.katan.event.EventBus
import io.github.petvat.katan.shared.hexlib.Layout
import io.github.petvat.katan.shared.hexlib.PCoordinate
import io.github.petvat.katan.shared.model.board.BoardGenerator
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.PermissionLevel
import io.github.petvat.katan.shared.model.game.PlayerColor
import io.github.petvat.katan.shared.protocol.dto.*
import io.github.petvat.katan.ui.Assets
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.screen.MainGameScreen.Companion.TEX_HEIGHT
import io.github.petvat.katan.ui.ktx.screen.MainGameScreen.Companion.TEX_WIDTH
import io.github.petvat.katan.ui.ktx.screen.loadVisUISkin
import io.github.petvat.katan.ui.ktx.view.BoardGraphic
import io.github.petvat.katan.ui.ktx.view.GameView
import io.github.petvat.katan.ui.model.GameViewModel
import ktx.app.KtxGame
import ktx.app.clearScreen
import ktx.scene2d.Scene2DSkin
import kotlin.math.sqrt

fun main() = gdxTest("UI Lobby test", GameViewTest())


private class GameViewTest : KtxGame<GameTest>() {
    override fun create() {
        loadVisUISkin()
        addScreen(GameTest())
        setScreen<GameTest>()
    }
}


private class GameTest() : AbstractTestScreen() {

    val board = BoardGenerator.generateBoard(Settings()).fromDomain()

    val viewport = ExtendViewport(KtxKatan.VW, KtxKatan.VH)
    val camera = viewport.camera as OrthographicCamera
    val assets = Assets()
    val batch = SpriteBatch()

    val game = GameStateDTO(
        player = PlayerDTO(
            1, PlayerColor.RED, 0, ResourceMap(0, 0, 0, 0, 0), 0,
            0,
            0,
        ),
        otherPlayers = listOf(
            PlayerDTO(
                1, PlayerColor.RED, 0, ResourceMap(0, 0, 0, 0, 0), 0,
                0,
                0,
            ),
            PlayerDTO(
                2, PlayerColor.RED, 0, ResourceMap(0, 0, 0, 0, 0), 0,
                0,
                0,
            ),
            PlayerDTO(
                3, PlayerColor.RED, 0, ResourceMap(0, 0, 0, 0, 0), 0,
                0,
                0,
            ),
        ),
        turnOrder = listOf(1, 2),
        board = board,
        turnPlayer = 1
    )

    val group = PrivateGroupDTO(
        id = "1",
        clients = mutableMapOf("id" to "player"),
        level = PermissionLevel.USER,
        chatLog = mutableListOf("player" to "Hello"),
        settings = Settings()
    )


    init {
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

    val viewModel = GameViewModel(MockController(), group, game)


    // private lateinit var tiles: MutableList<Tile>

    val boardRenderer = BoardGraphic(
        viewModel,
        batch,
        assets,
        layout
    )


    override fun setup() {
        stage.addActor(
            GameView(viewModel, Scene2DSkin.defaultSkin)
        )
    }

    override fun render(delta: Float) {
        clearScreen(0f, 0f, 0f, 1f)
        boardRenderer.render()
        super.render(delta)
    }
}

