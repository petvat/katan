package io.github.petvat.katan.server.service.command

import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind

data object RollDice : GameCommand

data class Build(
    val buildKind: BuildKind,
    val coordinates: Coordinates,
) : GameCommand


data class BuildInitSettlment(
    val coordinates: Coordinates,
) : GameCommand


data object EndTurn : GameCommand


