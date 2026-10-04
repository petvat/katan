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

fun Node.toDto() = IntersectionDTO(coordinate, village.toDto())
