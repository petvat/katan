package io.github.petvat.katan.server.dto

import io.github.petvat.katan.server.action.ActionID
import io.github.petvat.katan.server.board.BuildKind
import io.github.petvat.katan.server.board.Coordinate
import io.github.petvat.katan.server.board.ResourceMap
import io.github.petvat.katan.server.game.GameSettings

/**
 * Request to do an action
 */
interface ActionRequest {
    val playerID: Int
    val gameID: Int
    val actionID: ActionID
}

/**
 * NOTE: gameID should be -1
 * FIXME: Fix gameID not need be required
 */
data class NewGameRequest(
    override val playerID: Int,
    override val gameID: Int,
    override val actionID: ActionID,
    val gameSettings: GameSettings
) : ActionRequest

data class RollDiceRequest(
    override val playerID: Int,
    override val gameID: Int,
    override val actionID: ActionID = ActionID.ROLL
) : ActionRequest

/**
 * Request to build a structure at coordinate. Could be intersection or path structure.
 */
data class BuildRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionID: ActionID = ActionID.BUILD,
    val buildKind: BuildKind,
    val coordinate: Coordinate
) : ActionRequest

data class PlaceFirstSettlementRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionID: ActionID = ActionID.BUILD_SETTL_INIT,
    val coordinate: Coordinate
) : ActionRequest

data class InitiateTradeRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionID: ActionID = ActionID.INIT_TRADE,
    val tradeID: Int,
    val targetPlayersID: Set<Int>,
    val offer: ResourceMap,
    val request: ResourceMap
) : ActionRequest

data class RespondTradeRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionID: ActionID = ActionID.RESPOND_TRADE,
    val tradeID: Int,
    val accept: Boolean
) : ActionRequest

data class StealCardRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionID: ActionID = ActionID.SETUP,
    val stealFromPlayerID: Int
) : ActionRequest

data class MoveRobberRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionID: ActionID = ActionID.SETUP,
    val newTileCoordinate: Coordinate
) : ActionRequest

data class EndTurnRequest(
    override val gameID: Int,
    override val playerID: Int,
    override val actionID: ActionID = ActionID.SETUP,
) : ActionRequest


