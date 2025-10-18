package io.github.petvat.katan.server.service.handler

import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.command.Build
import io.github.petvat.katan.server.service.command.RollDice
import io.github.petvat.katan.server.service.engine.EngineResult
import io.github.petvat.katan.server.service.engine.GameRuleEngine
import io.github.petvat.katan.server.service.engine.Phase
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.server.service.channel.GameChannel
import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.protocol.ErrorCode

class RollDiceHandler : GameCommandHandler<RollDice> {

    override fun execute(
        client: ConnectedClient,
        channel: GameChannel,
        command: RollDice
    ): GameEvent {
        val game = channel.snapshot

        // TODO CHECK game.find ClientId == Subscriber is PLAYER

        val player = channel.clientToPlayerId[client.auth.id] ?: return Event.Failure(
            "Player is not part of this game.",
            ErrorCode.DENIED
        )

        // TODO: Very redundant! Find way to inject without creating
        val gameRuleEngine = GameRuleEngine(game.rules)

        if (player != game.turnPlayer) {
            return Event.Failure("Cannot roll dice - not your turn.", ErrorCode.DENIED)
        }
        val (roll1, roll2) = gameRuleEngine.rollDice()
        val eyes = roll1 + roll2

        val (resources, phase) = if (game.rules.moveRobberOn != eyes) {
            gameRuleEngine.harvestResources(game, eyes) to Phase.BUILD_N_TRADE
        } else {
            gameRuleEngine.discardResources(game) to Phase.MOVE_ROBBER
        }

        channel.snapshot =
            game.copy(
                players = game.players.map {
                    it.copy(
                        resources = resources[it.number]!!
                    )
                }
            )

        return GameEvent.DiceRolledSummary(
            roll1 = roll1,
            roll2 = roll2,
            resources = resources,
            nextPhase = phase
        )

    }
}

class BuildHandler : GameCommandHandler<Build> {
    override fun execute(client: ConnectedClient, channel: GameChannel, command: Build): Event {
        val game = channel.snapshot
        if (game.phase != Phase.BUILD_N_TRADE)
            return Event.Failure("Cannot roll dice in current state ${game.phase}.", ErrorCode.DENIED)

        val playerNum =
            channel.clientToPlayerId[client.auth.id] ?: return Event.Failure(
                "Player is not part of this game.",
                ErrorCode.DENIED
            )
        if (playerNum != game.turnPlayer) {
            return Event.Failure("Cannot roll dice - not your turn.", ErrorCode.DENIED)
        }
        val player = game.players.find { it.number == playerNum }!!

        val gameRuleEngine = GameRuleEngine(game.rules) // TODO: Remove

        if (!gameRuleEngine.hasSufficientResources(player, command.buildKind.cost)) {
            return Event.Failure("Not sufficient resources to build", ErrorCode.DENIED)
        }
        val board = when (command.buildKind) {
            is BuildKind.Village -> {
                gameRuleEngine.buildVillage(
                    game.board,
                    player.number,
                    command.coordinates as ICoordinates,
                    command.buildKind.kind
                )
            }

            is BuildKind.Road -> {
                gameRuleEngine.buildRoad(
                    game.board,
                    player.number,
                    command.coordinates as EdgeCoordinates,
                    command.buildKind.kind
                )
            }
        }

        when (board) {
            is EngineResult.Failure -> return Event.Failure(board.description, ErrorCode.DENIED)
            is EngineResult.Success -> {
                val vps = gameRuleEngine.countVictoryPoints(game)

                val updatedPlayers = game.players.map {
                    val vp = vps[it.number]!!
                    player.copy(victoryPoints = vp)
                }

                channel.snapshot = game.copy(board = board.value, players = updatedPlayers)

                return GameEvent.Built(
                    coordinates = command.coordinates,
                    buildKind = command.buildKind,
                    vps = gameRuleEngine.countVictoryPoints(game) // Important as any build action could lead to change is VPs for any player
                )
            }
        }
    }
}


//
//class RollDiceHandler : GameCommandHandler<RollDice> {
//
//    override fun execute(playerId: PlayerId, game: GameRuleEngine, command: RollDice): GameEvent {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * On not rolled move robber.
//     */
//    private fun harvestResources(game: GameRuleEngine, eyes: Int) {
//        game.boardManager.board.tiles.filter { it.rollListenValue == eyes }
//            .flatMap { t ->
//                game.boardManager.getAdjacentBuildings(t)
//                    .map { b -> b to t }
//            }
//            .forEach { (b, t) ->
//                if (game.boardManager.robberLocation != t.hexCoordinate)
//                    t.resource?.let { b.harvest(it) }
//            }
//    }
//
//    /**
//     * On rolled move robber.
//     */
//    private fun discardResources(game: GameRuleEngine) {
//        val userToPlayerId = game.userToPlayerId
//        userToPlayerId.forEach { player ->
//            val cards = player.resources.count()
//            if (cards >= game.ruleBook.cardLimit) {
//                val removes = cards.floorDiv(2)
//                repeat(removes) {
//                    Random.nextInt(0, player.resources.count())
//                    if (player.resources.getMap().isNotEmpty()) {
//                        val randomResource = player.resources.getMap()
//                            .filter { it.value > 0 }.keys.random()
//                        player.resources.transaction(randomResource, -1)
//                    }
//                }
//            }
//        }
//    }
//}


