package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.game.GameProgress

class StealCard(
    override val gameProgress: GameProgress, override val playerID: Int,
    val stealCardFromPlayerID: Int
) : AbstractAction() {
    override fun execute(): Map<Int, ActionResponse> {
        TODO("Not yet implemented")
    }
}
