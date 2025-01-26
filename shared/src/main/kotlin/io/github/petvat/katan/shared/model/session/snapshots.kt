package io.github.petvat.katan.shared.model.session

import io.github.petvat.katan.shared.model.board.Player
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.dto.BoardView
import kotlinx.serialization.Serializable


/**
 * Simplified game progress available to players from the perspective of client.
 */
@Serializable
data class PrivateGameState(
    val player: Player,
    val otherPlayers: List<OtherPlayerView>,
    val turnOrder: List<Int>,
    var turnPlayer: Int,
    val board: BoardView
)

/**
 * Simplified game progress available to all users.
 */
@Serializable
data class PublicGameState(
    val gameId: String,
    val players: List<OtherPlayerView>,
    val turnOrder: List<Int>,
    val board: BoardView
)

/**
 *
 * Public snapshot.
 */
@Serializable
data class PublicSessionInfo(
    val groupId: String,
    val hostId: Int,
    val settings: Settings,
    val users: Set<PublicUserView>,
    val joinable: Boolean
)

/**
 * Private snapshot for session members.
 *
 * Replaced by Group view
 */
@Serializable
data class PrivateSessionInfo(
    val otherUsers: Set<PublicUserView>,
    val settings: Settings,
)

/**
 * Dto for visible player data.
 *
 * NOTE: Color is assigned by client, will not be consistent across clients
 */
@Serializable
data class OtherPlayerView(
    val id: String,
    val playerNumber: Int,
    var victoryPoints: Int,
    var cardCount: Int,
    var settlementCount: Int,
    var cityCount: Int,
    var roadCount: Int
)

@Serializable
data class PublicUserView(
    val id: String,
    var username: String
)

@Serializable
data class PrivateUserView(
    val id: String,
    val username: String,
    val password: String,
    // Add other stuff
)
