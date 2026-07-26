package io.github.petvat.katan.ui.viewmodel

import io.github.petvat.katan.controller.ILobbyActions
import io.github.petvat.katan.controller.LobbyActions
import io.github.petvat.katan.event.*
import io.github.petvat.katan.model.GroupSummary
import io.github.petvat.katan.shared.model.game.Settings


data class LobbyViewModel(
    val lobbyService: ILobbyActions, // Make Model bigger! Move controllers out.
    val transitionService: ViewTransitionService,
    val groups: MutableList<GroupSummary>,
) : ViewModel() {

    var groupSummaries: Map<String, GroupSummary> by propertyNotify(
        groups.associateBy { it.id }
    )

    private fun addGroup(key: String, group: GroupSummary) {
        groupSummaries = groupSummaries + (key to group)
    }

    private fun updateGroups(groups: Map<String, GroupSummary>) {
        groupSummaries = groupSummaries + groups
    }

    fun handleJoin(id: String) {
        lobbyService.join(id)
    }

    fun handleCreate(vararg settings: Array<String> = arrayOf()) {
        // TODO: Parse settings info.
        lobbyService.create(Settings())
    }

//    fun handleGetGroups() {
//        lobbyService.handleGetGroup(5)
//    }

    override fun onEvent(event: Event) {

        if (event is CreateEvent || event is JoinEvent) {
            transitionService(ScreenType.GROUP)
        }
        if (event is GroupUpdateEvent) {
            addGroup(
                event.groupSummary.id,
                event.groupSummary
            )
        }
    }
}
