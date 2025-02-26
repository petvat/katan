package io.github.petvat.katan.server.api.action

import io.github.petvat.katan.server.api.ExecutionResult
import io.github.petvat.katan.server.api.GameStates
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Request
import io.github.petvat.katan.shared.protocol.Response
import kotlin.random.Random

class RollDice(
    override val game: Game,
    override val playerNumber: Int
) : Action {
    private val random = Random

    override fun validate(): Boolean {
        return playerNumber != game.playerInTurn()
    }

    override fun execute(): ExecutionResult<Response.DiceRolled> {
        val responses: MutableMap<Int, Response.DiceRolled> = mutableMapOf()

        if (validate()) {
            return ExecutionResult.Failure(ErrorCode.DENIED, "Not your turn.")
        }

        val diceRoll: Pair<Int, Int> = rollDice()
        val moveRobber: Boolean = diceRoll.first + diceRoll.second == 7
        val newCardCounts = mutableMapOf<Int, Int>()
        game.players.forEach { player ->
            newCardCounts[player.playerNumber] = player.inventory.count()
        }
        // newCardCounts.filterNot { (pid, count) -> pid == player.playerNumber },

        // TODO: Better approach regarding player resource diff.
        game.players.forEach { player ->
            val actionDTO = Response.DiceRolled(
                playerNumber = playerNumber,
                roll1 = diceRoll.first,
                roll2 = diceRoll.second,
                player.inventory,
                othersResources = game.players
                    .filter { it.playerNumber != playerNumber }
                    .associate { it.playerNumber to it.inventory },
                moveRobber = moveRobber,
                description = "Dice rolled."
            )

            responses[player.playerNumber] =
                actionDTO
        }

        game.transitionToState(
            if (moveRobber) GameStates.MOVE_ROBBER else GameStates.ROLL_DICE
        )

        return ExecutionResult.Success(responses)
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
            val cards = player.inventory.count()
            if (cards >= game.settings.cardLimit) {
                val removes = cards.floorDiv(2)
                repeat(removes) {
                    Random.nextInt(0, player.inventory.count())
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
    }

}
