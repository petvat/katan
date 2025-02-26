package io.github.petvat.core.model

import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.Player
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.session.PrivateGameState
import io.github.petvat.katan.shared.model.session.PrivateUserDTO
import io.github.petvat.katan.shared.protocol.PermissionLevel
import io.github.petvat.katan.shared.protocol.dto.*

/**
 * Main model of client.
 *
 */
class KatanModel {

    /**
     * The user details of this player.
     */
    var userInfo =
        PrivateUserDTO(
            username = "",
            password = ""
        )

    /**
     * This player action token.
     */
    var accessToken: String = ""

    /**
     * This player session ID.
     */
    var sessionId: String = ""

    /**
     * Public groups fetched from server.
     */
    var groups: MutableList<PublicGroupDTO> = mutableListOf()

    /**
     * The group this player is currently in.
     */
    var group = PrivateGroupDTO(
        // dummy data
        id = "",
        clients = mutableMapOf(),
        level = PermissionLevel.GUEST,
        chatLog = mutableListOf(),
        settings = Settings()

    )

    // TODO: More fields, don't store DTOs

    var game = PrivateGameState(
        player = Player("", -1, Settings()),
        otherPlayers = emptyList(),
        turnOrder = emptyList(),
        turnPlayer = -1,
        board = BoardDTO(
            emptyList(),
            mutableListOf(),
            mutableListOf(),
            HexCoordinates(-1, -1)
        )

    )

    var turnIndex = 0 // Custom local Game State
    var gamePhase = 0


    fun createGroup(groupId: String, level: PermissionLevel, settings: Settings) {
        group =
            PrivateGroupDTO(
                groupId,
                mutableMapOf(sessionId to "PLACEHOLDER"),
                level,
                chatLog = mutableListOf(),
                settings
            )
    }

    fun userJoin(id: String, name: String) {
        // NOTE: Losing data here!
        group.clients[id] = name
    }

    fun incrementTurn() {
        turnIndex += 1 % game.turnOrder.size
        game.turnPlayer = game.turnOrder[turnIndex]
    }

    /**
     * Delta update on dice rolled.
     */
    fun diceRolled(playerResource: ResourceMap, otherPlayersResources: Map<Int, ResourceMap>, moveRobber: Boolean) {
        game.player.inventory = playerResource
        game.otherPlayers.forEach {
            it.cardCount = otherPlayersResources[it.playerNumber]!!.count()
        }
        // TODO: add MoveRobber. LocalGameState class?
    }

    fun newBuilding(playerNumber: Int, building: BuildKind, coordinates: Coordinates, victoryPoints: Boolean = true) {
        when (building) {
            is BuildKind.Road -> {
                game.board.paths += EdgeDTO(coordinates as EdgeCoordinates, RoadDTO(building.kind, playerNumber))
            }

            is BuildKind.Village -> {
                if (building.kind == VillageKind.SETTLEMENT) {
                    game.board.intersections += IntersectionDTO(
                        coordinates as ICoordinates,
                        VillageDTO(building.kind, playerNumber)
                    )
                } else if (building.kind == VillageKind.CITY) {
                    val city = game.board.intersections
                        .find { it.coordinate == coordinates as ICoordinates }!!
                    city.village.villageKind = VillageKind.CITY
                }

                // TODO: fix
                if (victoryPoints) {
                    game.otherPlayers.find { it.playerNumber == playerNumber }?.let { it.victoryPoints++ }
                    if (game.player.playerNumber == playerNumber) {
                        game.player.victoryPoints++
                    }
                }
            }
        }
    }
}






