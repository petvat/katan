package io.github.petvat.katan.ui.viewmodel

import io.github.petvat.katan.event.Event
import io.github.petvat.katan.model.BoardHelpers
import io.github.petvat.katan.model.command.KatanCommands
import io.github.petvat.katan.model.state.ClientState
import io.github.petvat.katan.model.state.GameSession
import io.github.petvat.katan.shared.hexlib.Layout
import io.github.petvat.katan.ui.projection.BoardProjector
import io.github.petvat.katan.ui.viewmodel.BoardViewModel

enum class BuildTarget { SETTLEMENT, CITY, ROAD }


class GameScreenViewModel(state: ClientState, commands: KatanCommands, layout: Layout) : ViewModel() {

    val board: BoardViewModel = BoardViewModel(state, commands.game, layout)
    val game: GameViewModel = GameViewModel(state, commands, board)

    override fun onEvent(event: Event) {
        game.onEvent(event)
        board.onEvent(event)
    }

    override fun refresh() {
        game.refresh()
        board.refresh()
    }
}
