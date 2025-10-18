package io.github.petvat.katan.model

import io.github.petvat.katan.controller.*
import io.github.petvat.katan.event.*
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.PermissionLevel
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response
import io.github.petvat.katan.shared.protocol.dto.*
import kotlin.reflect.KClass


class ClientState {
    private val eventSystem = EventSystem()

    lateinit var sessionId: String

    lateinit var name: String

    lateinit var groupModel: GroupState

    lateinit var groupModels: MutableList<GroupState>

    lateinit var gameModel: GameState

    private val handlers = mapOf(
        handleWith<Response.DiceRolled> { r, _ -> rollDice(r) },
        handleWith<Response.Chat> { r, _ -> chat(r) },
        handleWith<Response.Joined> { r, _ -> join(r) },
    )

    private inline fun <reified T : Response> handleWith(
        crossinline handler: (T, ClientState) -> Event
    ): Pair<KClass<out Response>, (Response) -> Event> {
        return T::class to { r -> handler(r as T, this) }
    }

    fun update(response: Response) {
        val handler = handlers[response::class]!!
        val event = handler(response)
        eventSystem.fire(event)
    }
    
    // FIX: HANDLERS

    private fun login(sessionId: String, name: String): Event {
        this.sessionId = sessionId
        this.name = name
        return LoginEvent
    }

    private fun join(response: Response.Joined): Event {
        groupModel = response.groupDTO.toClientModel()
        return JoinEvent
    }

    private fun init(response: Response.Init): Event {
        gameModel = response.privateGameState.toClientModel()
        return InitEvent
    }

    private fun error(description: String?, code: ErrorCode?) = ErrorEvent(description ?: "No description.", code)


    private fun endSetup(): Event {
        TODO()
    }

    private fun endTurn(): Event {
        // TODO: INCREMENT TURN
        return NextTurnEvent(gameModel.turnPlayer)
    }

    private fun rollDice(response: Response.DiceRolled): Event {
        gameModel.player.resources = response.resources
        gameModel.otherPlayers.forEach {
            it.resources = response.othersResources[it.playerNumber]!!
        }
        // TODO: add MoveRobber. LocalGameState class?

        return RolledDiceEvent(
            response.roll1,
            response.roll2,
            response.moveRobber,
            response.resources,
            response.othersResources.mapValues { it.value.count() }
        )
    }

    private fun chat(response: Response.Chat): Event {
        val from = response.from
        val message = response.message
        groupModel.chatLog += groupModel.clients[from]!! to message
        return ChatEvent(groupModel.chatLog.last().first, groupModel.chatLog.last().second) // !
    }

    private fun build(
        builder: Int,
        coordinates: Coordinates,
        building: BuildKind,
        victoryPoints: Boolean = true
    ): Event {
        when (building) {
            is BuildKind.Road -> {
                gameModel.board.paths += EdgeDTO(coordinates as EdgeCoordinates, RoadDTO(building.kind, builder))
            }

            is BuildKind.Village -> {
                if (building.kind == VillageKind.SETTLEMENT) {
                    gameModel.board.intersections += IntersectionDTO(
                        coordinates as ICoordinates,
                        VillageDTO(building.kind, builder)
                    )
                } else if (building.kind == VillageKind.CITY) {
                    val city = gameModel.board.intersections
                        .find { it.coordinate == coordinates as ICoordinates }!!
                    city.village.villageKind = VillageKind.CITY
                }

                if (victoryPoints) {
                    gameModel.otherPlayers.find { it.playerNumber == builder }?.let { it.victoryPoints++ }
                    if (gameModel.player.playerNumber == builder) {
                        gameModel.player.victoryPoints++
                    }
                }
            }
        }
        return BuildEvent(builder, building, coordinates)
    }

    /**
     * Make private.
     */
    fun diceRolled(playerResource: ResourceMap, otherPlayersResources: Map<Int, ResourceMap>, moveRobber: Boolean) {
        gameModel.player.resources = playerResource
        gameModel.otherPlayers.forEach {
            it.resources = otherPlayersResources[it.playerNumber]!!
        }
        // TODO: add MoveRobber. LocalGameState class?
    }


    /**
     * Make private.
     */
//    fun newBuilding(playerNumber: Int, building: BuildKind, coordinates: Coordinates, victoryPoints: Boolean = true) {
//        when (building) {
//            is BuildKind.Road -> {
//                gameModel.board.paths += EdgeDTO(coordinates as EdgeCoordinates, RoadDTO(building.kind, playerNumber))
//            }
//
//            is BuildKind.Village -> {
//                if (building.kind == VillageKind.SETTLEMENT) {
//                    gameModel.board.intersections += IntersectionDTO(
//                        coordinates as ICoordinates,
//                        VillageDTO(building.kind, playerNumber)
//                    )
//                } else if (building.kind == VillageKind.CITY) {
//                    val city = gameModel.board.intersections
//                        .find { it.coordinate == coordinates as ICoordinates }!!
//                    city.village.villageKind = VillageKind.CITY
//                }
//
//                // TODO: fix
//                if (victoryPoints) {
//                    gameModel.otherPlayers.find { it.playerNumber == playerNumber }?.let { it.victoryPoints++ }
//                    if (gameModel.player.playerNumber == playerNumber) {
//                        gameModel.player.victoryPoints++
//                    }
//                }
//            }
//        }
//    }
}


fun GameStateDTO.toClientModel() = GameState(
    player = this.player,
    otherPlayers = this.otherPlayers,
    turnOrder = this.turnOrder,
    turnPlayer = this.turnPlayer,
    board = this.board
)

fun PrivateUserDTO.toClientModel() = UserModel

fun PrivateGroupDTO.toClientModel() = GroupState(
    groupId = this.id,
    chatLog = this.chatLog,
    clients = this.clients,
    level = this.level,
    settings = this.settings
)

data class ChatLogModel(
    val log: MutableList<Pair<String, String>>
) {
    operator fun plusAssign(message: Pair<String, String>) {
        log += message
    }
}

data class GameState(
    val player: PlayerDTO,
    val otherPlayers: List<PlayerDTO>,
    val turnOrder: List<Int>,
    var turnPlayer: Int,
    val board: BoardDTO
)

data class GroupState(
    val groupId: String, // TODO: Move GroupId to shared
    val chatLog: MutableList<Pair<String, String>>,
    val clients: MutableMap<String, String>,
    val level: PermissionLevel,
    val settings: Settings
)

data object UserModel


/**
 * Main gameState of client.
 *
 */
//class KatanModel {
//
//    /**
//     * The user details of this player.
//     */
//    lateinit var userInfo: PrivateUserDTO
//
//    /**
//     * This player action token.
//     */
//    lateinit var accessToken: String
//
//    /**
//     * This player session ID.
//     */
//    lateinit var sessionId: String
//
//    lateinit var name: String // NOTE: Temporary!
//
//    /**
//     * Public groups fetched from server.
//     */
//    var groups: MutableList<PublicGroupDTO> = mutableListOf()
//
//    /**
//     * The group this player is currently in.
//     */
//    lateinit var group: PrivateGroupDTO
//
//    lateinit var game: GameStateDTO
//
//    var turnIndex = 0 // Custom local Game State
//    var gamePhase = 0
//
//
//    fun createGroup(groupId: String, level: PermissionLevel, settings: Settings) {
//        group = // HACK: NAAH, can't create a DTO like this. That's just silly.
//            PrivateGroupDTO(
//                groupId,
//                mutableMapOf(sessionId to name),
//                level,
//                chatLog = mutableListOf(),
//                settings
//            )
//    }
//
//    fun join(group: PrivateGroupDTO) {
//        this.group = group
//        this.group.clients += sessionId to name
//    }
//
//    fun userJoin(id: String, name: String) {
//        // NOTE: Losing data here!
//        group.clients[id] = name
//    }
//
//    fun incrementTurn() {
//        turnIndex += 1 % game.turnOrder.size
//        game.turnPlayer = game.turnOrder[turnIndex]
//    }
//
//    /**
//     * Delta update on dice rolled.
//     */
//    fun diceRolled(playerResource: ResourceMap, otherPlayersResources: Map<Int, ResourceMap>, moveRobber: Boolean) {
//        game.player.resources = playerResource
//        game.otherPlayers.forEach {
//            it.resources = otherPlayersResources[it.playerNumber]!!
//        }
//        // TODO: add MoveRobber. LocalGameState class?
//    }
//
//    fun newBuilding(playerNumber: Int, building: BuildKind, coordinates: Coordinates, victoryPoints: Boolean = true) {
//        when (building) {
//            is BuildKind.Road -> {
//                game.board.paths += EdgeDTO(coordinates as EdgeCoordinates, RoadDTO(building.kind, playerNumber))
//            }
//
//            is BuildKind.Village -> {
//                if (building.kind == VillageKind.SETTLEMENT) {
//                    game.board.intersections += IntersectionDTO(
//                        coordinates as ICoordinates,
//                        VillageDTO(building.kind, playerNumber)
//                    )
//                } else if (building.kind == VillageKind.CITY) {
//                    val city = game.board.intersections
//                        .find { it.coordinate == coordinates as ICoordinates }!!
//                    city.village.villageKind = VillageKind.CITY
//                }
//
//                // TODO: fix
//                if (victoryPoints) {
//                    game.otherPlayers.find { it.playerNumber == playerNumber }?.let { it.victoryPoints++ }
//                    if (game.player.playerNumber == playerNumber) {
//                        game.player.victoryPoints++
//                    }
//                }
//            }
//        }
//    }
//}







