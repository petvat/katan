package io.github.petvat.katan.ui.viewmodel

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.event.ConnectionEvent
import io.github.petvat.katan.event.ErrorEvent
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.model.KatanClient
import io.github.petvat.katan.model.command.LobbyCommands
import io.github.petvat.katan.shared.protocol.ErrorCode

/**
 * Start view, where user can choose server host.
 */
class StartMenuViewModel(
    private val client: KatanClient,
    private val transitionService: ViewTransitionService,
    private val eventSystem: EventSystem
) : ViewModel() {
    private val logger = KotlinLogging.logger { }

    fun connect(address: String? = null, port: Int? = null) {
        if (client.connect(address, port)) {
            transitionService(ScreenType.LOGIN)
        } else eventSystem.fire(ErrorEvent("Could not connect to server", ErrorCode.COULD_NOT_CONNECT))

    }

    override fun onEvent(event: Event) {
        if (event is ConnectionEvent) {
            logger.debug { "Screen switch to LOGIN" }
        }
    }
}
