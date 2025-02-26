package io.github.petvat.katan.server.api

import io.github.petvat.katan.server.api.action.*
import io.github.petvat.katan.server.group.Game
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Request
import io.github.petvat.katan.shared.protocol.Response


enum class GameStates {
    SETUP, ROLL_DICE, STEAL, MOVE_ROBBER, BUILD_TRADE
}
