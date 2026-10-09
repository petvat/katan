package io.github.petvat.katan.ui.ktx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.kotcrab.vis.ui.VisUI
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.model.KatanClient
import io.github.petvat.katan.ui.KatanAssets
import io.github.petvat.katan.ui.disposeSkin
import io.github.petvat.katan.ui.ktx.screen.*
import io.github.petvat.katan.ui.loadUISkin
import io.github.petvat.katan.ui.viewmodel.ScreenType
import io.github.petvat.katan.ui.viewmodel.ViewTransitionService
import ktx.app.KtxGame


/**
 * Main class for LibGDX view context.
 *
 */
class KtxKatan(
    val model: KatanClient,
    val eventBus: EventSystem

) :
    KtxGame<AbstractScreen>() {

    companion object {
        const val VH = 400f
        const val VW = 400f
    }

    private val logger = KotlinLogging.logger { }

    lateinit var assets: KatanAssets

    lateinit var batch: SpriteBatch

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


    override fun render() {
        super.render() // delegates to the active screen's render, as KtxGame normally does
    }

    override fun create() {

        logger.debug { "Start screen init." }
        assets = KatanAssets()
        batch = SpriteBatch()

        loadUISkin()

        assert(VisUI.isLoaded())

        addScreen(MenuScreen(this, eventBus))
        addScreen(MainGameScreen(this, eventBus))
        addScreen(GroupScreen(this, eventBus))
        addScreen(LobbyScreen(this, eventBus))
        addScreen(LoginScreen(this, eventBus))

        setScreen<MenuScreen>()
    }

    override fun dispose() {
        disposeSkin()
        batch.dispose()
        assets.dispose()
    }
}
