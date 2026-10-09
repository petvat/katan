package io.github.petvat.katan.shared.model.game

import io.github.petvat.katan.shared.protocol.dto.UserData
import kotlinx.serialization.Serializable


/**
 * Other relevant data for a game.
 */
@Serializable
data class GameMeta(
    val playerUserData: Map<Int, UserData>
)
