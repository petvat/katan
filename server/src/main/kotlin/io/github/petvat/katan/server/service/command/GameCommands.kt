package io.github.petvat.katan.server.service.command

import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.ResourceMap

data object RollDice : GameCommand

data class Build(
    val buildKind: BuildKind,
    val coordinates: Coordinates,
) : GameCommand


data class BuildInitSettlment(
    val coordinates: Coordinates,
) : GameCommand


data object EndTurn : GameCommand


data class InitTrade(
    val targets: Set<Int>,
    val offer: ResourceMap,
    val inReturn: ResourceMap
) : GameCommand

data class RespondTrade(
    val tradeId: Int,
    val accept: Boolean
) : GameCommand

