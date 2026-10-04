package io.github.petvat.katan.shared.protocol.dto

import io.github.petvat.katan.shared.model.board.Board
import io.github.petvat.katan.shared.model.board.Edge
import io.github.petvat.katan.shared.model.board.Node
import io.github.petvat.katan.shared.model.board.Road
import io.github.petvat.katan.shared.model.board.Village


fun VillageDTO.toDomain() = Village(villageKind, owner)
fun RoadDTO.toDomain() = Road(roadKind, owner)
fun IntersectionDTO.toDomain() = Node(coordinate, village.toDomain())
fun EdgeDTO.toDomain() = Edge(coordinate, road.toDomain())

fun BoardDTO.toDomain(): Board = Board(
    tiles = tiles.toMutableList(),
    intersections = intersections.map { it.toDomain() },
    paths = paths.map { it.toDomain() },
    robberLocation = robberLocation
)

//fun PublicPlayer.toDomain() = PlayerPublic(number, resourceCardCount, devCardCount, victoryPointCount)
//
//fun PrivatePlayer.toDomain() = PlayerState(number, resources, victoryPointCount, roadsLeft, citiesLeft, settlementsLeft)
//
//fun TradeDTO.toDomain() = Trade(tradeId, initiator, targetPlayers, offer, inReturn)
//
//fun ParticipantGameSnapshot.toDomain() = GameSnapshot(
//    player = player.toDomain(),
//    board = board.toDomain(),
//    otherPlayers = otherPlayers.map { it.toDomain() },
//    phase = phase,
//    turnOrder = turnOrder,
//    turnPlayer = turnPlayer,
//    ongoingTrades = ongoingTrades.map { it.toDomain() }
//)
//
//fun SpectatorGameSnapshot.toDomain() = GameSnapshot(
//    player = null, // structural: a spectator snapshot has no private hand
//    board = board.toDomain(),
//    otherPlayers = players.map { it.toDomain() },
//    phase = phase,
//    turnOrder = turnOrder,
//    turnPlayer = turnPlayer,
//    ongoingTrades = ongoingTrades.map { it.toDomain() }
//)
//
//fun PublicGroup.toDomain() = GroupExternal(id, numClients, maxClients, mode)
//fun PrivateGroup.toDomain() = GroupInternal(id, clients, settings)

// ===== domain -> DTO (server) =====
// fun Board.toDto(): BoardDTO = ... (inverse of the above)
// fun ResourceMap.toDto(): ResourceMapData  <- move this out of Dtos.kt
