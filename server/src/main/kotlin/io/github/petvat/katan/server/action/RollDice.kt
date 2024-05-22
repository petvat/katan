package io.github.petvat.katan.server.action

// Make general
import io.github.petvat.katan.server.dto.RollDiceDTO
import io.github.petvat.katan.server.game.GameProgress
import kotlin.random.Random

class RollDice(
    override val gameProgress: GameProgress,
    // override val ID: Int,
    override val playerID: Int
) : AbstractAction() {
    var player = gameProgress.getPlayer(playerID)
    private val random = Random

    override fun execute(): Map<Int, ActionResponse> {
        val responses: MutableMap<Int, ActionResponse> = mutableMapOf()
        try {
            validatePlayerInTurn()
            val diceRoll: Pair<Int, Int> = rollDice()
            val moveRobber: Boolean = diceRoll.first + diceRoll.second == 7

            if (moveRobber) {
                // gameProgress.enqueue()
            }

            // TODO: Better approach regarding player resource diff.
            gameProgress.players.forEach { player ->
                val actionDTO = RollDiceDTO(
                    this.player.ID, diceRoll.first,
                    diceRoll.second, player.inventory,
                    moveRobber// Not optimal
                )
                responses[player.ID] =
                    ActionResponse(true, "$diceRoll was rolled.", actionDTO)
            }
        } catch (e: Exception) {
            responses.clear() // flush
            responses[playerID] =
                ActionResponse(false, e.message ?: "Unknown error occured.", null)
        }
        return responses
    }

    private fun rollDice(): Pair<Int, Int> {
        val roll1 = random.nextInt(1, 7)
        val roll2 = random.nextInt(1, 7)

        val eyes = roll1 + roll2
        if (eyes != 7) {
            harvestResources(eyes)
        } else {
            discardResources()
        }
        return Pair(roll1, roll2)
        // La rollDice() vere en generic action og ha eit oppgjer til slutt der ein teller endringa
        // iterer over kvar spelar, finn endring/listener el., ActionInformation/Type, terningkast, ressursar
    }

    /**
     * On not rolled 7.
     */
    private fun harvestResources(eyes: Int) {
        gameProgress.boardManager.tiles.filter { it.rollListenValue == eyes }
            .flatMap { t ->
                gameProgress.boardManager.getAdjacentBuildings(t)
                    .map { b -> b to t }
            }
            .forEach { (b, t) ->
                if (gameProgress.boardManager.robberLocation != t.coordinate)
                    t.resource?.let { b.harvest(it) }
            }
    }

    /**
     * On rolled 7.
     */
    private fun discardResources() {
        val players = gameProgress.players
        players.forEach { player ->
            val cards = player.cardCount
            if (cards >= 7) {
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
        // gameProgress.enqueue(MoveRobber())
    }

}
