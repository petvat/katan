package io.github.petvat.katan.ui.ktx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.kotcrab.vis.ui.VisUI
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.controller.ChatActions
import io.github.petvat.katan.controller.GameActions
import io.github.petvat.katan.controller.LobbyActions
import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.model.ClientState
import io.github.petvat.katan.networking.NetworkSession
import io.github.petvat.katan.ui.Assets
import io.github.petvat.katan.ui.ktx.screen.*
import io.github.petvat.katan.ui.viewmodel.ScreenType
import io.github.petvat.katan.ui.viewmodel.ViewTransitionService
import ktx.app.KtxGame


/**
 * Main class for LibGDX view context.
 *
 */
class KtxKatan(
    val model: ClientState,
    val lobbyService: LobbyActions,
    val gameActions: GameActions,
    val chatActions: ChatActions,
    val networkSession: NetworkSession,
    val eventBus: EventSystem

) :
    KtxGame<AbstractScreen>() {

    companion object {
        const val VH = 400f
        const val VW = 400f
    }

    private val logger = KotlinLogging.logger { }

    lateinit var assets: Assets

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
        networkSession.poll()
        super.render() // delegates to the active screen's render, as KtxGame normally does
    }

    override fun create() {

        logger.debug { "Start screen init." }
        assets = Assets()
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
