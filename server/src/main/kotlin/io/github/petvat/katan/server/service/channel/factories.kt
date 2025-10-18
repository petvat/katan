package io.github.petvat.katan.server.service.channel

import io.github.petvat.katan.server.service.client.ClientId
import io.github.petvat.katan.server.service.engine.GameSnapshot
import io.github.petvat.katan.server.service.engine.Phase
import io.github.petvat.katan.server.service.engine.Player
import io.github.petvat.katan.server.service.engine.RuleBook
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.model.board.BoardGenerator
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Settings
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap


private enum class Type(val prefix: String) {
    SYSTEM("sys:"),
    LOBBY("lobby:"),
    GROUP("group:"),
    CHAT("chat:"),
    GAME("game:")
}

private fun generateId(type: Type) = ChannelId(type.prefix + UUID.randomUUID().toString())

object GroupFactory {
    fun create(settings: Settings, members: Collection<UserId>, fromLobby: LobbyChannel): GroupChannel {
        val groupId = generateId(Type.GROUP)
        return GroupChannel(
            id = groupId,
            subs = ConcurrentHashMap(members.associateWith { GroupSubscriber.Member }),
            settings = settings,
            lobby = fromLobby
        )
    }
}

object GameFactory {

    private fun initTurnOrder(players: Int) = (0 until players).shuffled()

    fun create(settings: Settings, members: Collection<UserId>): GameChannel {

        val gameId = generateId(Type.GAME)
        val ruleBook = RuleBook.from(settings)

        val turnOrder = initTurnOrder(members.size)

        val userToPlayerId: Map<UserId, Int> = members
            .mapIndexed { index, id -> id to index }
            .toMap()

        val players: List<Player> = List(members.size) { index ->
            Player(
                number = index,
                resources = ResourceMap(),
                victoryPoints = 0,
                roadsLeft = settings.maxRoads,
                citiesLeft = settings.maxCities,
                settlementsLeft = settings.maxSettlements
            )
        }

        val board = BoardGenerator.generateBoard(settings)
        val phase = Phase.SETUP
        val turnPlayer = turnOrder.first()

        val snapshot = GameSnapshot(
            rules = ruleBook,
            players = players,
            board = board,
            phase = phase,
            turnOrder = turnOrder,
            turnPlayer = turnPlayer,
            eventHistory = emptyList()
        )

        return GameChannel(
            id = gameId,
            subs = ConcurrentHashMap(members.associateWith { GameSubscriber.Player }),
            clientToPlayerId = userToPlayerId,
            snapshot = snapshot
        )
    }
}
