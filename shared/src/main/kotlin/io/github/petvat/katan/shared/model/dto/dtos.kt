package io.github.petvat.katan.shared.model.dto

import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.protocol.dto.BoardDTO
import kotlinx.serialization.Serializable


enum class PlayerColor {
    YELLOW, BLUE, RED, WHITE
}

/**
 * Simplified game progress available to players from the perspective of client.
 */
//@Serializable
//data class PrivateGameState(
//    val playerNumber: Int,
//    val otherPlayers: List<OtherPlayerDTO>,
//    val turnOrder: List<Int>,
//    var turnPlayer: Int,
//    val board: BoardDTO
//)

/**
 * Simplified game progress available to players from the perspective of client.
 *
 */
@Serializable
data class GameStateDTO(
    val player: PlayerDTO,
    val otherPlayers: List<PlayerDTO>,
    val turnOrder: List<Int>,
    var turnPlayer: Int,
    val board: BoardDTO
)

/**
 * NOTE: For now, all player data is sent to all players.
 *
 * NOTE: 'var' because reusing this for client logic.
 */
@Serializable
data class PlayerDTO(
    val playerNumber: Int,
    val color: PlayerColor,
    var victoryPoints: Int,
    var resources: ResourceMap,
    var settlementCount: Int,
    var cityCount: Int,
    var roadCount: Int
)

/**
 * Dto for visible player data.
 *
 * NOTE: Color is assigned by client, will not be consistent across clients
 */
@Serializable
data class OtherPlayerDTO(
    val playerNumber: Int,
    var victoryPoints: Int,
    var cardCount: Int,
    var settlementCount: Int,
    var cityCount: Int,
    var roadCount: Int
)

@Serializable
data class PublicUserDTO(
    val id: String,
    var username: String
)

@Serializable
data class PrivateUserDTO(
    val username: String,
    val password: String,
    // Add other stuff
)
