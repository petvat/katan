package io.github.petvat.core.ui.model

import io.github.petvat.core.controller.RequestController
import io.github.petvat.katan.event.*
import io.github.petvat.katan.shared.model.game.GameMode
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.PermissionLevel
import io.github.petvat.katan.shared.protocol.dto.PublicGroupDTO

// TODO: Rename to View?
data class GroupModel(val id: String, val mode: GameMode, val numClients: Int, val level: PermissionLevel)


data class LobbyViewModel(
    val requestController: io.github.petvat.core.controller.RequestController,
    val transitionService: ViewTransitionService,
    val groups: MutableList<PublicGroupDTO>,
) : ViewModel() {

//    private val _groupModels = MutableStateFlow<Map<String, GroupModel>>(emptyMap())
//
//    val groupModels: StateFlow<Map<String, GroupModel>> = _groupModels.asStateFlow()

    var groupModels: Map<String, GroupModel> by propertyNotify(emptyMap())

    private fun addGroup(key: String, group: GroupModel) {
        groupModels += (key to group)
    }

    private fun updateGroups(groups: Map<String, GroupModel>) {
        groupModels += (groups)
    }

    fun handleJoin(id: String, groupName: String) {
        // TODO: Find the it using provided groupname.
        // groups.find { it.name == groupName }
        requestController.handleJoin(id)
    }

    fun handleCreate(vararg settings: Array<String> = arrayOf()) {
        // TODO: Parse settings info.
        requestController.handleCreate(Settings())
    }

    fun handleGetGroups() {
        requestController.handleGetGroup(5)
    }

    override fun onEvent(event: Event) {
        if (event is GetGroupsEvent) {
            // Full copy? Simplified version! Like, we don't need is and all that stuff!
            updateGroups(
                event.groups.associate {
                    it.id to GroupModel(it.id, it.mode, it.numClients, it.level)
                })
        }
        if (event is CreateEvent || event is JoinEvent) {
            transitionService(ScreenType.GROUP)
        }
        if (event is GroupUpdateEvent) {
            addGroup(event.groupId, GroupModel(event.groupId, event.gameMode, event.clientCount, event.level))
        }
    }
}
