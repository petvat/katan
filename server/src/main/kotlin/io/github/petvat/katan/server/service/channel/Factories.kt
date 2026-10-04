package io.github.petvat.katan.server.service.channel

import io.github.petvat.katan.server.service.client.ClientId
import io.github.petvat.katan.server.service.engine.GameState
import io.github.petvat.katan.shared.model.game.Phase
import io.github.petvat.katan.server.service.engine.Player
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.model.board.BoardGenerator
import io.github.petvat.katan.shared.model.game.PlayerColor
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.RuleBook
import io.github.petvat.katan.shared.model.game.Settings
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap


enum class IdType(val prefix: String) {
    SYSTEM("sys"),
    LOBBY("lobby"),
    GROUP("group"),
    CHAT("chat"),
    GAME("game"),

    CLIENT("client"),
    USER("user"),
    GUEST("guest"),
    ADMIN("admin")
}

fun generateId(type: IdType) = type.prefix + ": " + UUID.randomUUID().toString()

fun generateGroupId() = ChannelId("group:${UUID.randomUUID()}")
fun generateChatId() = ChannelId("chat:${UUID.randomUUID()}")
fun generateClientId(type: IdType) = ClientId("${type.prefix}:${UUID.randomUUID()}")
fun generateUserId(type: IdType) = UserId("${type.prefix}:${UUID.randomUUID()}")

object GroupFactory {
    fun create(settings: Settings, members: Collection<UserId>, fromLobby: LobbyChannel): GroupChannel {
        val groupId = generateGroupId()
        return GroupChannel(
            id = groupId,
            subs = ConcurrentHashMap(members.associateWith { GroupSubscriber.Member }),
            settings = settings,
            lobby = fromLobby,
            chat = ChatChannel(
                generateChatId(),
                ConcurrentHashMap(members.associateWith { ChatSubscriber.Member }),
                mutableListOf()
            )
        )
    }
}

object GameFactory {

    private fun initTurnOrder(players: Int) = (0 until players).shuffled()
    private fun assignColors(numPlayers: Int): Map<Int, PlayerColor> {
        return ((1..numPlayers).toList().shuffled() zip PlayerColor.entries.take(numPlayers).shuffled()).toMap()
    }


    fun create(settings: Settings, members: Collection<UserId>, chat: ChatChannel?): GameChannel {

        val gameId = generateId(IdType.GAME)

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
        val snapshot = GameState(
            rules = RuleBook.from(settings),
            players = players,
            board = BoardGenerator.generateBoard(settings),
            phase = Phase.SETUP,
            turnOrder = turnOrder,
            turnPlayer = turnOrder.first(),
            eventHistory = emptyList(),
            colors = assignColors(members.size)
        )

        return GameChannel(
            id = ChannelId(gameId),
            subs = ConcurrentHashMap(members.associateWith { GameSubscriber.Player }),
            userToPlayerId = userToPlayerId,
            snapshot = snapshot,
            chat = chat
        )
    }
}
