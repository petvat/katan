package io.github.petvat.katan.ui.ktx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.kotcrab.vis.ui.VisUI
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.controller.NioController
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

    private lateinit var _controller: NioController

    var controller: NioController
        get() = _controller
        set(value) {
            _controller = value
        }

    val transitionService: ViewTransitionService = { screenType: ScreenType ->
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
