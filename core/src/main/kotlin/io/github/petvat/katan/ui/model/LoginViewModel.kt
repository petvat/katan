package io.github.petvat.core.ui.model

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.core.controller.RequestController
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.LobbyEvent
import io.github.petvat.katan.event.LoginEvent

/**
 *
 * Possible state transitions:
 * - LobbyView
 */
class LoginViewModel(
    private val outController: io.github.petvat.core.controller.RequestController,
    private val transitionService: ViewTransitionService
) : ViewModel() {

    private val logger = KotlinLogging.logger { }

    fun registerAsGuest(name: String) {
        // TODO: InEvent -> Error on bad input
//        if (port?.toIntOrNull() == null || port.toInt() < 10000) {
//            InEventBus.fire(ErrorEvent(""))
//        }
        outController.handleRegister(name)
    }

    override fun onEvent(event: Event) {
        if (event is LoginEvent) {
            logger.debug { "Screen switch to LOBBY" }
            transitionService(ScreenType.LOBBY)
        }
    }
}
