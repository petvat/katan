package io.github.petvat.katan.blocking
//
//import com.google.gson.JsonObject
//import io.github.petvat.katan.server.service.GameService
//import io.github.petvat.katan.server.Session
//import io.github.petvat.katan.server.service.SessionService
//import io.github.petvat.katan.server.client.UserRepository
//import io.github.petvat.katan.server.api.ActionAPI
//import io.github.petvat.katan.server.api.GameProgress
//import io.github.petvat.katan.server.service.UserService
//import io.github.petvat.katan.shared.protocol.*
//import io.github.petvat.katan.shared.protocol.dto.ActionRequest
//import io.github.petvat.katan.shared.protocol.dto.Request
//import io.github.petvat.katan.shared.protocol.dto.ActionResponse
//import io.github.petvat.katan.shared.protocol.dto.Response
//import io.github.petvat.katan.shared.protocol.json.GsonParser
//import mu.KotlinLogging
//
///**
// * Implementation of object state pattern.
// *
// * TODO: Rename to modelState,
// */
//abstract class ClientHandlerState {
//    val logger = KotlinLogging.logger {}
//    abstract val sender: ClientHandler
//    abstract fun nextState(vararg inputs: Any)
//
//    /**
//     * Handle all requests through this function.
//     * Parses the request message from JSON, formulates a response ands sends it to its recipients.
//     *
//     * @return whether the execution was successful
//     *
//     */
//    fun handleRequest(request: JsonObject): Boolean {
//        if (!validateSender(request)) {
//            return false
//        }
//        val parsedRequest = GsonParser.toReqMsg(request)
//        val data = parsedRequest.payload.data
//
//        try {
//            val responses: Map<Int, Payload<Response>> = when (parsedRequest.header.messageType) {
//                MessageType.LOGIN -> handleLoginRequest(data as Request.Login)
//                MessageType.CREATE -> handleCreateSession(data as Request.Create)
//                MessageType.JOIN -> handleJoinRequest(data as Request.Join)
//                MessageType.INIT -> handleStartGameRequest()
//                MessageType.ACTION -> handleActionRequest(data as ActionRequest)
//                MessageType.CHAT -> handleChatRequest(data as Request.Chat)
//                MessageType.GET_SESSION -> handleGetSessions(data as Request.Sessions)
//            }
//            val responsesJson = responses.mapValues { (_, value) ->
//                GsonParser.toJson(
//                    ResponseMessage(
//                        Header(
//                            groupId = parsedRequest.header.groupId,
//                            messageType = parsedRequest.header.messageType,
//                            token = parsedRequest.header.token
//                        ),
//                        value
//                    )
//                )
//            }
//            sendResponses(responsesJson) // Bad use other below
//
////            if (sender.session != null) {
////                sendResponses(responsesJson, sender.session!!)
////
////            } else {
////
////            responses.forEach { (pid, resp) ->
////               sender.session.getClientBy(pid).sendResponseMessage(resp)
////            }
//
//
//            return true
//        } catch (e: Exception) {
//            println("Error: ${e.message}.")
//            return false
//        }
//    }
//
//
//    abstract fun sendResponses(responses: Map<Int, JsonObject>)
//
//    /**
//     * Validate that sender is allowed to send request in current context.
//     */
//    abstract fun validateSender(request: JsonObject): Boolean
//
//    /**
//     * Request to create session, changing state to [NoSessionState]
//     */
//    protected open fun handleLoginRequest(request: Request.Login):
//        Map<Int, Payload<Response.Login>> {
//        throw IllegalStateException("Cannot login in the current state.")
//    }
//
//    /**
//     * Request to create session, changing state to [InSessionState].
//     */
//    protected open fun handleCreateSession(request: Request.Create):
//        Map<Int, Payload<Response.Create>> {
//        throw IllegalStateException("Cannot create session in the current state.")
//    }
//
//    /**
//     * Request to join session, changing state to [InSessionState].
//     */
//    protected open fun handleJoinRequest(request: Request.Join):
//        Map<Int, Payload<Response.Join>> {
//        throw IllegalStateException("Cannot join session in the current state.")
//    }
//
//    /**
//     * Request to execute game action.
//     *
//     * @see [ActionAPI] for concrete impl.
//     */
//    protected open fun handleActionRequest(request: ActionRequest): Map<Int, Payload<ActionResponse>> {
//        throw IllegalStateException("Cannot perform action in the current state.")
//    }
//
//    /**
//     * Request to send chat message.
//     *
//     */
//    protected open fun handleChatRequest(request: Request.Chat):
//        Map<Int, Payload<Response.Chat>> {
//        throw IllegalStateException("Cannot send chat message in the current state.")
//    }
//
//    /**
//     * Request to start the game, changing the state to [GameInProgressState] for every client in sender's session.
//     *
//     * Only session host can start the game.
//     */
//    protected open fun handleStartGameRequest():
//        Map<Int, Payload<Response.Init>> {
//        throw IllegalStateException("Cannot start game in the current state.")
//    }
//
//    /**
//     * Request to get ongoing groups.
//     *
//     */
//    protected open fun handleGetSessions(request: Request.Sessions):
//        Map<Int, Payload<Response.Sessions>> {
//        throw IllegalStateException("Cannot get session in the current state.")
//    }
//}
//
///**
// * Initial unauthorized state, accepts only login request.
// */
//class UnAuthState(override val sender: ClientHandler) : ClientHandlerState() {
//
//    override fun nextState(vararg inputs: Any) {
//        sender.state = NoSessionState(sender)
//    }
//
//    override fun sendResponses(responses: Map<Int, JsonObject>) {
//        sendResponses(responses)
//    }
//
//    override fun validateSender(request: JsonObject): Boolean {
//        return sender.id == request.get("SenderId").asInt
//    }
//
//    override fun handleLoginRequest(request: Request.Login): Map<Int, Payload<Response.Login>> {
//        val response = mutableMapOf<Int, Payload<Response.Login>>()
//        val userId = UserRepository.authenticate(request.username, request.password)
//        sender.clientId = userId
//        val loginResponse = Response.Login(
//            UserService.getPrivateUserInfo(UserRepository.getUser(userId)), // TODO: Too complex
//            ""
//        )
//        response[sender.id] = MessageFactory.createResponsePayload(
//            success = true,
//            "Login successful.",
//            loginResponse
//        )
//
//        nextState() // Changes state
//
//        return response
//    }
//}
//
//// BIG NOTE: Time to move away from client handler state pattern. Unlike game, where we
//// BIG NOTE: should not allow all actions always, it only enriches the API to have them available if they are possible.
//// BIG TODO: Remove the client state pattern and add Top-level Api functions.
//// BIG TODO: Use polymorphism in Request - Request.handle
//
///**
// * Representes disconnected state, i.e. not connected to any session.
// *
// * Only allows [Request.Create] and [Request.Join] requests.
// */
//class NoSessionState(override val sender: ClientHandler) : ClientHandlerState() {
//
//    override fun nextState(vararg inputs: Any) {
//        sender.state = InSessionState(sender, inputs[0] as Session)
//    }
//
//    override fun sendResponses(responses: Map<Int, JsonObject>) {
//        sender.sendResponseMessage(responses[sender.id]!!)
//    }
//
//    override fun validateSender(request: JsonObject): Boolean {
//        return sender.id == request.get("SenderId").asInt
//    }
//
//    override fun handleGetSessions(request: Request.Sessions): Map<Int, Payload<Response.Sessions>> {
//        val response = mutableMapOf<Int, Payload<Response.Sessions>>()
//        val groups = Response.Sessions(SessionService.getAllSessionPublicSnapshots())
//        val sessionsResponseMsg =
//            MessageFactory.createResponsePayload(
//                true,
//                "All groups.",
//                groups,
//            )
//        response[sender.id] = sessionsResponseMsg
//        return response
//    }
//
//    /**
//     * Create a new session.
//     *
//     * Returns the a SessionResponse as Json to creator.
//     *
//     */
//    override fun handleCreateSession(request: Request.Create): Map<Int, Payload<Response.Create>> {
//        val response = mutableMapOf<Int, Payload<Response.Create>>()
//        val session = SessionService.createSession(sender.id, request.settings)
//        val dto =
//            MessageFactory.createResponsePayload(
//                true,
//                "Created session.",
//                Response.Create(session.id)
//            )
//        // associate with session. Changes state to In Session state
//        connectToSession(session)
//        response[sender.id] = dto
//        return response
//    }
//
//    override fun handleJoinRequest(request: Request.Join): Map<Int, Payload<Response.Join>> {
//        val responses = mutableMapOf<Int, Payload<Response.Join>>()
//        val session = SessionService.groups[request.groupId]
//
//        if (session != null) {
//            connectToSession(session)
//
//            val sessionJoinedDTO = Response.Join(
//                SessionService.getGroupView(
//                    session,
//                    UserRepository.getUser(sender.id)
//                )
//            )
//            for (client in session.clients) {
//                responses[client.id] =
//                    MessageFactory.createResponsePayload(
//                        data = if (client.id == sender.id) null else sessionJoinedDTO,
//                        success = true,
//                        description = "${sender.id} joined session.",
//                    )
//            }
//            return responses
//        } else {
//            throw IllegalArgumentException("Session did not get created.")
//        }
//    }
//
//
//    /**
//     * Connect to session, changing state to [InSessionState].
//     */
//    private fun connectToSession(session: Session) {
//        session.addClient(sender)
//        // sender.session = session
//        sender.state = InSessionState(sender, session)
//    }
//}
//
//
///**
// * Represents client handler connected to session and game in progress.
// */
//class GameInProgressState(
//    override val sender: ClientHandler,
//    private val session: Session,
//    private val game: GameProgress
//) :
//    ClientHandlerState() {
//
//    override fun nextState(vararg inputs: Any) {
//        TODO("Not yet implemented")
//    }
//
//    override fun sendResponses(responses: Map<Int, JsonObject>) {
//        responses.forEach { (pid, resp) ->
//            session.getClientBy(pid).sendResponseMessage(resp)
//        }
//
//    }
//
//    override fun validateSender(request: JsonObject): Boolean {
//        return sender.id == request.get("senderId").asInt &&
//            session.clients.contains(sender)
//    }
//
//    override fun handleActionRequest(request: ActionRequest): Map<Int, Payload<ActionResponse>> {
//        return ActionAPI.serviceRequest(sender.id, request, game)
//    }
//
//    override fun handleChatRequest(request: Request.Chat): Map<Int, Payload<Response.Chat>> {
//        val responses: MutableMap<Int, Payload<Response.Chat>> = mutableMapOf()
//
//        // Broadcast the message to all clients in the session
//        for (client in session.clients) {
//
//            // TODO: check if target
//            responses[client.id] =
//                MessageFactory.createResponsePayload(
//                    success = true,
//                    description = "Message received.",
//                    data = Response.Chat(client.id, request.message)
//                )
//        }
//
//        return responses
//    }
//}
//
//
///**
// * Represents client handler in connected state, i.e. connected to a session, but game has not started.
// * Only allows INIT and CHAT requests.
// */
//class InSessionState(override val sender: ClientHandler, private val session: Session) : ClientHandlerState() {
//
//    override fun nextState(vararg inputs: Any) {
//        TODO("Not yet implemented")
//    }
//
//    override fun sendResponses(responses: Map<Int, JsonObject>) {
//        responses.forEach { (pid, resp) ->
//            session.getClientBy(pid).sendResponseMessage(resp)
//        }
//    }
//
//    /**
//     * Auth player and check belongs to session.
//     */
//    override fun validateSender(request: JsonObject): Boolean {
//        return session.clients.contains(sender) &&
//            sender.id == request.get("SenderId").toString().toInt()
//    }
//
//    override fun handleChatRequest(request: Request.Chat): Map<Int, Payload<Response.Chat>> {
//        val responses: MutableMap<Int, Payload<Response.Chat>> = mutableMapOf()
//
//        // Broadcast the message to all clients in the session
//        for (client in session.clients) {
//
//            responses[client.id] =
//                MessageFactory.createResponsePayload(
//                    success = true,
//                    description = "Message received.",
//                    data = Response.Chat(client.id, request.message)
//                )
//        }
//
//        return responses
//    }
//
//
//    override fun handleStartGameRequest(): Map<Int, Payload<Response.Init>> {
//        // check requester is session host
//        if (session.hostId != sender.id) {
//            throw IllegalStateException("Only host can start the game.")
//        }
//        val responses: MutableMap<Int, Payload<Response.Init>> = mutableMapOf()
//
//
//        val game = GameService.createGameProgress(session)
//        logger.debug { "Game initialized." }
//
//        // Connect all clients to game
//        for (client in session.clients) {
//            (client.state as InSessionState).connectToGame(game) // !!!
//
//            val response = MessageFactory.createResponsePayload(
//                data = Response.Init(GameService.getPrivateGameState(game, game.getPlayer(client.id))),
//                success = true,
//                description = "Game has started."
//            )
//
//            responses[client.id] = response
//
//        }
//
//        return responses
//    }
//
//    /**
//     * Connect to game, changing _state to [GameInProgressState].
//     */
//    private fun connectToGame(game: GameProgress) {
//        session.game = game
//        sender.state = GameInProgressState(sender, session, game)
//    }
//}
