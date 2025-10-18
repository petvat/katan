package io.github.petvat.katan.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.client.NioKatanClient
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.model.ClientState
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.MTypes
import io.github.petvat.katan.shared.protocol.Request
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


class RequestTracker() {
    private val pendingRequests = ConcurrentHashMap<String, Request>()

    fun track(request: Request) {}

    fun resolve() {}

}

typealias ResponseHandler<R> = (response: R) -> Event


class GameService(val gameState: ClientState) {
    private val logger = KotlinLogging.logger { }

    private var messageCount = AtomicInteger(0)

    lateinit var client: NioKatanClient


    /**
     * Open connection to server.
     * Set client to listen for messages from server.
     *
     * @param host Server hostname/address
     * @param port Server port
     *
     * @return true if the connection was successful
     */
    fun handleConnectClient(host: String?, port: Int?): Boolean {
        client = NioKatanClient()

        val success: Boolean = if (host != null) {
            client.start(address = host, portNumber = port ?: 1234)
        } else {
            client.start(portNumber = port ?: 1234)
        }

        if (!success) {
            return false
        }
        logger.debug { "Init client." }

        listenForResponses()
        return true
    }

    fun handleRegister(name: String) = send { id -> Request.GuestRegister(id, name) }

    fun handleJoin(sessionId: String) = send { id -> Request.Join(id, sessionId) }

    fun handleRollDice() = send { id -> Request.RollDice(id) }

    fun handleBuild(buildKind: BuildKind, coordinates: Coordinates) = send { id ->
        Request.Build(id, buildKind, coordinates)
    }

    fun handleInit() = send { id -> Request.Init(id) }

    fun handleClose() {
        client.close()
    }

//    fun handleGetGroup(pagination: Int) {
//        // forwardRequest(MessageType.GET_GROUPS, Request.Groups(pagination), null)
//    }

    fun handleCreate(settings: Settings) = send { id -> Request.Create(id, settings) }

    fun handleChat(message: String, recipients: Set<String>?) = send { id ->
        Request.Chat(id, message) // gameState.group.clients.values.filter { it != gameState.sessionId }.toSet()
    }

    private fun send(partialRequest: (Int) -> Request) {
        val id = messageCount.incrementAndGet()
        val request = partialRequest(id)
        client.forwardRequest(request)
    }

    private fun listenForResponses() {
        val t = Thread {
            while (true) {
                val message = client.messageQueue.take()
                logger.debug { "Polled message from NIO client message queue." }
                gameState.update(message)
            }
        }
        t.isDaemon = true // Kill on main exit.
        t.start()
    }
}
