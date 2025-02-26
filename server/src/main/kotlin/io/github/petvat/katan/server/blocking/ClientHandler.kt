package io.github.petvat.katan.blocking

//import com.google.gson.JsonObject
//import com.google.gson.JsonParser
//import io.github.petvat.katan.server.Session
//import io.github.petvat.katan.server.api.GameProgress
//import io.github.petvat.katan.shared.User
//import io.github.petvat.katan.shared.protocol.*
//import io.github.petvat.katan.shared.protocol.dto.RequestVisitor
//import io.github.petvat.katan.shared.protocol.dto.Request
//import io.github.petvat.katan.shared.protocol.dto.Response
//import mu.KotlinLogging
//import java.io.*
//import java.net.Socket
//
//
//interface RequestHandler<T> {
//    fun handle(request: T)
//}
//
//interface KatanState : RequestVisitor {
//    val model: KatanContext
//
//    fun handle(request: RequestMessage): Boolean {
//        try {
//            val responses = request.accept(this) // This works if we MUST simplify Request and Response messages
//
//            // TODO: Responses
//            if (model.session != null) {
//                responses.forEach { (pid, resp) ->
//                    session.getClientBy(pid).sendResponseMessage(resp)
//                }
//            } else {
//                // this client.sendResponseMessage(resp)
//            }
//
//        } catch (e: Error) {
//            println("Error")
//            return false
//        }
//
//        return true
//    }
//
//
//    override fun processJoin(chat: Request.Join): Map<Int, Payload<Response.Join>> {
//        TODO("Not yet implemented")
//    }
//
//    override fun processCreate(chat: Request.Create): Map<Int, Payload<Response.Create>> {
//        TODO("Not yet implemented")
//    }
//
//    override fun processChat(chat: Request.Chat): Map<Int, Payload<Response.Chat>> {
//        TODO("Not yet implemented")
//    }
//
//}
//
//class UnAuthedState(override val model: KatanContext) : KatanState {
//
//}
//
//class SessionState(override val model: KatanContext, private val session: Session) : KatanState {
//    override fun processChat(chat: Request.Chat): Map<Int, Payload<Response.Chat>> {
//        // Broadcast the message to all clients in the session
//        for (client in session.clients) {
//
//            // TODO: check if target
//            responses[client.id] =
//                MessageFactory.createResponsePayload(
//                    success = true,
//                    description = "Message received.",
//                    data = Response.Chat(client.userId, request.message)
//                )
//        }
//
//        return responses
//    }
//
//    // NOTE: Broadcast or not is not dependent on state but message type, so need
//    override fun sendResponses(response: ResponseMessage) {
//        for (client in session.clients) {
//            client.sendResponseMessage()
//        }
//    }
//
//}
//
//class KatanContext(val clientHandler: ClientHandler) : RequestHandler<RequestMessage> {
//    var user: User? = null // Or lateinit throw
//    var session: Session? = null
//    var game: GameProgress? = null
//    var state: KatanState = UnAuthedState(this)
//
//
//    override fun handle(request: RequestMessage) {
//        state.handle(request)
//    }
//
//}
//
//
//abstract class AbstractClientHandler<In, Out>(clientSocket: Socket) : Runnable {
//    private val _logger = KotlinLogging.logger { }
//    private val _inn = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
//    private val _out = BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream()))
//
//    val observers = mutableListOf<RequestHandler<In>>()
//
//    protected abstract fun processRequest(request: String): In
//
//    protected abstract fun processResponse(request: Out): String
//
//
//    /**
//     * write parsed message to _out.
//     */
//    fun forwardResponse(response: Out) {
//        _out.write(processResponse(response)) // wakes up client
//        _out.newLine()
//        _out.flush()
//    }
//
//    override fun run() {
//        _logger.debug { "handler running." }
//        try {
//            var message: String
//            while (_inn.readLine().also { message = it } != null) {
//                _logger.debug { "Request received." }
//
//                observers.forEach { it.handle(processRequest(message)) }
//            }
//        } catch (e: IOException) {
//            _logger.error { "Exception: ${e.message}" }
//        }
//    }
//}
//
//
///**
// * _inn and _out represents the connection with Client.
// *
// * @property session
// * @property id The ID of the client handler
// */
//class ClientHandler(val id: Int, clientSocket: Socket) : Runnable {
//    private val _logger = KotlinLogging.logger { }
//    private val _inn = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
//    private val _out = BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream()))
//
//    var state: ClientHandlerState = UnAuthState(this)
//
//    // model or session, game,
//    var session: Session? = null
//
//    var clientId: Int? = null
//
//    /**
//     * Handle an incoming request from client.
//     *
//     * @return if the request sucesseded.
//     */
//    private fun handleRequest(request: JsonObject): Boolean {
//        return state.handleRequest(request)
//    }
//
//    /**
//     * write parsed message to _out.
//     */
//    fun sendResponseMessage(message: JsonObject) {
//        _out.write(message.toString()) // wakes up client
//        _out.newLine()
//        _out.flush()
//    }
//
//    override fun run() {
//        _logger.debug { "handler running." }
//        try {
//            var message: String
//            while (_inn.readLine().also { message = it } != null) {
//                _logger.debug { "Request received." }
//                val request = JsonParser.parseString(message).asJsonObject
//                val success = handleRequest(request)
//
//            }
//        } catch (e: IOException) {
//            _logger.error { "Exception: ${e.message}" }
//        }
//    }
//}
