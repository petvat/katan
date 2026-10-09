package io.github.petvat.katan.model

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.event.ConnectionLostEvent
import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.model.command.ChatCommandsImpl
import io.github.petvat.katan.model.command.KatanCommands
import io.github.petvat.katan.model.command.GameCommandsImpl
import io.github.petvat.katan.model.command.GroupCommandsImpl
import io.github.petvat.katan.model.command.LobbyCommandsImpl
import io.github.petvat.katan.model.net.ConnectionLostException
import io.github.petvat.katan.model.net.InBoundRouter
import io.github.petvat.katan.model.net.KatanChannel
import io.github.petvat.katan.model.net.MessageChannel
import io.github.petvat.katan.model.net.RequestTracker
import io.github.petvat.katan.model.state.ClientState
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.hexlib.EdgeCoord
import io.github.petvat.katan.shared.hexlib.NodeCoord
import io.github.petvat.katan.shared.model.board.*
import io.github.petvat.katan.shared.model.game.Phase
import io.github.petvat.katan.shared.model.game.PlayerColor
import io.github.petvat.katan.shared.model.game.ResourceMap
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.Request
import java.util.concurrent.TimeUnit


class KatanClient(
    private val events: EventSystem,
    val state: ClientState = ClientState(),
    private val channel: MessageChannel = KatanChannel()
) {
    private val logger = KotlinLogging.logger { }
    private val tracker = RequestTracker(channel)
    private val router = InBoundRouter(state, tracker, events) { channelId ->
        // Gap in a channel's delta stream: pull a fresh full snapshot.
        logger.warn { "channelSeq gap on '$channelId'. Requesting resync" }
        tracker.send(channelId, Request.Init)
    }

    val commands = KatanCommands(
        game = GameCommandsImpl(tracker, state),
        lobby = LobbyCommandsImpl(tracker, state),
        chat = ChatCommandsImpl(tracker, state),
        group = GroupCommandsImpl(tracker, state)
    )

    @Volatile
    private var running = false
    private var pump: Thread? = null

    fun connect(host: String?, port: Int?): Boolean {
        if (!channel.connect(host ?: "localhost", port ?: 1234)) return false
        startPump()
        return true
    }

    /**
     * TODO: Add this before dying.
     */
    fun disconnect() {
        running = false
        channel.shutdown()
        tracker.failAll(ConnectionLostException("Client disconnected"))
        state.reset()
    }

    private fun startPump() {
        if (running) return
        running = true
        pump = Thread(::pumpLoop, "katan-inbound").apply {
            isDaemon = true
        }.also { it.start() }
    }

    private fun pumpLoop() {
        val queue = channel.incoming()
        while (running) {
            val message = try {
                queue.poll(POLL_INTERVAL, TimeUnit.MILLISECONDS)

            } catch (_: InterruptedException) {
                break
            }
            if (message == null) {
                tracker.sweepExpired()
                if (!channel.connected()) {
                    onConnectionLost()
                    break
                }
                continue
            }
            try {
                logger.debug { "Received message: $message" }
                router.route(message)
            } catch (t: Throwable) {
                logger.error(t) { "Routing failed for ${message.payload::class.simpleName}" }
            }
        }
    }

    private fun onConnectionLost() {
        running = false
        tracker.failAll(ConnectionLostException())
        events.fire(ConnectionLostEvent) // VM decides: menu / reconnect via auth.resumeToken
    }

    companion object {
        const val POLL_INTERVAL = 250L
    }
}

//
//data class LobbyState(
//    val id: String,
//    val groups: Map<String, GroupSummary>
//)
//
//
//data class ChatState(
//    val id: String,
//    val members: List<String>,
//    val chatLog: List<Pair<String, String>>
//)


//data class GroupSummary(
//    val id: String,
//    val numClients: Int,
//    val capacity: Int,
//)
//
//data class GroupState(
//    val groupId: String, // TODO: Move GroupId to shared
//    val clients: MutableMap<String, String>, // TODO: ClientData
//    val settings: Settings
//)


/**
 * Main gameState of client.
 *
 */
//class KatanModel {
//
//    /**
//     * The user details of this player.
//     */
//    lateinit var userInfo: PrivateUserDTO
//
//    /**
//     * This player action token.
//     */
//    lateinit var accessToken: String
//
//    /**
//     * This player session ID.
//     */
//    lateinit var sessionId: String
//
//    lateinit var name: String // NOTE: Temporary!
//
//    /**
//     * Public groups fetched from server.
//     */
//    var groups: MutableList<PublicGroupDTO> = mutableListOf()
//
//    /**
//     * The group this player is currently in.
//     */
//    lateinit var group: PrivateGroupDTO
//
//    lateinit var ktxCtx: GameStateDTO
//
//    var turnIndex = 0 // Custom local Game State
//    var gamePhase = 0
//
//
//    fun createGroup(clientId: String, level: PermissionLevel, settings: Settings) {
//        group = // HACK: NAAH, can't create a DTO like this. That's just silly.
//            PrivateGroupDTO(
//                clientId,
//                mutableMapOf(sessionId to name),
//                level,
//                chatLog = mutableListOf(),
//                settings
//            )
//    }
//
//    fun join(group: PrivateGroupDTO) {
//        this.group = group
//        this.group.members += sessionId to name
//    }
//
//    fun userJoin(clientId: String, name: String) {
//        // NOTE: Losing data here!
//        group.members[clientId] = name
//    }
//
//    fun incrementTurn() {
//        turnIndex += 1 % ktxCtx.turnOrder.size
//        ktxCtx.turnPlayer = ktxCtx.turnOrder[turnIndex]
//    }
//
//    /**
//     * Delta update on dice rolled.
//     */
//    fun diceRolled(playerResource: ResourceMap, otherPlayersResources: Map<Int, ResourceMap>, moveRobber: Boolean) {
//        ktxCtx.player.resources = playerResource
//        ktxCtx.otherPlayers.forEach {
//            it.resources = otherPlayersResources[it.playerNumber]!!
//        }
//        // TODO: add MoveRobber. LocalGameState class?
//    }
//
//    fun newBuilding(playerNumber: Int, building: BuildKind, coordinates: Coordinates, victoryPoints: Boolean = true) {
//        when (building) {
//            is BuildKind.Road -> {
//                ktxCtx.board.paths += EdgeDTO(coordinates as EdgeCoordinates, RoadDTO(building.kind, playerNumber))
//            }
//
//            is BuildKind.Village -> {
//                if (building.kind == VillageKind.SETTLEMENT) {
//                    ktxCtx.board.intersections += IntersectionDTO(
//                        coordinates as ICoordinates,
//                        VillageDTO(building.kind, playerNumber)
//                    )
//                } else if (building.kind == VillageKind.CITY) {
//                    val city = ktxCtx.board.intersections
//                        .find { it.coordinate == coordinates as ICoordinates }!!
//                    city.village.villageKind = VillageKind.CITY
//                }
//
//                // TODO: fix
//                if (victoryPoints) {
//                    ktxCtx.otherPlayers.find { it.playerNumber == playerNumber }?.let { it.victoryPoints++ }
//                    if (ktxCtx.player.playerNumber == playerNumber) {
//                        ktxCtx.player.victoryPoints++
//                    }
//                }
//            }
//        }
//    }
//}







