package io.github.petvat.katan.lwjgl3

import io.github.petvat.katan.shared.model.board.BoardGenerator
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.PermissionLevel
import io.github.petvat.katan.shared.protocol.dto.*
import io.github.petvat.katan.ui.ktx.screen.loadVisUISkin
import io.github.petvat.katan.ui.ktx.view.GameView
import io.github.petvat.katan.ui.model.GameViewModel
import ktx.app.KtxGame
import ktx.scene2d.Scene2DSkin


private class GameViewTest : KtxGame<GameTest>() {
    override fun create() {
        loadVisUISkin()
        addScreen(GameTest())
        setScreen<GameTest>()
    }
}


private class GameTest() : AbstractTestScreen() {

    val board = BoardGenerator.generateBoard(Settings()).fromDomain()

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
    val viewModel = GameViewModel(MockController(), group, game)

    override fun setup() {
        stage.addActor(
            GameView(viewModel, Scene2DSkin.defaultSkin)
        )
    }
}

