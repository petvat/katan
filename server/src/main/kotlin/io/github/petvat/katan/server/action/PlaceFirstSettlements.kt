package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.api.RollDiceState
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.protocol.dto.ActionResponse


class PlaceFirstSettlements(
    override val game: Game,
    override val playerNumber: Int,
    private val intersectionCoordinate: Coordinates
) : Action {
    override fun validate(): String? {
        if (playerNumber != game.playerInTurn()) {
            return "Not your turn!"
        }
        return null
    }

    override fun execute(): ExecutionResult<ActionResponse.PlaceInitSettlment> {
        validate()?.let { ExecutionResult.Failure(it) }

        game.boardManager.buildSettlementInitial(
            game.getPlayer(playerNumber)!!,
            intersectionCoordinate as ICoordinates,
            VillageKind.SETTLEMENT // Not optimal
        )
        val responses: Map<Int, ActionResponse.PlaceInitSettlment>

        if (game.turnIndex == game.setupTurnOrder.size - 1) {
            // Setup state done
            game.transitionToState(RollDiceState(game))
            game.boardManager.harvestInitialResources() // TODO: Make private

            responses = game.players.associate { p ->
                p.playerNumber to ActionResponse.PlaceInitSettlment(
                    intersectionCoordinate,
                    true,
                    inventory = p.inventory,
                    game.players
                        .filter { it.playerNumber != playerNumber }
                        .associate { it.playerNumber to p.cardCount }
                )
            }
        } else {
            responses = game.players.associate { p ->
                p.playerNumber to ActionResponse.PlaceInitSettlment(
                    intersectionCoordinate,
                    false,
                    null,
                    null
                )
            }
            game.setNextTurn(game.setupTurnOrder[game.turnIndex++])
        }

        return ExecutionResult.Success(
            responses, "Initial settlement built on $intersectionCoordinate."
        )
    }
}
