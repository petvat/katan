package io.github.petvat.katan.ui.model

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.controller.GameService
import io.github.petvat.katan.event.ConnectionEvent
import io.github.petvat.katan.event.Event

/**
 * Start view, where user can choose server host.
 */
class StartMenuViewModel(
    private val outController: GameService,
    private val transitionService: ViewTransitionService
) : ViewModel() {

    private val logger = KotlinLogging.logger { }

    fun connectToclient(address: String? = null, port: Int? = null) {

        // TODO: InEvent -> Error on bad input
//        if (port?.toIntOrNull() == null || port.toInt() < 10000) {
//            InEventBus.fire(ErrorEvent(""))
//        }
        outController.handleConnectClient(address, port)
    }

    override fun onEvent(event: Event) {
        if (event is ConnectionEvent) {
            logger.debug { "Screen switch to LOGIN" }
            transitionService(ScreenType.LOGIN)
        }
    }
}
