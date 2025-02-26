package io.github.petvat.core.ui.model

import io.github.petvat.core.controller.RequestController
import io.github.petvat.katan.event.*
import io.github.petvat.katan.shared.protocol.dto.PrivateGroupDTO

class GroupViewModel(
    private val group: PrivateGroupDTO, // <- TODO
    private val outController: io.github.petvat.core.controller.RequestController,
    private val transitionService: ViewTransitionService,
) : ViewModel() {

    var lastGroupMessage: Pair<String, String> by propertyNotify("" to "") // Nice?
    private val chatLogProperty: MutableList<Pair<String, String>> by propertyNotify(mutableListOf())
    private val groupModel: MutableList<GroupModel> by propertyNotify(mutableListOf())

    fun handleInit() {
        outController.handleInit()
    }

    fun handleChat(message: String) {
        outController.handleChat(message, null)
    }

    override fun onEvent(event: Event) {
        when (event) {
            is InitEvent -> {
                transitionService(ScreenType.GAME)
            }

            is UserJoinedEvent -> {

            }

            is ChatEvent -> {
                // lastGroupMessage = event.from to event.message
                chatLogProperty += event.from to event.message
            }

            else -> Unit
        }
    }
}
