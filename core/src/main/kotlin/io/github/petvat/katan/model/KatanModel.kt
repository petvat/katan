package io.github.petvat.katan.model

import io.github.petvat.katan.shared.hexlib.EdgeCoordinates
import io.github.petvat.katan.shared.model.board.ExternalBoardManager
import io.github.petvat.katan.shared.model.session.PrivateGameState
import io.github.petvat.katan.shared.model.session.PrivateUserView
import io.github.petvat.katan.shared.model.session.PublicSessionInfo
import io.github.petvat.katan.shared.protocol.dto.BoardView
import io.github.petvat.katan.shared.protocol.dto.RestrictedGroupView


/**
 * Main model of client.
 */
class KatanModel {
    var userInfo: PrivateUserView? = null
    var accessToken: String? = null
    var sessonId: String? = null
    var clientState: ViewState = LoginState(this)
    var groups = mutableListOf<PublicSessionInfo>()
    var group: RestrictedGroupView? = null
    var gameState: PrivateGameState? = null
    var boardManager: ExternalBoardManager? = null

    fun setupBoard(dto: BoardView) {
        boardManager = ExternalBoardManager(dto)
    }

    fun roadBuildOptions(): Set<EdgeCoordinates> {
        return boardManager!!.getRoadFrontier(gameState!!.player)
    }

    fun villageBuildOptions(): Set<EdgeCoordinates> {
        return boardManager!!.getRoadFrontier(gameState!!.player)
    }
}






