package io.github.petvat.katan.ui.model

import io.github.petvat.katan.controller.RequestController
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.GetGroupsEvent
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.session.PublicSessionInfo
import io.github.petvat.katan.shared.protocol.dto.PublicGroupView
import io.github.petvat.katan.ui.ktx.screen.KtxScreenTransitionService

data class LobbyViewModel(
    val requestController: RequestController,
    val transitionService: ViewTransitionService<*>,
    val groups: List<PublicGroupView>,
) : ViewModel() {
    fun handleJoin(id: String, groupName: String) {
        // TODO: Find the it using provided groupname.
        // groups.find { it.name == groupName }
        requestController.handleJoin(id)
    }

    fun handleCreate(vararg settings: Array<String> = arrayOf()) {
        // TODO: Parse settings info.
        requestController.handleCreate(Settings())
    }

    override fun onEvent(event: Event) {
        if (event is GetGroupsEvent) {

        }
    }

}
