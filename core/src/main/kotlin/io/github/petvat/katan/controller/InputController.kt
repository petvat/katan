package io.github.petvat.katan.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.client.*
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.shared.hexlib.HexCoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.Settings
import io.github.petvat.katan.shared.protocol.*
import io.github.petvat.katan.shared.protocol.dto.ActionRequest
import io.github.petvat.katan.shared.protocol.dto.Request
import io.github.petvat.katan.ui.cli.SimpleCliView
import io.github.petvat.katan.ui.ktx.KtxKatan

/**
 * The main request controller. Should only exist one.
 *
 * Handles user input by creating requests and forwarding them to client socket.
 */
open class MainController(
    val model: KatanModel,
    val responseController: ResponseController
) : RequestController {
    private val logger = KotlinLogging.logger { }
    private lateinit var client: NioKatanClient
    // private lateinit var responseHandler: ResponseDispatcher // NOTE: breaks?

    /**
     * Open connection to server.
     * Set client to listen for messages from server.
     *
     * @param host Server hostname/address
     * @param port Server port
     */
    override fun connectClient(host: String?, port: Int?): Boolean {
        client = NioKatanClient()

        val success: Boolean

        if (host != null) {
            success = client.start(address = host, portNumber = port ?: 1234)
        } else {
            success = client.start(portNumber = port ?: 1234)
        }

        if (!success) {
            return false
        }
        logger.debug { "Init client." }

        // Initializes the response middleware
        val responseHandler = ResponseDispatcher(client, responseController)
        responseHandler.run()
        return true
    }

    // ?
    fun forwardRequest(request: Message<Request>) {
        client.forwardRequest(request)
    }


    override fun handleJoin(sessionId: String) {
        val request = MessageFactory.create(
            messageType = MessageType.JOIN,
            data = Request.Join(sessionId)
        )
        client.forwardRequest(request)
    }

    override fun handleLogin(username: String, password: String) {
        val request = MessageFactory.create(
            messageType = MessageType.LOGIN,
            data = Request.Login(username, password)
        )
        client.forwardRequest(request)
    }

    override fun handleClose() {
        client.close()
    }

    override fun handleRollDice() {
        val request = MessageFactory.create(
            messageType = MessageType.ACTION,
            data = ActionRequest.RollDice
        )
        client.forwardRequest(request)
    }

    override fun handleBuild(buildKind: BuildKind, coordinates: HexCoordinates) {
        val request = MessageFactory.create(
            messageType = MessageType.ACTION,
            data = ActionRequest.Build(buildKind, coordinates)
        )

        client.forwardRequest(request)
    }

    override fun handleGetGroup(pagination: Int) {
        val request = MessageFactory.create(
            messageType = MessageType.GET_GROUPS,
            data = Request.Groups(pagination)
        )
        client.forwardRequest(request)
    }

    override fun handleInit() {
        val request = MessageFactory.create(
            groupId = model.group!!.id,
            messageType = MessageType.INIT,
            data = Request.Init
        )
        client.forwardRequest(request)
    }

    override fun handleCreate(settings: Settings) {

        val request = MessageFactory.create(
            messageType = MessageType.CREATE,
            data = Request.Create(settings, false)
        )
        client.forwardRequest(request)
    }

    override fun handleChat(message: String, recipients: Set<String>) {
        val request = MessageFactory.create(
            messageType = MessageType.CHAT,
            data = Request.Chat(message, recipients)
        )
        client.forwardRequest(request)
    }
}


interface RequestController {

    fun handleInit()

    fun handleCreate(settings: Settings)

    fun handleJoin(sessionId: String)

    fun handleGetGroup(pagination: Int)

    fun connectClient(host: String?, port: Int?): Boolean

    fun handleChat(message: String, recipients: Set<String>)

    fun handleLogin(username: String, password: String)

    fun handleClose()

    fun handleRollDice()

    fun handleBuild(buildKind: BuildKind, coordinates: HexCoordinates)
}

class KtxInputController(
    private val base: MainController,
    val view: KtxKatan
) : RequestController {
    override fun handleInit() {
        base.handleInit()
    }

    override fun handleCreate(settings: Settings) {
        base.handleCreate(settings)
    }

    override fun handleJoin(sessionId: String) {
        base.handleJoin(sessionId)
    }

    override fun handleGetGroup(pagination: Int) {
        base.handleGetGroup(pagination)
    }

    override fun connectClient(host: String?, port: Int?): Boolean {
        return base.connectClient(host, port)
    }

    override fun handleChat(message: String, recipients: Set<String>) {
        base.handleChat(message, recipients)
    }

    override fun handleLogin(username: String, password: String) {
        base.handleLogin(username, password)
    }

    override fun handleClose() {
        base.handleClose()
    }

    override fun handleRollDice() {
        base.handleRollDice()
    }

    override fun handleBuild(buildKind: BuildKind, coordinates: HexCoordinates) {
        base.handleBuild(buildKind, coordinates)
    }

}

/**
 * Simple CLI Input Controller.
 * Listen for requests from the console. Does not keep track of client state and may therefore suggest
 * impossible requests.
 *
 * @property run Starts listening
 */
class SimpleCliInputController(
    private val base: MainController,
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

        base.handleChat(
            message,
            base.model.gameViewModel!!.otherPlayers.map { p -> p.id }.toSet() + base.model.gameViewModel!!.thisPlayer.id
        )
        view.prompt("Chat request sent.")
    }

    fun handleClose() {
        base.handleClose()
    }

}

