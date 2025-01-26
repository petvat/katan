package io.github.petvat.katan.server.action

import io.github.petvat.katan.server.api.BuildAndTradeState
import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.api.MoveRobberState
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.protocol.dto.ActionResponse
import kotlin.random.Random

class RollDice(
    override val game: Game,
    override val playerNumber: Int
) : Action {
    var player = game.getPlayer(playerNumber)
    private val random = Random

    override fun validate(): String? {
        if (playerNumber != game.playerInTurn()) {
            return "Not your turn."
        }
        return null
    }

    override fun execute(): ExecutionResult<ActionResponse.RollDice> {
        val responses: MutableMap<Int, ActionResponse.RollDice> = mutableMapOf()

        validate()?.let { return ExecutionResult.Failure(it) }

        val diceRoll: Pair<Int, Int> = rollDice()
        val moveRobber: Boolean = diceRoll.first + diceRoll.second == 7

        // TODO: State switch, MoveRobber() or BuildAndTrade()

        game.transitionToState(
            if (moveRobber) MoveRobberState(game) else BuildAndTradeState(
                game
            )
        )

        val newCardCounts = mutableMapOf<Int, Int>()
        game.players.forEach { player ->
            newCardCounts[player.playerNumber] = player.cardCount
        }

        // TODO: Better approach regarding player resource diff.
        game.players.forEach { player ->
            val actionDTO = ActionResponse.RollDice(
                playerNumber,
                diceRoll.first,
                diceRoll.second,
                player.inventory,
                newCardCounts.filterNot { (pid, count) -> pid == player.playerNumber },
                moveRobber// Not optimal
            )

            responses[player.playerNumber] =
                actionDTO
        }
        return ExecutionResult.Success(responses, "Rolled dice")
    }

    private fun rollDice(): Pair<Int, Int> {
        val roll1 = random.nextInt(1, 7)
        val roll2 = random.nextInt(1, 7)

        val eyes = roll1 + roll2
        if (eyes != game.settings.numRobber) {
            harvestResources(eyes)
        } else {
            discardResources()
        }
        return Pair(roll1, roll2)
        // La rollDice() vere en generic action og ha eit oppgjer til slutt der ein teller endringa
        // iterer over kvar spelar, finn endring/listener el., ActionInformation/Type, terningkast, ressursar
    }

    /**
     * On not rolled move robber.
     */
    private fun harvestResources(eyes: Int) {
        game.boardManager.board.tiles.filter { it.rollListenValue == eyes }
            .flatMap { t ->
                game.boardManager.getAdjacentBuildings(t)
                    .map { b -> b to t }
            }
            .forEach { (b, t) ->
                if (game.boardManager.robberLocation != t.hexCoordinate)
                    t.resource?.let { b.harvest(it) }
            }
    }

    /**
     * On rolled move robber.
     */
    private fun discardResources() {
        val players = game.players
        players.forEach { player ->
            val cards = player.cardCount
            if (cards >= game.settings.cardLimit) {
                val removes = cards.floorDiv(2)
                repeat(removes) {
                    Random.nextInt(0, player.cardCount)
                    val inventory = player.inventory.get()
                    if (inventory.isNotEmpty()) {
                        // TODO: inventory.filter prob not working
                        inventory.filter { it.value > 0 }
                        val randomResource = inventory.keys.random()
                        player.inventory.transaction(randomResource, -1)
                    }
                }
            }
        }
        // KISS:
        // add to queue if specific, else check turn
        // TODO: Should invoke robber as a follow-up response
        // Problem: Action ikkje initialisert, ikkje bruke queue, men excpect next
        // game.enqueue(MoveRobber())
    }

}
