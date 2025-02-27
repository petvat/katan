package io.github.petvat.katan.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.client.*
import io.github.petvat.katan.event.ConnectionEvent
import io.github.petvat.katan.event.EventBus
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.Request
import io.github.petvat.katan.ui.cli.SimpleCliView
import java.util.concurrent.atomic.AtomicInteger

interface RequestController {

    fun handleInit()

    fun handleCreate(settings: Settings)

    fun handleJoin(sessionId: String)

    fun handleGetGroup(pagination: Int)

    fun connectClient(host: String?, port: Int?): Boolean

    fun handleChat(message: String, recipients: Set<String>?)

    fun handleLogin(username: String, password: String)

    fun handleClose()

    fun handleRollDice()

    fun handleBuild(buildKind: BuildKind, coordinates: Coordinates)

    fun handleRegister(name: String)
}

/**
 * The main request controller. Should only exist one.
 *
 * Handles user input by creating requests and forwarding them to client socket.
 */
class NioController(
    private val responseController: ResponseProcessor
) : RequestController {
    private val logger = KotlinLogging.logger { }

    private lateinit var client: NioKatanClient

    private var messageCount = AtomicInteger(0)

    /**
     * Open connection to server.
     * Set client to listen for messages from server.
     *
     * @param host Server hostname/address
     * @param port Server port
     */
    override fun connectClient(host: String?, port: Int?): Boolean {
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

        EventBus.fire(ConnectionEvent) // Maybe a little hacky?

        // Initializes the response middleware
        val responseHandler = Thread(ResponseDispatcher(client, responseController))
        responseHandler.isDaemon = true // Kill on main exit.
        responseHandler.start()

        return true
    }

    /**
     * Hmm, could be done better.
     */
    private fun nextRequest() = messageCount.incrementAndGet()


    private fun forwardRequest(data: Request) {
        responseController.requests += data // NOTE: Currenlty leaking.
        client.forwardRequest(data)
    }

    // NOTE: GUEST
    override fun handleRegister(name: String) {
        forwardRequest(Request.GuestRegister(nextRequest(), name))
    }

    override fun handleJoin(sessionId: String) {
        forwardRequest(Request.Join(nextRequest(), sessionId))
    }

    override fun handleLogin(username: String, password: String) {
        TODO()
    }

    override fun handleClose() {
        client.close()
    }

    override fun handleRollDice() {
        forwardRequest(Request.RollDice(nextRequest()))
    }

    override fun handleBuild(buildKind: BuildKind, coordinates: Coordinates) {
        forwardRequest(Request.Build(nextRequest(), buildKind, coordinates))
    }

    override fun handleGetGroup(pagination: Int) {
        // forwardRequest(MessageType.GET_GROUPS, Request.Groups(pagination), null)
    }

    override fun handleInit() {
        forwardRequest(Request.Init(nextRequest()))
    }

    override fun handleCreate(settings: Settings) {
        forwardRequest(Request.Create(nextRequest(), settings))
    }

    override fun handleChat(message: String, recipients: Set<String>?) {
        forwardRequest(
            Request.Chat(nextRequest(), message) // model.group.clients.values.filter { it != model.sessionId }.toSet()
        )
    }
}

// NOTE: This is totally Useless!
// TODO: REMOVE THIS
//class KtxInputController(
//    private val base: MainController,
//    val view: KtxKatan
//) : RequestController {
//    override fun handleInit() {
//        base.handleInit()
//    }
//
//    override fun handleCreate(settings: Settings) {
//        base.handleCreate(settings)
//    }
//
//    override fun handleJoin(sessionId: String) {
//        base.handleJoin(sessionId)
//    }
//
//    override fun handleGetGroup(pagination: Int) {
//        base.handleGetGroup(pagination)
//    }
//
//    override fun connectClient(host: String?, port: Int?): Boolean {
//        return base.connectClient(host, port)
//    }
//
//    override fun handleChat(message: String, recipients: Set<String>?) {
//        base.handleChat(message, null)
//    }
//
//    override fun handleLogin(username: String, password: String) {
//        base.handleLogin(username, password)
//    }
//
//    override fun handleClose() {
//        base.handleClose()
//    }
//
//    override fun handleRollDice() {
//        base.handleRollDice()
//    }
//
//    override fun handleBuild(buildKind: BuildKind, coordinates: Coordinates) {
//        base.handleBuild(buildKind, coordinates)
//    }
//
//    override fun handleRegister(name: String) {
//        base.handleRegister(name)
//    }
//
//}

/**
 * TODO: Update so that it works.
 *
 * Simple CLI Input Controller.
 * Listen for requests from the console. Does not keep track of client state and may therefore suggest
 * impossible requests.
 *
 * @property run Starts listening
 */
class SimpleCliInputController(
    private val base: NioController,
    val view: SimpleCliView,
) {
    val logger = KotlinLogging.logger { }

    fun connectClient(): Boolean {
        val addr = view.promptResponse("Address: ")
        val port = 1024
        return base.connectClient(null, null)
    }

    fun handleLogin() {
        val username = view.promptResponse("Enter Username: ")
        val password = view.promptResponse("Enter Password: ")

        base.handleLogin(username, password)
        view.prompt("Login request sent.")
    }

    fun handleJoin() {
        val groupId = view.promptResponse("Type in group ID of the group you'd want to join.")

        base.handleJoin(groupId)
        view.prompt("Join request sent.")
    }

    fun handelGetGroups() {
        val page = view.promptResponse("Enter pagination value:").trim()

        base.handleGetGroup(page.toIntOrNull() ?: 10)
        view.prompt("Get groups request sent.")
    }

    fun handleCreate() {
        val default = view.promptResponse("Use default settings (Y/N)").trim()

        if (default == "N") {
            view.prompt("Custom option not implemented.")
        }
        base.handleCreate(Settings())
        view.prompt("Create group request sent.")

    }

    fun handleChat() {
        val recipient = view.promptResponse("Type in the ID of the recipient or 'all':").trim()
        val message = view.promptResponse("Type your message:").trim()

//        base.handleChat(
//            message,
//            base.model.gameViewModel!!.otherPlayers.map { p -> p.id }.toSet() + base.model.gameViewModel!!.thisPlayer.id
//        )
        view.prompt("Chat request sent.")
    }

    fun handleClose() {
        base.handleClose()
    }

}

