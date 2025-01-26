package io.github.petvat.katan.ui.model

import io.github.petvat.katan.controller.RequestController
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.LobbyEvent

class StartMenuViewModel(
    private val outController: RequestController,
    private val transitionService: ViewTransitionService<*>
) : ViewModel() {

    fun connectToclient(address: String? = null, port: Int? = null) {

        // TODO: InEvent -> Error on bad input
//        if (port?.toIntOrNull() == null || port.toInt() < 10000) {
//            InEventBus.fire(ErrorEvent(""))
//        }
        outController.connectClient(address, port)
    }

    override fun onEvent(event: Event) {
        if (event is LobbyEvent) {
            transitionService.transition(ScreenType.LOBBY)
        }
    }
}
