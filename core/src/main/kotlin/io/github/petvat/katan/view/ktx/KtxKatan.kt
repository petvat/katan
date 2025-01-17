package io.github.petvat.katan.view.ktx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.kotcrab.vis.ui.VisUI
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.client.ResponseController
import io.github.petvat.katan.controller.KtxInputController
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.view.Assets
import io.github.petvat.katan.view.KatanView
import io.github.petvat.katan.view.ktx.screen.*
import ktx.app.KtxGame
import ktx.scene2d.Scene2DSkin

/**
 * Main class for LibGDX view context.
 *
 */
open class KtxKatan(val model: KatanModel) :
    KtxGame<AbstractScreen>(), KatanView {

    private val logger = KotlinLogging.logger { }
    lateinit var assets: Assets
    lateinit var batch: SpriteBatch
    private lateinit var _controller: KtxInputController // Backing property

    var controller: KtxInputController
        get() = _controller
        set(value) {
            // Add custom logic here if needed
            _controller = value
        }

    companion object {
        const val VH = 200f
        const val VW = 200f
    }

    override fun create() {
        // Gdx.app.logLevel = LOG_DEBUG
        //responseController.views += this

        // logger.debug { responseController.views }

        logger.debug { "Start screen init." }
        assets = Assets()
        batch = SpriteBatch()

        // loadJsonSkin()
        loadVisUISkin()

        assert(VisUI.isLoaded())

        addScreen(MenuScreen(this))
        addScreen(MainGameScreen(this))
        addScreen(GroupScreen(this))
        addScreen(LobbyScreen(this))
        setScreen<MenuScreen>()
    }

    override fun dispose() {
        assets.manager.dispose()
        batch.dispose()
    }

    override fun showNewChatMessage() {

    }

    override fun showLoggedInView() {
        TODO("Not yet implemented")
    }


    override fun showGroupView() {

        Gdx.app.postRunnable {
            logger.debug { "Group View" }
            setScreen<GroupScreen>()
        }
    }

    override fun showGameView() {
        logger.debug { "Game init." }
        Gdx.app.postRunnable {
            logger.debug { "Run from main." }
            setScreen<MainGameScreen>()
        }
    }

    override fun updateGroupsView(groups: List<Pair<String, String>>) {
        Gdx.app.postRunnable {
            (currentScreen as LobbyScreen).updateGroups(groups)
        }
    }

    override fun showLobbyView() {
        logger.debug { "GET TO THIS" }
        Gdx.app.postRunnable {
            logger.debug { "Lobby view" }
            setScreen<LobbyScreen>()
        }
    }

    override fun showErrorView(error: String) {
        Gdx.app.postRunnable {
            println("Error! $error")
        }
    }

    override fun showGameUpdate() {
        TODO("Not yet implemented")
    }

}
