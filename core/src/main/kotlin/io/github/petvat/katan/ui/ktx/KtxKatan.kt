package io.github.petvat.core.ui.ktx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.kotcrab.vis.ui.VisUI
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.core.controller.KtxInputController
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.ui.Assets
import io.github.petvat.katan.ui.ktx.screen.*
import io.github.petvat.katan.ui.model.ScreenType
import io.github.petvat.katan.ui.model.ViewTransitionService
import ktx.app.KtxGame


/**
 * Main class for LibGDX view context.
 *
 */
class KtxKatan(val model: KatanModel) :
    KtxGame<AbstractScreen>() {

    companion object {
        const val VH = 200f
        const val VW = 200f
    }

    private val logger = KotlinLogging.logger { }

    lateinit var assets: Assets

    lateinit var batch: SpriteBatch

    private lateinit var _controller: io.github.petvat.core.controller.KtxInputController

    init {
        logger.debug { "test" }
    }

    var controller: io.github.petvat.core.controller.KtxInputController
        get() = _controller
        set(value) {
            _controller = value
        }

    val transitionService: ViewTransitionService = { screenType: ScreenType ->
        logger.debug { "Main entry transitionService." }

        Gdx.app.postRunnable {
            when (screenType) {
                ScreenType.MENU -> setScreen<MenuScreen>()
                ScreenType.GAME -> setScreen<MainGameScreen>()
                ScreenType.GROUP -> setScreen<GroupScreen>()
                ScreenType.LOBBY -> setScreen<LobbyScreen>()
                ScreenType.LOGIN -> setScreen<LoginScreen>()
            }
        }
    }


    override fun create() {

        logger.debug { "Start screen init." }
        assets = Assets()
        batch = SpriteBatch()

        currentScreen

        // loadJsonSkin()
        loadVisUISkin()

        assert(VisUI.isLoaded())

        addScreen(MenuScreen(this))
        addScreen(MainGameScreen(this))
        addScreen(GroupScreen(this))
        addScreen(LobbyScreen(this))
        addScreen(LoginScreen(this))

        setScreen<MenuScreen>()
    }
}
