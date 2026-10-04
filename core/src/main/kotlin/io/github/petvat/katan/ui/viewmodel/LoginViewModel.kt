package io.github.petvat.katan.ui.viewmodel

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.controller.LobbyActions
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.LobbyEvent
import io.github.petvat.katan.event.LoginEvent
import io.github.petvat.katan.model.command.LobbyCommands

/**
 *
 * Possible state transitions:
 * - LobbyView
 */
class LoginViewModel(
    private val commands: LobbyCommands,
    private val transitionService: ViewTransitionService
) : ViewModel() {

    private val logger = KotlinLogging.logger { }

    fun registerAsGuest(name: String) {
        commands.register(name)
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
