package io.github.petvat.katan.ui.viewmodel


import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.event.*
import io.github.petvat.katan.model.command.GroupCommands
import io.github.petvat.katan.model.command.LobbyCommands
import io.github.petvat.katan.model.state.ChatSession
import io.github.petvat.katan.model.state.ClientState
import io.github.petvat.katan.model.state.GroupSession
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.ui.projection.Projector

class GroupViewModel(
    val state: ClientState,
    private val commands: GroupCommands,
    private val transitionService: ViewTransitionService,
) : ViewModel() {

    private val logger = KotlinLogging.logger {}
    val group by mirror(
        { state.group },
        requireNotNull(state.group) { "GroupScreen built before join." },
        { Projector.project(it) }
    )
    val chat by mirror(
        { state.chat },
        requireNotNull(state.chat) { "GroupScreen built before join." },
        { Projector.project(it) }
    )

    fun handleChat(message: String) {
        commands.chat(message)
    }

    fun handleInit() {
        commands.init()
    }

    fun leave() {
        commands.leave()
    }

    fun updateSettings(settings: Settings) {
        //
    }

    override fun onEvent(event: Event) {
        when (event) {
            InitGameEvent -> {
                transitionService(ScreenType.GAME)
            }

            is ChatEvent -> {
                logger.debug { "ChatEvent: $event" }
            }

            LeaveEvent -> TODO()
            UserJoinedEvent -> TODO()
            else -> Unit
        }
    }
}
