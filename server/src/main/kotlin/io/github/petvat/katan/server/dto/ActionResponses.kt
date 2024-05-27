package io.github.petvat.katan.server.dto

import io.github.petvat.katan.server.action.ActionCode
import io.github.petvat.katan.server.board.BuildKind
import io.github.petvat.katan.server.board.Coordinate
import io.github.petvat.katan.server.board.ResourceMap

/**
 * Response to an action
 * Sent as an acknowledgement and may also provide some response data associated with a request.
 *
 * TODO: append gameID to support multiple games same client
 */
interface ActionDTO {
    val playerID: Int // Executer of action
}

// TODO: Maybe add a ResponseID/ActionID to each DTO or to ActionResponse,
//  so that client knows *what* a respond is responding to

// NOTE: Because data classes doesn't like inheritance
data class GenericDTO(
    val actionCode: ActionCode,
    override val playerID: Int,
) : ActionDTO

data class GameCreatedDTO(
    override val playerID: Int,
    val gameID: Int
) : ActionDTO

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

class TradeDTO(
    override val playerID: Int,
    val tradeID: Int,
    val accept: Boolean
) : ActionDTO

class GameStartedDTO(
    override val playerID: Int,
    val turnOrder: List<Int>
) : ActionDTO

// NOTE:
class TurnEndedDTO(
    override val playerID: Int
) : ActionDTO

/**
 * Response when setup has ended only.
 */
data class SetupEndedDTO(
    override val playerID: Int,
    val inventory: ResourceMap
) : ActionDTO
