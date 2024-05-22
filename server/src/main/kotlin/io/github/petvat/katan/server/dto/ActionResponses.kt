package io.github.petvat.katan.server.dto

import io.github.petvat.katan.server.board.BuildKind
import io.github.petvat.katan.server.board.Coordinate
import io.github.petvat.katan.server.board.ResourceMap
import io.github.petvat.katan.server.game.GameState

/**
 * Response to an action
 */
interface ActionDTO {
    val playerID: Int // Executer of action
}

// TODO: Maybe add a ResponseID/ActionID to each DTO or to ActionResponse,
//  so that client knows *what* a respond is responding to

data class RollDiceDTO(
    override val playerID: Int,
    val roll1: Int,
    val roll2: Int,
    val resources: ResourceMap,
    val moveRobber: Boolean
) : ActionDTO

data class MoveRobberDTO(
    override val playerID: Int,
    val newRobberCoordinate: Coordinate,
    val nextState: Boolean = true // TODO: Not necessary
) : ActionDTO

class BuildDTO(
    override val playerID: Int,
    val buildKind: BuildKind,
    val coordinate: Coordinate

) : ActionDTO

class InitiateTradeDTO(
    override val playerID: Int,
    val tradeID: Int,
    val targetPlayers: Set<Int>,
    val offer: ResourceMap,
    val request: ResourceMap
) : ActionDTO
