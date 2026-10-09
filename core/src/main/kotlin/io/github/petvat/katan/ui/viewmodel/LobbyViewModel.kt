package io.github.petvat.katan.ui.viewmodel

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.event.*
import io.github.petvat.katan.model.command.LobbyCommands
import io.github.petvat.katan.model.state.ClientState
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.dto.GroupExternal


data class LobbyViewModel(
    val state: ClientState,
    val commands: LobbyCommands,
    val transitionService: ViewTransitionService,
    val groups: MutableList<GroupExternal>,
) : ViewModel() {
    val logger = KotlinLogging.logger { }

    val groupSummaries: Map<String, GroupExternal> by mirror(
        source = { state.lobby }, initial = state.lobby, project = { it }
    )

    fun handleJoin(id: String) {
        commands.join(id)
    }

    fun handleCreate(vararg settings: Array<String> = arrayOf()) {
        // TODO: Parse settings info.
        commands.create(Settings())
    }

//    fun handleGetGroups() {
//        lobbyService.handleGetGroup(5)
//    }

    override fun onEvent(event: Event) {

        if (event is CreateEvent || event is JoinEvent) {
            transitionService(ScreenType.GROUP)
        }
        if (event is GroupUpdateEvent) {
            logger.debug { "$event" }
        }
    }
}
