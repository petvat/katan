package io.github.petvat.katan.model

import com.badlogic.gdx.Game
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.*
import io.github.petvat.katan.shared.model.game.Phase
import io.github.petvat.katan.shared.model.game.PlayerColor
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.game.ResourceMapData


class ClientState {

    lateinit var id: String

    lateinit var name: String

    lateinit var resumeToken: String

    lateinit var lobby: String

    lateinit var group: GroupState


    lateinit var game: GameState

    var chat: ChatState? = null

    val groupSummaries: List<GroupSummary> = mutableListOf()
}


data class ChatState(
    val id: String,
    val members: Map<String, String>,
    val chatLog: List<Pair<String, String>>
)


data class GameState(
    val player: Int,
    val otherPlayers: List<Int>,
    val id: String,
    val colors: Map<Int, PlayerColor>,
    val turnOrder: List<Int>,
    var turnPlayer: Int,
    val resources: ResourceMapData,
    val otherResources: Map<Int, Int>,
    val victoryPoints: Map<Int, Int>,
    val phase: Phase,
    var board: Board

)

fun ChatState.chatMessage(from: String, message: String) =
    this.copy(
        chatLog = chatLog + (from to message)
    )

fun GameState.addDiceRoll(
    resources: ResourceMapData,
    othersResources: Map<Int, Int>,
) =
    this.copy(
        resources = resources,
        otherResources = otherResources
    )


fun GameState.rolledDice(resources: ResourceMapData, othersResources: Map<Int, Int>, moveRobber: Boolean): GameState {
    return this.copy(
        resources = resources,
        otherResources = otherResources.mapValues { (k, _) ->
            othersResources[k]!! // TODO: Check
        },
        phase = if (moveRobber) Phase.MOVE_ROBBER else this.phase
    )
}

fun GameState.addBuilding(
    builder: Int,
    building: BuildKind,
    coordinates: Coordinates,
    victoryPoints: Map<Int, Int>
): GameState {

    val newBoard = when (building) {
        is BuildKind.Road ->
            board.copy(
                paths = board.paths + Edge(
                    coordinates as EdgeCoordinates,
                    Road(building.kind, builder)
                )
            )

        is BuildKind.Village ->
            when (building.kind) {
                VillageKind.SETTLEMENT ->
                    board.copy(
                        intersections = board.intersections + Intersection(
                            coordinates as ICoordinates,
                            Village(VillageKind.SETTLEMENT, builder)
                        )
                    )

                VillageKind.CITY ->
                    board.copy(
                        intersections = board.intersections.map { intersection ->
                            if (intersection.coordinate == coordinates) {
                                intersection.copy(
                                    village = intersection.village.copy(
                                        villageKind = VillageKind.CITY
                                    )
                                )
                            } else intersection
                        }
                    )
            }
    }

    return copy(
        board = newBoard,
        victoryPoints = victoryPoints
    )
}

data class GroupSummary(
    val id: String,
    val numClients: Int,
    val capacity: Int,
)

data class GroupState(
    val groupId: String, // TODO: Move GroupId to shared
    val clients: MutableMap<String, String>, // TODO: ClientData
    val settings: Settings
)


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
//    lateinit var ktxCtx: GameStateDTO
//
//    var turnIndex = 0 // Custom local Game State
//    var gamePhase = 0
//
//
//    fun createGroup(id: String, level: PermissionLevel, settings: Settings) {
//        group = // HACK: NAAH, can't create a DTO like this. That's just silly.
//            PrivateGroupDTO(
//                id,
//                mutableMapOf(sessionId to name),
//                level,
//                chatLog = mutableListOf(),
//                settings
//            )
//    }
//
//    fun join(group: PrivateGroupDTO) {
//        this.group = group
//        this.group.members += sessionId to name
//    }
//
//    fun userJoin(id: String, name: String) {
//        // NOTE: Losing data here!
//        group.members[id] = name
//    }
//
//    fun incrementTurn() {
//        turnIndex += 1 % ktxCtx.turnOrder.size
//        ktxCtx.turnPlayer = ktxCtx.turnOrder[turnIndex]
//    }
//
//    /**
//     * Delta update on dice rolled.
//     */
//    fun diceRolled(playerResource: ResourceMap, otherPlayersResources: Map<Int, ResourceMap>, moveRobber: Boolean) {
//        ktxCtx.player.resources = playerResource
//        ktxCtx.otherPlayers.forEach {
//            it.resources = otherPlayersResources[it.playerNumber]!!
//        }
//        // TODO: add MoveRobber. LocalGameState class?
//    }
//
//    fun newBuilding(playerNumber: Int, building: BuildKind, coordinates: Coordinates, victoryPoints: Boolean = true) {
//        when (building) {
//            is BuildKind.Road -> {
//                ktxCtx.board.paths += EdgeDTO(coordinates as EdgeCoordinates, RoadDTO(building.kind, playerNumber))
//            }
//
//            is BuildKind.Village -> {
//                if (building.kind == VillageKind.SETTLEMENT) {
//                    ktxCtx.board.intersections += IntersectionDTO(
//                        coordinates as ICoordinates,
//                        VillageDTO(building.kind, playerNumber)
//                    )
//                } else if (building.kind == VillageKind.CITY) {
//                    val city = ktxCtx.board.intersections
//                        .find { it.coordinate == coordinates as ICoordinates }!!
//                    city.village.villageKind = VillageKind.CITY
//                }
//
//                // TODO: fix
//                if (victoryPoints) {
//                    ktxCtx.otherPlayers.find { it.playerNumber == playerNumber }?.let { it.victoryPoints++ }
//                    if (ktxCtx.player.playerNumber == playerNumber) {
//                        ktxCtx.player.victoryPoints++
//                    }
//                }
//            }
//        }
//    }
//}







