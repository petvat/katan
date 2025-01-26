package io.github.petvat.katan.ui.model

import io.github.petvat.katan.controller.RequestController
import io.github.petvat.katan.event.ChatEvent
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.InitEvent
import io.github.petvat.katan.shared.protocol.dto.PrivateGroupView

class GroupViewModel(
    private val group: PrivateGroupView,
    private val outController: RequestController,
    private val transitionService: ViewTransitionService<*>,
) : ViewModel() {

    fun handleInit() {
        outController.handleInit()
    }

    /**
     * View-friendly.
     */
    fun getChatView(): List<Pair<String, String>> {
        return group.chatLog
    }

    override fun onEvent(event: Event) {
        when (event) {
            is InitEvent -> {
                transitionService.transition(ScreenType.GAME)
            }

            is ChatEvent -> {

            }

            else -> Unit
        }
    }
}
