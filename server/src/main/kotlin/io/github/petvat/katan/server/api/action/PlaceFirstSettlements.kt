package io.github.petvat.katan.server.api.action

import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.api.GameStates
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response


/**
 * NOTE:
 * TODO: Player Init Roads!
 *
 */
class PlaceFirstSettlements(
    override val game: Game,
    override val playerNumber: Int,
    private val intersectionCoordinate: Coordinates
) : Action {
    override fun validate(): Boolean {
        return playerNumber != game.playerInTurn()
    }

    override fun execute(): ExecutionResult<Response> {
        if (validate()) {
            return ExecutionResult.Failure(ErrorCode.DENIED, "Not your turn.")
        }

        game.boardManager.buildSettlementInitial(
            game.getPlayer(playerNumber)!!,
            intersectionCoordinate as ICoordinates,
            VillageKind.SETTLEMENT // Not optimal
        )
        val responses: Map<Int, Response>


        val buildDTO = Response.Build(
            playerNumber,
            BuildKind.Village(VillageKind.SETTLEMENT),
            intersectionCoordinate,
            game.getPlayer(playerNumber)!!.victoryPoints,
            null
        )


        if (game.turnIndex == game.setupTurnOrder.size - 1) {
            // Setup state done
            game.transitionToState(GameStates.ROLL_DICE)
            game.boardManager.harvestInitialResources() // TODO: Make private

            responses = game.players.associate { p ->
                p.playerNumber to Response.SetupEnded(
                    build = buildDTO,
                    thisPlayer = p.inventory,
                    otherPlayers = game.players
                        .filter { it.playerNumber != playerNumber }
                        .associate { it.playerNumber to p.inventory },
                    description = null
                )
            }
        } else {
            responses = game.players.associate { p ->
                p.playerNumber to buildDTO
            }
            game.nextTurn()
        }

        return ExecutionResult.Success(
            responses
        )
    }
}
