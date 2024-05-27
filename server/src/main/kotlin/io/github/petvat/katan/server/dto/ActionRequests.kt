package io.github.petvat.katan.server.dto

import io.github.petvat.katan.server.action.ActionCode
import io.github.petvat.katan.server.board.BuildKind
import io.github.petvat.katan.server.board.Coordinate
import io.github.petvat.katan.server.board.ResourceMap

/**
 * Request to do an action
 */
interface ActionRequest {
    val actionCode: ActionCode // NOTE: Not really necessary
    val playerID: Int
    val gameID: Int
}

/**
 * NOTE: gameID should be -1
 * FIXME: Fix gameID not need be required
 */
data class NewGameRequest(
    override val playerID: Int,
    override val gameID: Int,
    override val actionCode: ActionCode = ActionCode.GAME_CREATE
    // val gameSettings: GameSettings?
) : ActionRequest

data class RollDiceRequest(
    override val playerID: Int,
    override val gameID: Int,
    override val actionCode: ActionCode = ActionCode.ROLL_DICE
) : ActionRequest

/**
 * Request to build a structure at coordinate. Could be intersection or path structure.
 */
data class BuildRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionCode: ActionCode = ActionCode.BUILD,
    val buildKind: BuildKind,
    val coordinate: Coordinate
) : ActionRequest

data class PlaceFirstSettlementRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionCode: ActionCode = ActionCode.BUILD,
    val coordinate: Coordinate
) : ActionRequest

data class InitiateTradeRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionCode: ActionCode = ActionCode.INIT_TRADE,
    val tradeID: Int,
    val targetPlayersID: Set<Int>,
    val offer: ResourceMap,
    val request: ResourceMap
) : ActionRequest

data class RespondTradeRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionCode: ActionCode = ActionCode.RESPOND_TRADE,
    val tradeID: Int,
    val accept: Boolean
) : ActionRequest

data class StealCardRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionCode: ActionCode = ActionCode.STEAL_CARD,
    val stealFromPlayerID: Int
) : ActionRequest

data class MoveRobberRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionCode: ActionCode = ActionCode.MOVE_ROBBER,
    val newTileCoordinate: Coordinate
) : ActionRequest

data class EndTurnRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionCode: ActionCode = ActionCode.SETUP_END,
) : ActionRequest


