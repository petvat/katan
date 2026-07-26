package io.github.petvat.katan.ui.viewmodel

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.controller.LobbyActions
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.LoginEvent

/**
 *
 * Possible state transitions:
 * - LobbyView
 */
class LoginViewModel(
    private val lobbyService: LobbyActions,
    private val transitionService: ViewTransitionService
) : ViewModel() {

    private val logger = KotlinLogging.logger { }

    fun registerAsGuest(name: String) {
        lobbyService.register(name)
    }

    fun registerAsUser(name: String, password: String) {
        TODO()
    }

    override fun onEvent(event: Event) {
        if (event is LoginEvent) {
            logger.debug { "Screen switch to LOBBY" }
            transitionService(ScreenType.LOBBY)
        }
    }
}
