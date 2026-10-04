package io.github.petvat.katan.lwjgl3

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.lwjgl3.tests.AbstractTestScreen
import io.github.petvat.katan.lwjgl3.tests.gdxTest
import io.github.petvat.katan.model.command.ChatCommands
import io.github.petvat.katan.model.command.GameCommands
import io.github.petvat.katan.model.command.KatanCommands
import io.github.petvat.katan.model.command.LobbyCommands
import io.github.petvat.katan.model.state.ClientState
import io.github.petvat.katan.model.state.GameSession
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.HexCoord
import io.github.petvat.katan.shared.hexlib.Layout
import io.github.petvat.katan.shared.hexlib.PCoord
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.BoardGenerator
import io.github.petvat.katan.shared.model.game.*
import io.github.petvat.katan.shared.protocol.Response
import io.github.petvat.katan.ui.Assets
import io.github.petvat.katan.ui.control.DragCameraController
import io.github.petvat.katan.ui.ktx.KtxKatan
import io.github.petvat.katan.ui.ktx.screen.MainGameScreen.Companion.TEX_HEIGHT
import io.github.petvat.katan.ui.ktx.screen.MainGameScreen.Companion.TEX_WIDTH
import io.github.petvat.katan.ui.ktx.screen.loadUISkin
import io.github.petvat.katan.ui.ktx.view.BoardView
import io.github.petvat.katan.ui.ktx.view.gameView
import io.github.petvat.katan.ui.viewmodel.BuildTarget
import io.github.petvat.katan.ui.viewmodel.GameScreenViewModel
import ktx.app.KtxGame
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors
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

    val game = GameSession(
        id = "game",
        player = 1,
        otherPlayers = listOf(2, 3, 4),
        board = board,
        colors = mapOf(
            1 to PlayerColor.CLR3,
            2 to PlayerColor.CLR2,
            3 to PlayerColor.CLR4,
            4 to PlayerColor.CLR1
        ),
        resources = ResourceMap(1, 1, 1, 1, 1),
        otherResources = mapOf(2 to 2, 3 to 3, 4 to 4),
        phase = Phase.SETUP,
        turnOrder = listOf(1, 2, 3, 4),
        turnIndex = 0,                       // was: turnPlayer (now a computed property)
        victoryPoints = mapOf(1 to 0, 2 to 0, 3 to 0, 4 to 0),
        onGoingTrades = emptyList(),         // new required field
        rules = RuleBook.from(Settings())    // new required field
    )

    /**
     * Hex layout for gameState window
     */
    val layout = Layout(
        PCoord(TEX_WIDTH / sqrt(3.0), (TEX_HEIGHT / 2) + 2), // Inradius width and height
        PCoord(0.0, 0.0) // Origin hex relative to viewport
    )

    // ViewModels read from ClientState, so the session must be pushed into it
    // before construction — same as production. onGroupCreated also seeds chat,
    // which GameViewModel's mirror requires.
    val state = ClientState().apply {
        onGroupCreated(Response.GroupCreated("game", "chat:game", Settings()))
        onGameInit(game!!)
    }

    val commands = KatanCommands(
        game = object : GameCommands {
            override fun rollDice() = null
            override fun build(kind: BuildKind, at: Coordinates) = null
            override fun moveRobber(to: HexCoord) = null
            override fun steal(fromPlayer: Int) = null
            override fun initTrade(offer: ResourceMap, inReturn: ResourceMap, targets: Set<Int>) = null
            override fun respondTrade(tradeId: Int, accept: Boolean) = null
            override fun endTurn() = null
            override fun claimVictory() = null
            override fun requestResync() = null
        },
        lobby = object : LobbyCommands {
            override fun register(name: String) = null
            override fun resume(token: String) = null
            override fun join(channelId: String) = null
            override fun create(settings: Settings) = null
            override fun leave() = null
        },
        chat = object : ChatCommands {
            override fun send(message: String) = null
        }
    )

    val viewModel = GameScreenViewModel(state, commands, layout)

    val eventSystem = EventSystem()

    val boardRenderer = BoardView(
        viewModel.board,   // new signature: (BoardViewModel, assets)
        assets
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

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)          // updates stage viewport
        viewport.update(width, height, true) // updates board's ExtendViewport
        camera.update()
    }

    override fun setup() {
        val multiplexer = InputMultiplexer(stage, movementController)
        Gdx.input.inputProcessor = multiplexer
        stage.actors {
            gameView(vm = viewModel.game, hud = viewModel.game.hud, Scene2DSkin.defaultSkin)
        }
        super.clearScreen = false
        eventSystem += viewModel

        // was: viewModel.initSettlementPlacingMode = true
        viewModel.board.selectPlacingTarget(BuildTarget.SETTLEMENT)
    }

    override fun render(delta: Float) {
        viewModel.refresh()   // poll mirrors BEFORE drawing (new since the mirror refactor)

        camera.update()
        Gdx.gl.glClearColor(0f, 0.2f, 0.3f, 0.8f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        batch.projectionMatrix = camera.combined

        boardRenderer.render(batch)   // now takes the batch

        stage.viewport.apply()
        stage.act()
        stage.draw()
    }
}
