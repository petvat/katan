package io.github.petvat.katan.shared.model.dto

import io.github.petvat.katan.shared.model.board.*
import io.github.petvat.katan.shared.protocol.dto.*

fun Board.fromDomain() =
    BoardDTO(
        tiles,
        intersections.map { it.fromDomain() }.toMutableList(),
        paths.map { it.fromDomain() }.toMutableList(),
        robberLocation
    )

fun Village.fromDomain() = VillageDTO(villageKind, owner.playerNumber)

fun Road.fromDomain() = RoadDTO(roadKind, owner.playerNumber)

fun Edge.fromDomain() = EdgeDTO(coordinate, road.fromDomain())

fun Intersection.fromDomain() = IntersectionDTO(coordinate, village.fromDomain())

fun Player.fromDomain() = PlayerDTO(
    playerNumber = playerNumber,
    resources = inventory,
    settlementCount = settlementCount,
    cityCount = cityCount,
    roadCount = roadCount,
    victoryPoints = victoryPoints,
    color = color
)
