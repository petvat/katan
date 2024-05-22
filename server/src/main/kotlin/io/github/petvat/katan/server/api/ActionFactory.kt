package io.github.petvat.katan.server.api

import io.github.petvat.katan.server.action.*

import io.github.petvat.katan.server.game.GameProgress
import io.github.petvat.katan.server.action.ActionID
import io.github.petvat.katan.server.board.BuildKind
import io.github.petvat.katan.server.board.Coordinate
import io.github.petvat.katan.server.board.RoadKind
import io.github.petvat.katan.server.board.VillageKind
import io.github.petvat.katan.server.dto.*

object ActionFactory {

    /**
     * @param actionRequest holds information about a new action request
     */
    fun createAction(
        actionRequest: ActionRequest,
        gameProgress: GameProgress
    ): Action {

        return when (actionRequest.actionID) {
            ActionID.ROLL -> RollDice(
                gameProgress,
                actionRequest.playerID
            )

            // Approach where
            ActionID.BUILD -> {
                val req = actionRequest as BuildRequest
                BuildAction(
                    gameProgress,
                    req.playerID,
                    req.coordinate,
                    req.buildKind
                )
            }

            ActionID.BUILD_SETTL_INIT -> {
                val req = actionRequest as PlaceFirstSettlementRequest
                PlaceFirstSettlements(
                    gameProgress,
                    req.playerID,
                    req.coordinate
                )
            }

            ActionID.INIT_TRADE -> {
                val req = actionRequest as InitiateTradeRequest
                // TODO: Target players
                val targetPlayers: Set<Int> = mutableSetOf()
                InitiateTrade(
                    gameProgress,
                    req.playerID,
                    targetPlayers,
                    req.offer,
                    req.request
                )
            }

            ActionID.RESPOND_TRADE -> {
                val req = actionRequest as RespondTradeRequest
                // TODO: Target players
                val targetPlayers: Set<Int> = mutableSetOf()
                RespondTrade(
                    gameProgress,
                    req.playerID,
                    req.accept
                )
            }


            else -> {
                throw IllegalArgumentException("Such action does not exist.")
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val actionRequest: ActionRequest =
            BuildRequest(
                1,
                1,
                ActionID.BUILD,
                BuildKind.Village(
                    VillageKind.SETTLEMENT
                ),
                Coordinate(1, 1)
            )
        BuildRequest(
            1,
            1,
            ActionID.BUILD,
            BuildKind.Road(
                RoadKind.ROAD
            ),
            Coordinate(1, 1)
        )

    }
}
