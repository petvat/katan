package io.github.petvat.katan.ui.viewmodel


import io.github.petvat.katan.controller.IChatActions
import io.github.petvat.katan.controller.IGameActions
import io.github.petvat.katan.event.*
import io.github.petvat.katan.model.GroupState

class GroupViewModel(
    private val group: GroupState,
    private val chatService: IChatActions,
    private val gameService: IGameActions,
    private val transitionService: ViewTransitionService,
) : ViewModel() {

    var lastMessage: Pair<String, String> by propertyNotify("" to "")

    fun handleInit() {
        gameService.init()
    }

    fun handleChat(message: String) {
        chatService.sendMessage(message)
    }

    override fun onEvent(event: Event) {
        when (event) {
            is InitEvent -> {
                transitionService(ScreenType.GAME)
            }

            is UserJoinedEvent -> {

            }

            is ChatEvent -> {
                lastMessage = event.from to event.message
            }

            else -> Unit
        }
    }
}
