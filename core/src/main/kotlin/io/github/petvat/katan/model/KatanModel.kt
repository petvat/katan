package io.github.petvat.katan.model

import io.github.petvat.katan.controller.RequestController
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.model.board.Player
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.model.session.PrivateGameState
import io.github.petvat.katan.shared.model.session.PrivateUserView
import io.github.petvat.katan.shared.protocol.PermissionLevel
import io.github.petvat.katan.shared.protocol.dto.BoardView
import io.github.petvat.katan.shared.protocol.dto.PrivateGroupView
import io.github.petvat.katan.shared.protocol.dto.PublicGroupView
import io.github.petvat.katan.ui.model.*
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * Main model of client.
 *
 * TODO: God, refact
 */
class KatanModel {

    /**
     * The user details of this player.
     */
    var userInfo: PrivateUserView by propertyNotify(
        PrivateUserView(
            id = "",
            username = "",
            password = ""
        )
    )

    /**
     * This player action token.
     */
    var accessToken: String by propertyNotify("")

    /**
     * This player session ID.
     */
    var sessionId: String by propertyNotify("")

    /**
     * Public groups fetched from server.
     */
    var groups: List<PublicGroupView> by propertyNotify(mutableListOf())

    /**
     * The group this player is currently in.
     */
    var group: PrivateGroupView by propertyNotify(
        PrivateGroupView(
            // dummy data
            id = "",
            clients = mutableMapOf(),
            level = PermissionLevel.GUEST,
            chatLog = mutableListOf(),
            settings = Settings()
        )
    )

    // TODO: More fields, don't store DTOs

    var game: PrivateGameState by propertyNotify(
        PrivateGameState(
            player = Player("", -1, Settings()),
            otherPlayers = emptyList(),
            turnOrder = emptyList(),
            turnPlayer = -1,
            board = BoardView(
                emptyList(),
                mutableListOf(),
                emptyList(),
                HexCoordinates(-1, -1)
            )
        )
    )

    @PublishedApi
    internal val actionsMap = mutableMapOf<KProperty<*>, MutableList<(Any) -> Unit>>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> onPropertyChange(property: KProperty<T>, noinline action: (T) -> Unit) {
        val actions = actionsMap.getOrPut(property) { mutableListOf() } as MutableList<(T) -> Unit>
        actions += action
    }

    inline fun <reified T : Any> propertyNotify(initialValue: T): ReadWriteProperty<KatanModel, T> =
        Delegates.observable(initialValue) { property, _, newValue -> notify(property, newValue) }

    fun notify(property: KProperty<*>, value: Any) {
        actionsMap[property]?.forEach { action -> action(value) }
    }
}






