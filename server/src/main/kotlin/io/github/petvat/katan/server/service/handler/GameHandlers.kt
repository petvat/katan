package io.github.petvat.katan.server.service.handler

import io.github.petvat.katan.server.service.command.Build
import io.github.petvat.katan.server.service.command.RollDice
import io.github.petvat.katan.server.service.engine.EngineResult
import io.github.petvat.katan.server.service.engine.GameRuleEngine
import io.github.petvat.katan.shared.model.game.Phase
import io.github.petvat.katan.server.service.event.Event
import io.github.petvat.katan.server.service.event.GameEvent
import io.github.petvat.katan.server.service.channel.GameChannel
import io.github.petvat.katan.server.service.command.BuildInitSettlment
import io.github.petvat.katan.server.service.command.EndTurn
import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.protocol.ErrorCode

class RollDiceHandler : GameCommandHandler<RollDice> {

    override fun executeGameAction(
        channel: GameChannel,
        player: Int,
        engine: GameRuleEngine,
        command: RollDice
    ): GameEvent {
        val game = channel.snapshot
        if (game.phase != Phase.ROLL_DICE) {
            return Event.Failure("Cannot roll dice - not dice roll phase.", ErrorCode.DENIED)
        }

        if (player != game.turnPlayer) {
            return Event.Failure("Cannot roll dice - not your turn.", ErrorCode.DENIED)
        }
        val (roll1, roll2) = engine.rollDice()
        val eyes = roll1 + roll2

        val (resources, phase) = if (game.rules.moveRobberOn != eyes) {
            engine.harvestResources(game, eyes) to Phase.BUILD_N_TRADE
        } else {
            engine.discardResources(game) to Phase.MOVE_ROBBER
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
            targetChannelId = channel.id,
            roll1 = roll1,
            roll2 = roll2,
            resources = resources,
            nextPhase = phase
        )
    }
}

class BuildInitialSettlementHandler : GameCommandHandler<BuildInitSettlment> {
    override fun executeGameAction(
        channel: GameChannel,
        player: Int,
        engine: GameRuleEngine,
        command: BuildInitSettlment
    ): GameEvent {
        val game = channel.snapshot
        if (game.phase != Phase.SETUP) {
            return Event.Failure("Cannot build initial settlement -  not setup phase.", ErrorCode.DENIED)
        }

        when (val board =
            engine.buildVillage(game.board, player, command.coordinates as ICoordinates, VillageKind.SETTLEMENT)) {
            is EngineResult.Failure -> return Event.Failure(board.description, ErrorCode.DENIED)
            is EngineResult.Success -> {
                val vps = engine.countVictoryPoints(game)

                val playerData = game.players.single { it.number == player }

                val updatedPlayers = game.players.map {
                    val vp = vps[it.number]!!
                    playerData.copy(victoryPoints = vp)
                }

                channel.snapshot = game.copy(board = board.value, players = updatedPlayers)

                return GameEvent.Built(
                    targetChannelId = channel.id,
                    coordinates = command.coordinates,
                    buildKind = BuildKind.Village(VillageKind.SETTLEMENT),
                    vps = engine.countVictoryPoints(game) // Important as any build action could lead to change is VPs for any player
                )
            }
        }


    }

}

class BuildHandler : GameCommandHandler<Build> {
    override fun executeGameAction(
        channel: GameChannel,
        player: Int,
        engine: GameRuleEngine,
        command: Build
    ): GameEvent {
        val game = channel.snapshot
        if (game.phase != Phase.BUILD_N_TRADE)
            return Event.Failure("Cannot roll dice in current state ${game.phase}.", ErrorCode.DENIED)

        if (player != game.turnPlayer) {
            return Event.Failure("Cannot roll dice - not your turn.", ErrorCode.DENIED)
        }
        val playerData = game.players.find { it.number == player }!!


        if (!engine.hasSufficientResources(playerData, channel.snapshot.rules.getCost(command.buildKind))) {
            return Event.Failure("Not sufficient resources to build", ErrorCode.DENIED)
        }
        val board = when (command.buildKind) {
            is BuildKind.Village -> {
                engine.buildVillage(
                    game.board,
                    playerData.number,
                    command.coordinates as ICoordinates,
                    command.buildKind.kind
                )
            }

            is BuildKind.Road -> {
                engine.buildRoad(
                    game.board,
                    playerData.number,
                    command.coordinates as EdgeCoordinates,
                    command.buildKind.kind
                )
            }
        }

        when (board) {
            is EngineResult.Failure -> return Event.Failure(board.description, ErrorCode.DENIED)
            is EngineResult.Success -> {
                val vps = engine.countVictoryPoints(game)

                val updatedPlayers = game.players.map {
                    val vp = vps[it.number]!!
                    playerData.copy(victoryPoints = vp)
                }

                channel.snapshot = game.copy(board = board.value, players = updatedPlayers)

                return GameEvent.Built(
                    targetChannelId = channel.id,
                    coordinates = command.coordinates,
                    buildKind = command.buildKind,
                    vps = engine.countVictoryPoints(game) // Important as any build action could lead to change is VPs for any player
                )
            }
        }
    }
}

class EndTurnHandler : GameCommandHandler<EndTurn> {

    override fun executeGameAction(
        channel: GameChannel,
        player: Int,
        engine: GameRuleEngine,
        command: EndTurn
    ): GameEvent {
        val game = channel.snapshot
        if (game.phase != Phase.BUILD_N_TRADE)
            return Event.Failure("Cannot end turn in current state ${game.phase}.", ErrorCode.DENIED)

        if (player != game.turnPlayer) {
            return Event.Failure("Cannot end turn - not your turn.", ErrorCode.DENIED)
        }

        channel.snapshot = game.copy(
            turnPlayer = engine.nextTurn(game)
        )

        return GameEvent.TurnEnded(targetChannelId = channel.id, nextPlayer = channel.snapshot.turnPlayer)
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


