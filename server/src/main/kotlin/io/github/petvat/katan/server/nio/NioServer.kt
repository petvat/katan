package io.github.petvat.katan.server.nio

import io.github.petvat.katan.server.api.handleRequest
import io.github.petvat.katan.server.client.ClientService
import io.github.petvat.katan.server.client.GuestState
import io.github.petvat.katan.shared.protocol.SessionId
import io.github.petvat.katan.shared.protocol.*
import io.github.petvat.katan.shared.protocol.dto.Request
import io.github.petvat.katan.shared.protocol.dto.Response
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.shared.protocol.json.KatanJson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.log

// TODO: MOVE
/**
 * Avoid race conditions by aquiring its lock from this point.
 */
val clientLockMap = ConcurrentHashMap<SessionId, Mutex>()

suspend fun <R> wrapWithMutex(mutex: Mutex, function: suspend () -> R): suspend () -> R {
    return {
        mutex.withLock {
            function()
        }
    }
}


/**
 * This class represents a NIO server.
 *
 * @property start Starts up the server on a single thread
 * @property acceptConnection Accepts a new client connection
 * @property handleResponse Sends a response back to client
 * @property acceptClientRequest Processes a client request.
 *
 */
class NioServer(
    private val requestChannel: Channel<Pair<SessionId, String>>,
    private val responseChannel: Channel<Pair<SocketChannel, String>>,
    private val testMode: Boolean = false
) {
    private val logger = KotlinLogging.logger { }
    private val serverChannel = ServerSocketChannel.open()
    private val selector = Selector.open()

    // private val clients = mutableSetOf<ClientConnection>()
    private val clients = ConcurrentHashMap<SessionId, SocketChannel>()
    private val serverScope = CoroutineScope(Dispatchers.Default)


    /**
     * A queue of requests from clients.
     */

    /**
     * A queue of responses generated by the server.
     */

    private val clientBuffers = ConcurrentHashMap<SocketChannel, Pair<ByteBuffer, StringBuilder>>()

//    private fun generateConnId(): CID {
//        val secureRandom = SecureRandom()
//        return CID(abs(secureRandom.nextLong()).toString())
//    }

    private fun generateSessionId(): SessionId {
        return SessionId(UUID.randomUUID().toString());
    }

    /**
     * Main server entry.
     *
     * @throws IOException Close connections.
     */
    fun start(portNumber: Int) {
        try {
            serverChannel.configureBlocking(false)
            serverChannel.bind(InetSocketAddress(portNumber))
            serverChannel.register(selector, SelectionKey.OP_ACCEPT)

            // This coroutine acts as the producer. Listens to client requests and enqueues them if request must be handled.
            serverScope.launch(Dispatchers.IO) {
                while (true) {
                    if (selector.select() == 0) {
                        continue
                    }
                    selector.selectedKeys().forEach { key ->
                        if (key.isAcceptable) {
                            when (val channel = key.channel()) {
                                is ServerSocketChannel -> acceptConnection(channel)
                                else -> throw RuntimeException("Unknown channel.")
                            }
                        } else if (key.isReadable) {
                            logger.debug { "Readable request" }
                            when (val channel = key.channel()) {
                                is SocketChannel -> acceptClientRequest(key)
                                else -> throw RuntimeException("Unknown channel.")
                            }
                        }
                    }
                    selector.selectedKeys().clear()
                }
            }

            // This coroutine acts as consumer. Dequeues from request queue, generates response and enqueues it onto response queue.
            val requestProcessorsPoolSize = 3
            repeat(requestProcessorsPoolSize) {
                serverScope.launch {
                    for ((client, req) in requestChannel) {
                        if (testMode) {
                            // echo
                            responseCallback(mapOf(client to req))
                        } else {
                            handleRequest(req, client, ::responseCallback)
                        }
                    }
                }
            }

            val responsePoolSize = 3
            repeat(responsePoolSize) {
                serverScope.launch(Dispatchers.IO) {
                    for ((client, res) in responseChannel) {
                        handleResponse(client, res)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error { "${e.message}.\nClosing connections." }
            serverScope.cancel()
            serverChannel.close()
        }
    }

    private suspend fun responseCallback(responses: Map<SessionId, String>) {
        //val json = responses.mapValues { KatanJson.messageToJson(it.value) }
        responses.forEach { (ch, res) ->
            responseChannel.send(clients[ch]!! to res)
        }
    }

    private fun handleResponse(client: SocketChannel, response: String) {
        val buffer = ByteBuffer.wrap(response.toByteArray())
        var writes = 0
        while (buffer.hasRemaining()) {
            client.write(buffer)
            writes++
        }
        // TODO: Now blocking but could use channel's MessageWriter

        logger.debug { "Writes: $writes" }

    }

    /**
     * Connect a new client to server.
     */
    private fun acceptConnection(serverChannel: ServerSocketChannel) {
        val client = serverChannel.accept()
        val socket = client.socket()
        logger.info { "CONNECTED: ${socket.inetAddress.hostAddress} : ${socket.port}" }
        client.configureBlocking(false)
        val key = client.register(selector, SelectionKey.OP_READ)
        // val clientConn = ClientConnection(client)

        // Add dedicated client buffer
        clientBuffers[client] = ByteBuffer.allocate(4096) to StringBuilder()

        // Attach ClientSession to track state
        val sid = generateSessionId()
        key.attach(sid)
        ClientService.addClient(GuestState(sessionId = sid))
        clients[sid] = client
        clientLockMap[sid] = Mutex()
        logger.debug { "New client is given session ID: $sid." }

        // Response with SID
        // TODO: Mutex!
        logger.debug { "Send back ACK." }
        handleResponse(
            client, KatanJson.messageToJson(
                MessageFactory.create(
                    messageType = MessageType.ACK,
                    data = Response.ConnectionAccept(sid.value),
                    description = "Client was assigned a session ID.",
                    success = true
                )
            )
        )


    }


    // clients += clientConn


    /**
     * Read from a channel into a buffer.
     */
    private fun channelRead(socketChannel: SocketChannel): String? {
        val (buf, str) = clientBuffers[socketChannel]!!
        val delimiter = '\n'

        logger.debug { "Begin read." }

        try {
            val bytesRead = socketChannel.read(buf) // NOTE: Could overflow the buffer.
            buf.flip() // read mode

            logger.debug { "PARTIAL: $str" }

            // If the string builder is not empty, that indicates that there is a
            // partial message in the buffer
            if (bytesRead == -1) {
                logger.error { "Read failed." }
                val socket = socketChannel.socket()
                logger.info { "DISCONNETED: ${socket.inetAddress.hostAddress} : ${socket.port}" }
                clientBuffers -= socketChannel
                socketChannel.close()
                return null
            }
            while (buf.hasRemaining()) {

                val c = buf.get().toInt().toChar()
                if (c == delimiter) {
                    if (buf.hasRemaining()) {
                        // There is a partial message in the buffer.
                        buf.compact()
                    } else {
                        buf.clear()
                    }
                    val msg = str.toString()
                    // We can clear the str because it now contains a complete message.
                    str.clear()
                    logger.debug { "Complete msg return: $msg" }
                    return msg
                } else {
                    str.append(c)
                }
            }
            // If we never reach a delimiter, we had a partial read.
            // Buffer should be empty.
            buf.flip() // write mode
            return null

        } catch (e: Exception) {
            logger.debug { "DISCONNECTING TYP 2, ABRUPT: ${e.message}" }
            disconnectClient(socketChannel)
            return null
        }

    }

    private fun disconnectClient(socketChannel: SocketChannel) {
        logger.info { "DISCONNECTED: ${socketChannel.remoteAddress}." }

        val key = socketChannel.keyFor(selector)
        val sid = key.attachment() as SessionId

        clientBuffers -= socketChannel
        clients -= sid
        clientLockMap -= sid

        // Cancel the selection key and close the channel
        key.cancel()
        socketChannel.close()
        logger.debug { "Disconnect clean-up successful." }
    }


    /**
     * Handle new client request.
     */
    private suspend fun acceptClientRequest(key: SelectionKey) = withContext(Dispatchers.IO) {
        val clientChannel = key.channel() as SocketChannel

        val sid = key.attachment() as SessionId
        logger.debug { "Aquire client lock attempt." }
        val mutex = clientLockMap[sid]!!
        wrapWithMutex(mutex) {
            val req = channelRead(clientChannel)

            if (req != null) {
                logger.info { "COMPLETE MSG: $req" }
                requestChannel.send(sid to req) // NOTE: Or client channel here if stateful
            }
        }.invoke()


        // val data = String(buffer.array(), buffer.position(), bytesRead).trim()

        // Enqueue request and associate with this client connection
    }
}


/**
 * This class is responsible for managing the networking of a group of [ClientConnection] objects.
 * It implements a blocking queue that operates on a single thread. All requests from members of this should be put on the blocking queue for processing.
 *
 * @property processQueue The blocking queue
 * @property members The members of this group
 * @property processExecutor Single-threaded executor that consumes the [processQueue]
 */
//open class NioGroup() {
//    private val logger = KotlinLogging.logger { }
//    private val _members = mutableListOf<ClientConnection>()
//    private val processQueue = LinkedBlockingQueue<Runnable>()
//    private val processExecutor = Executors.newSingleThreadExecutor()
//
//    val members: List<ClientConnection>
//        get() = _members
//
//    init {
//        Thread { executeProcesses() }.start()
//    }
//
//    /**
//     * Adds a new client to [members].
//     */
//    fun addClient(client: ClientConnection) {
//        _members += client
//    }
//
//    /**
//     * Removes a client from [members].
//     */
//    fun removeClient(client: ClientConnection) {
//        _members -= client
//    }
//
//    /**
//     * Enqueues a process on the queue.
//     *
//     * @param request The request to be processed
//     * @param processor The processor of this request
//     */
//    fun enqueueProcess(request: Message<Request>, processor: (Message<Request>) -> Map<Int, Message<Response>>) {
//        processQueue.offer {
//            processExecutor.submit {
//                processor(request).forEach { (id, resp) ->
//                    members[id].sendResponse(GsonParser.toJson(resp).toString())
//                }
//            }
//        }
//    }
//
//    /**
//     * Executes the next process on the queue. This function is called on initialization.
//     *
//     * @throws InterruptedException TODO: handle interrupt
//     */
//    private fun executeProcesses() {
//        try {
//            while (true) {
//                val request = processQueue.take() // Blocking call
//                request.run()
//            }
//        } catch (e: InterruptedException) {
//            Thread.currentThread().interrupt()
//            logger.error { "Group interrupted during request processing" }
//        }
//    }
//}


/**
 * @suppress Unsafe unchecked cast from Message<Request> to Message<Request.*> due to type erasure. To avoid bending the code out of shape, I have decided to allow this.
 *
 * TODO: Find way to avoid suppressing
 */
//    @Suppress("UNCHECKED_CAST")
//    suspend fun handleRequest(client: ClientConnection, json: String) {
//        val request = KatanJson.jsonToRequest(json) // TODO: Could break.
//
//        client.mutex.withLock {
//            try {
//
//                when (val messageType = request.header.messageType) {
//                    MessageType.CHAT -> processRequest(client, request, ClientState.IN_SESSION) { req ->
//                        handleChat(client, req as Message<Request.Chat>)
//                    }
//
//                    MessageType.ACTION -> processRequest(client, request, ClientState.IN_SESSION) { req ->
//                        handleAction(client, req as Message<ActionRequest>)
//                    }
//
//                    MessageType.JOIN -> processRequest(client, request, ClientState.SESSIONLESS) { req ->
//                        handleJoin(client, req as Message<Request.Join>)
//                    }
//
//                    MessageType.CREATE -> processRequest(client, request, ClientState.SESSIONLESS) { req ->
//                        handleCreate(client, req as Message<Request.Create>)
//                    }
//
//                    MessageType.INIT -> processRequest(client, request, ClientState.IN_SESSION) { req ->
//                        handleInit(client, req as Message<Request.Init>)
//                    }
//
//                    MessageType.LOGIN -> processRequest(client, request, ClientState.UNAUTHENTICATED) { req ->
//                        handleLogin(client, req as Message<Request.Login>)
//                    }
//
//                    MessageType.GET_SESSION -> processRequest(client, request, ClientState.UNAUTHENTICATED, negate = true) { req ->
//                        handleGetSessions(client, req as Message<Request.Sessions>)
//                    }
//                }
//            } catch (e: Exception) {
//                // TODO: Handle error
//            }
//
//        }
//
//
//        }
//    }

// NOTE: Tanke
// KatanAPI har liste med clientConn

// Processing : Message<Request> -> Message<Response> - Alltid sync på session/Group
// Nokre måtar: Finn session, bruk groupId-en. Ha ein clientConn.session
// Ved finn session: Vi les messageType, sjekker client state,
// Uansett: utfører med thread synced(session)
// ResponseHandler : Map<Int, Message<Response> -> Send responses to clients

//
///**
// * TODO: REFACTOR THIS!!!
// *
// * @throws IllegalArgumentException If client state does not allow this request
// */
//private fun processRequest(
//    client: ClientConnection,
//    request: Message<Request>,
//    requiredState: ClientState,
//    negate: Boolean = false,
//    block: (Message<Request>) -> Map<Int, Message<Response>>
//) {
//    require((client.state == requiredState) != negate) { "${request.header.messageType.name} is not allowed in current state" }
//    //client.group.enqueueProcess(request, block)
//}
//
//
//// Session is different from no Session state. No session state should be a group
//// Session is a group.
//
///**
// * Request to start a new game.
// */
//private fun handleInit(
//    client: ClientConnection,
//    request: Message<Request.Init> // NOTE: request is really not necessary
//): Map<Int, Message<Response.Init>> {
//    TODO()
//        val session = client.session!!
//        if (session.hostId != client.userDetails!!.id) {
//            throw IllegalArgumentException("Only host can start the game.")
//        }
//        val responses: MutableMap<Int, Message<Response.Init>> = mutableMapOf()
//        val game = GameService.createAndAttach(session)
//        for (member in session.members) {
//            responses[client.userDetails!!.id] = MessageFactory.createResponse(
//                messageType = request.header.messageType,
//                data = Response.Init(GameService.getPrivateGameState(game, game.getPlayer(client.id))),
//                success = true,
//                description = "Game has started."
//            )
//        }
//        return responses
//}


///**
// * This class represents a session between clients. The session is Non-blocking in the sense that
// * there is only one thread managing this session.
// *
// * Works like a producer-consumer buffer.
// *
// * @property clients The clients connected to this session. Concurrent, non-blocking queue.
// * @property requestQueue Queue of pending requests that are to be executed. Blocking queue.
// * @property requestExecutor Executes a from the [requestQueue]. Single-threaded.
// *
// */

//    init {
//        Thread { executeRequests() }.start()
//    }
//
//    /**
//     * Puts incoming request into requestQueue. Request is processed and a response is returned when the queue is ready.
//     */
//    fun enqueueRequest(request: Message<Request>, processor: (Message<Request>) -> Map<Int, Message<Response>>) {
//        requestQueue.offer {
//            requestExecutor.submit {
//                val response = processor(request)
//
//                //val buffer = ByteBuffer.wrap(response.toByteArray())
//                clients
//
//            }
//        }
//    }
//
//    /**
//     * This function is called on a separate thread. Its only purpose is to run pending requests on the queue until it's emtpy.
//     */
//    private fun executeRequests() {
//        try {
//            while (true) {
//                val request = requestQueue.take() // Blocking call
//                request.run()
//            }
//        } catch (e: InterruptedException) {
//            Thread.currentThread().interrupt()
//            logger.error { "Session interrupted during request processing" }
//        }
//    }
//
//    /**
//     * Send response message to members of session.
//     *
//     * @param response The response to be sent.
//     * @param recipients The members of this session who should receive this response, or all, if null
//     */
////    private fun sendResponse(response: String, recipients: List<Int>?) {
////
////        val buffer = ByteBuffer.wrap(response.toByteArray())
////        clients
////            .filterNot { recipients != null && !recipients.contains(it.user.id) }
////            .forEach { it.channel.write(buffer) }
////    }


//
//
//    // TODO: Make this in separate thread. (Overkill)
//    //  Reintroduce state
//    fun handleRequest(sender: ClientConnection, request: RequestMessage<Request>) {
//        try {
//            when (request.header.messageType) {
//                MessageType.CHAT -> {
//                    val session = requireNotNull(sender.session) { "Client can only request CHAT while in session." }
//                    session.enqueueRequest(request) { req ->
//                        handleChatRequest(
//                            sender,
//                            req as Request.Chat
//                        )
//                    }
//                }
//
//                MessageType.JOIN -> {
//                    // TODO: find session
//                    val session =
//                        requireNotNull(sender.session) { "Client requested to join session that does not exist." }
//                    session.enqueueRequest(request) { req ->
//                        handleJoinRequest(
//                            sender,
//                            req as Request.Join
//                        )
//                    }
//                }
//
//                MessageType.ACTION -> {
//                    val session = requireNotNull(sender.session) { "Client can only request ACTION while in session." }
//                    // NOTE: GameProgress?
//                    session.enqueueRequest(request) { req ->
//                        handleActionRequest(
//                            sender,
//                            req as ActionRequest
//                        )
//                    }
//                }
//
//                MessageType.CREATE -> {
//                    // TODO: Ordinary queue for this
//                    // NOTE: Could use enqueueRequest, KatanQueue
//                    //  Use the same system as for session?
//                }
//
//                MessageType.INIT -> {
//                    val session = requireNotNull(sender.session) { "Client can only request ACTION while in session." }
//                    session.enqueueRequest(request) {
//                        handleInitRequest(
//                            sender,
//                        )
//                    }
//                }
//
//                MessageType.LOGIN -> {
//                    // TODO: Ordinary queue for this
//                }
//
//                MessageType.GET_SESSION -> {
//                    // TODO: Ordinary queue for this
//                }
//            }
//        } catch (e: Exception) {
//            // TODO: Schedule to junk response
//        }
//    }
//
//    private fun handleGetSession(
//        sender: ClientConnection,
//        request: RequestMessage<Request.Sessions>
//    ): Map<Int, ResponseMessage<Response.Sessions>> {
//        TODO()
//    }
//
//    private fun handleLoginRequest(
//        sender: ClientConnection,
//        request: RequestMessage<Request.Login>
//    ): Map<Int, ResponseMessage<Response.Login>> {
//        TODO()
//    }
//
//    private fun handleInitRequest(sender: ClientConnection, request: RequestMessage<Nothing>): Map<Int, ResponseMessage<>> {
//        TODO()
//    }
//
//    private fun handleCreateRequest(
//        sender: ClientConnection,
//        request: Request.Create
//    ): Map<Int, Payload<Response.Create>> {
//        TODO()
//    }
//
//    private fun handleJoinRequest(sender: ClientConnection, request: Request.Join): Map<Int, Payload<Response.Join>> {
//        TODO()
//    }
//
//    private fun handleChatRequest(sender: ClientConnection, request: Request.Chat): Map<Int, Payload<Response.Chat>> {
//        TODO()
//    }
//
//    private fun handleActionRequest(
//        sender: ClientConnection,
//        request: ActionRequest
//    ): Map<Int, Payload<ActionResponse>> {
//        return ActionAPI.serviceRequest()
//    }



