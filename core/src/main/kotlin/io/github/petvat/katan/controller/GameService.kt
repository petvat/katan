package io.github.petvat.katan.controller

//import io.github.oshai.kotlinlogging.KotlinLogging
//import io.github.petvat.katan.networking.NioKatanClient
//import io.github.petvat.katan.event.Event
//import io.github.petvat.katan.model.ClientState
//import io.github.petvat.katan.shared.hexlib.Coordinates
//import io.github.petvat.katan.shared.model.board.BuildKind
//import io.github.petvat.katan.shared.model.game.Settings
//import io.github.petvat.katan.shared.protocol.Request
//import java.util.concurrent.ConcurrentHashMap
//import java.util.concurrent.atomic.AtomicInteger
//
//
//class RequestTracker() {
//    private val pendingRequests = ConcurrentHashMap<String, Request>()
//
//    fun track(request: Request) {}
//
//    fun resolve() {}
//
//}
//
//typealias ResponseHandler<R> = (response: R) -> Event
//
//
//class GameService(val gameState: ClientState, private val responseProcessor: ResponseProcessor) {
//    private val logger = KotlinLogging.logger { }
//
//    private var seq = AtomicInteger(0)
//
//    lateinit var client: NioKatanClient
//
//
//    /**
//     * Open connection to server.
//     * Set client to listen for messages from server.
//     *
//     * @param host Server hostname/address
//     * @param port Server port
//     *
//     * @return true if the connection was successful
//     */
//    fun handleConnectClient(host: String?, port: Int?): Boolean {
//        client = NioKatanClient()
//
//        val success = client.start(address = host, portNumber = port ?: 1234)
//
//        if (!success) {
//            return false
//        }
//        logger.debug { "Init client." }
//
//        listenForResponses()
//        return true
//    }
//
//    fun handleRegister(name: String) = send { seq ->
//        Request.GuestRegister(seq = seq, name = name)
//    }
//
//    fun handleJoin(channelId: String) = send { seq ->
//        Request.Join(
//            channelId,
//            seq,
//        )
//    }
//
//    fun handleRollDice(channelId: String) = send { seq -> Request.RollDice(channelId, seq) }
//
//    fun handleBuild(channelId: String, buildKind: BuildKind, coordinates: Coordinates) = send { seq ->
//        Request.Build(channelId, seq, buildKind, coordinates)
//    }
//
//    fun handleInit() = send { seq -> Request.Init(channel = gameState.gameFocus.gameId, seq = seq) }
//
//    // TODO: MOVE
//    fun handleClose() {
//        client.close()
//    }
//
//    fun handleCreate(settings: Settings) = send { seq -> Request.Create(seq = seq, settings = settings) }
//
//    fun handleChat(message: String) = send { seq ->
//        Request.Chat(
//            seq = seq,
//            channel = gameState.chatFocus.chatId,
//            message = message
//        ) // gameState.group.members.values.filter { it != gameState.sessionId }.toSet()
//    }
//
//    private fun send(partialRequest: (Int) -> Request) {
//        val id = seq.incrementAndGet()
//        val request = partialRequest(id)
//        client.forwardRequest(request)
//    }
//
//    private fun listenForResponses() {
//        val t = Thread {
//            while (true) {
//                val message = client.messageQueue.take()
//                logger.debug { "Polled message from NIO client message queue." }
//                responseProcessor.update(message)
//            }
//        }
//        t.isDaemon = true // Kill on main exit.
//        t.start()
//    }
//}
