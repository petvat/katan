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
import io.github.petvat.katan.server.service.command.InitTrade
import io.github.petvat.katan.server.service.command.RespondTrade
import io.github.petvat.katan.server.service.engine.tradesystem.TradeContext
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
        // TODO: Move to Engine
        val (roll1, roll2) = engine.rollDice()
        val eyes = roll1 + roll2

        val (resources, phase) = if (game.rules.moveRobberOn != eyes) {
            engine.harvestResources(game.board, eyes, game.board.robberLocation) to Phase.BUILD_N_TRADE
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
            return Event.Failure("Cannot build initial settlement - not setup phase.", ErrorCode.DENIED)
        }
        val playerData = game.players.find { it.number == player }!!

        return when (val result =
            engine.buildInitial(playerData, BuildKind.Village(VillageKind.SETTLEMENT), command.coordinates, game)
        ) {
            is EngineResult.Failure -> Event.Failure(result.description, ErrorCode.DENIED)
            is EngineResult.Success -> {
                channel.snapshot = result.value
                GameEvent.Built(
                    targetChannelId = channel.id,
                    coordinates = command.coordinates,
                    buildKind = BuildKind.Village(VillageKind.SETTLEMENT),
                    vps = result.value.players.associate { it.number to it.victoryPoints }
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
            return Event.Failure("Cannot build in current state ${game.phase}.", ErrorCode.DENIED)
        if (player != game.turnPlayer)
            return Event.Failure("Cannot build - not your turn.", ErrorCode.DENIED)
        val playerData = game.players.find { it.number == player }!!

        return when (val result = engine.build(playerData, command.buildKind, command.coordinates, game)) {
            is EngineResult.Failure -> Event.Failure(result.description, ErrorCode.DENIED)
            is EngineResult.Success -> {
                channel.snapshot = result.value
                GameEvent.Built(
                    targetChannelId = channel.id,
                    coordinates = command.coordinates,
                    buildKind = command.buildKind,
                    vps = result.value.players.associate { it.number to it.victoryPoints }
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
            ongoingTrades = emptyList(), // Expire all remaining trades
            turnPlayer = engine.advanceTurn(game)
        )

        return GameEvent.TurnEnded(targetChannelId = channel.id, nextPlayer = channel.snapshot.turnPlayer)
    }

}


class InitTradeHandler : GameCommandHandler<InitTrade> {
    override fun executeGameAction(
        channel: GameChannel,
        player: Int,
        engine: GameRuleEngine,
        command: InitTrade
    ): GameEvent {
        val game = channel.snapshot
        if (game.phase != Phase.BUILD_N_TRADE)
            return Event.Failure("Cannot trade in current phase ${game.phase}.", ErrorCode.DENIED)
        if (player != game.turnPlayer)
            return Event.Failure("Cannot trade - not your turn.", ErrorCode.DENIED)

        val initiator = game.players.find { it.number == player }!!
        if (command.targets.isEmpty() || player in command.targets)
            return Event.Failure("Invalid trade targets.", ErrorCode.DENIED)
        if (!initiator.resources.affords(command.offer))
            return Event.Failure("You cannot afford this offer.", ErrorCode.DENIED)

        val tradeId = game.ongoingTrades.maxOfOrNull { it.id }?.plus(1) ?: 0
        val trade = TradeContext(
            id = tradeId, initiator = initiator, targets = command.targets,
            offer = command.offer, inReturn = command.inReturn, acceptedBy = null, alive = true
        )

        channel.snapshot = game.copy(ongoingTrades = game.ongoingTrades + trade)
        return GameEvent.TradeInitiated(
            targetChannelId = channel.id, initiator = player, tradeId = tradeId,
            targets = command.targets, offer = command.offer, inReturn = command.inReturn
        )
    }
}

class RespondTradeHandler : GameCommandHandler<RespondTrade> {
    override fun executeGameAction(
        channel: GameChannel,
        player: Int,
        engine: GameRuleEngine,
        command: RespondTrade
    ): GameEvent {
        val game = channel.snapshot
        val trade = game.ongoingTrades.singleOrNull { it.id == command.tradeId }
            ?: return Event.Failure("No such trade.", ErrorCode.NOT_FOUND)
        if (player !in trade.pending)
            return Event.Failure("This trade is not pending for you.", ErrorCode.DENIED)

        if (!command.accept) {
            val declined = trade.copy(declinedBy = trade.declinedBy + player)
            channel.snapshot = game.copy(
                ongoingTrades = game.ongoingTrades.map { if (it.id == trade.id) declined else it }
            )
            return GameEvent.TradeDeclined(
                targetChannelId = channel.id, tradeId = trade.id, by = player,
                dead = declined.pending.isEmpty()
            )
        }

        val acceptor = game.players.find { it.number == player }!!
        val transacted = trade.transact(acceptor)
            ?: return Event.Failure("Trade is no longer executable.", ErrorCode.DENIED)

        val (newInitiator, newAcceptor) = transacted
        channel.snapshot = game.copy(
            players = game.players.map { p ->
                when (p.number) {
                    newInitiator.number -> newInitiator
                    newAcceptor.number -> newAcceptor
                    else -> p
                }
            },
            ongoingTrades = game.ongoingTrades - trade
        )
        return GameEvent.TradeExecuted(
            targetChannelId = channel.id, tradeId = trade.id,
            initiator = trade.initiator.number, acceptor = player,
            resources = mapOf(
                newInitiator.number to newInitiator.resources,
                newAcceptor.number to newAcceptor.resources
            )
        )
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


