package io.github.petvat.katan.shared.protocol.dto

import io.github.petvat.katan.shared.model.board.*

fun Board.toDto() =
    BoardDTO(
        tiles,
        intersections.map { it.toDto() },
        paths.map { it.toDto() },
        robberLocation
    )

fun Village.toDto() = VillageDTO(villageKind, owner)

fun Road.toDto() = RoadDTO(roadKind, owner)

fun Edge.toDto() = EdgeDTO(coordinate, road.toDto())

fun Intersection.toDto() = IntersectionDTO(coordinate, village.toDto())

//fun Player.fromDomain() = PlayerDTO(
//    playerNumber = playerNumber,
//    resources = inventory,
//    settlementCount = settlementCount,
//    cityCount = cityCount,
//    roadCount = roadCount,
//    victoryPoints = victoryPoints,
//    color = color
//)
