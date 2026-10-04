package io.github.petvat.katan.server.service.gateway

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.server.service.channel.Channel
import io.github.petvat.katan.server.service.channel.ChannelId
import io.github.petvat.katan.server.service.channel.ResumeToken
import io.github.petvat.katan.server.service.client.Auth
import io.github.petvat.katan.server.service.client.AuthCredentials
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.command.*
import io.github.petvat.katan.shared.protocol.*
import json.KatanJson


class RequestProcessor(
    private val authService: AuthService,
    private val lobbyService: LobbyService,
    private val groupService: GroupService,
    private val gameService: GameService,
    private val chatService: ChatService
) {

    private val logger = KotlinLogging.logger { }

    /**
     * @param callback receives the final per-recipient JSON strings, keyed by the
     *   recipient's live socket -- this is the only place [OutMessage] gets turned into text.
     */
    suspend fun handle(
        client: ConnectedClient,
        rawRequest: String,
        callback: suspend (Map<ConnectedClient, String>) -> Unit
    ) {
        val msg = try {
            KatanJson.toInMessage(rawRequest)
        } catch (e: Exception) {
            val errOut =
                OutMessage(replyTo = null, payload = Response.Error(ErrorCode.FMT, e.message ?: "Unknown error"))
            return callback(mapOf(client to KatanJson.toJson(errOut)))
        }

        try {
            val result: Map<ConnectedClient, OutMessage> = when (client.auth) {
                is Auth.Unauth -> handleUnauthenticated(client, msg)
                else -> handleAuthenticated(client, msg)
            }

            callback(result.mapValues { (_, out) -> KatanJson.toJson(out) })
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
            val err = OutMessage(
                replyTo = msg.seq,
                payload = Response.Error(ErrorCode.UNTRACED_ERR, e.message ?: "Unknown error")
            )
            callback(mapOf(client to KatanJson.toJson(err)))
        }


    }

    private suspend fun handleUnauthenticated(
        client: ConnectedClient,
        message: InMessage
    ): Map<ConnectedClient, OutMessage> {

        // TODO: Clutter
        val response = when (val request = message.payload) {
            is Request.Register ->
                authService.handleFreshRegister(client, AuthCredentials.User(request.username, request.psw))

            is Request.GuestRegister ->
                authService.handleFreshRegister(client, AuthCredentials.Guest(request.name))

            is Request.Resume ->
                authService.handleResume(client.connection!!, ResumeToken(request.token))

            else ->
                Response.Error(ErrorCode.UNAUTHENTICATED, "Register or resume first.")
        }
        // None of these are channel events -- no channelSeq, always a direct reply.
        return mapOf(client to OutMessage(replyTo = message.seq, payload = response))
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun handleAuthenticated(
        client: ConnectedClient,
        message: InMessage
    ): Map<ConnectedClient, OutMessage> {
        val command = try {
            toCommand(message.payload)
        } catch (e: NotImplementedError) {
            handleError(
                message, client, ErrorCode.UNKNOWN_COMMAND
            )
        }

        // NOTE: BREAK
        val channelId = message.channel?.let(::ChannelId)
            ?: return handleError(message, client, ErrorCode.FMT, "Missing channel")

        val service = when (command) {
            is GroupCommand -> groupService
            is LobbyCommand -> lobbyService
            is GameCommand -> gameService
            is ChatCommand -> chatService
            else -> handleError(message, client, ErrorCode.UNKNOWN_COMMAND)
        }


        val response = (service as? CommandDispatcher<Command, Channel<*>>)
            ?.handleCommand(client, channelId, command as Command, message.seq)
            ?: handleError(message, client, ErrorCode.UNKNOWN_COMMAND, "${command::class}")
        return response
    }

    private fun toCommand(request: Request): Command = when (request) {
        is Request.Build -> Build(buildKind = request.buildkind, coordinates = request.coordinates)
        is Request.BuildInitSettl -> BuildInitSettlment(coordinates = request.coordinates)
        is Request.Chat -> Chat(request.message)
        is Request.ClaimVictory -> TODO()
        is Request.Create -> CreateGroup(request.settings)
        is Request.EndTurn -> TODO()
        is Request.Init -> InitGame
        is Request.InitTrade -> TODO()
        is Request.Join -> JoinGroup
        Request.Leave -> LeaveGroup
        is Request.MoveRobber -> TODO()
        is Request.RespondTrade -> TODO()
        Request.RollDice -> RollDice
        is Request.Steal -> TODO()
        is Request.Register, is Request.GuestRegister, is Request.Resume ->
            error("${request::class.simpleName} must be handled pre-auth, not reach toCommand")
    }
}

//
//class RequestProcessor(
//    authService: AuthService
//    lobbyService: LobbyService,
//    groupService: GroupService,
//    gameService: GameService,
//) {
//
//    private val services = mapOf(
//        GameCommand::class to gameService,
//        LobbyCommand::class to lobbyService,
//        GroupCommand::class to groupService,
//    )
//
//    @Suppress("UNCHECKED_CAST")
//    suspend fun handle(
//        client: ConnectedClient,
//        rawRequest: String,
//        callback: KSuspendFunction1<Map<ConnectedClient, String>, Unit>
//    ) {
//        val result = try {
//            val message = KatanJson.toInMessage(rawRequest)
//
//            val request = message.payload
//
//
//            val channel = ChannelId(request.channel) // TODO: Pass in as channel id. Never lobby
//            val command = toCommand(request)
//
//
//            val response = (services[command::class] as? CommandDispatcher<Command, Channel<*>>)
//                ?.handleCommand(client, channel, command, request.seq)
//                ?: throw IllegalArgumentException("Unknown command type: ${command::class}")
//            response
//
//        } catch (e: Exception) {
//            handleError(client, code = ErrorCode.FMT)
//        }
//
//        callback(result.mapValues { KatanJson.toJson(it.value) })
//
////        callback(result.flatMap { (c, r) -> c.sessions.map { s -> s to r } }
////            .toMap() // TODO: Maybe better to have just s here (not s.id), as we then need to lookup the session again ...
////            .mapValues { KatanJson.toJson(it.value) })
//    }
//
//    private fun handleUnauth(client: ConnectedClient, request: Request): Map<Session, String> {
//        return when (request) {
//            is Request.Register -> authService.handleFreshRegister(client, AuthCredentials.User(request.username))
//            is Request.GuestRegister -> authService.handleFreshRegister(client, AuthCredentials.Guest(request.name))
//            is Request.Resume -> authService.handleResume(client, ResumeToken(request.token))
//            else -> handleError(handle, code = ErrorCode.UNAUTHENTICATED)
//        }
//    }
//
//    private fun handleAuth() {
//
//    }
//
//    private fun toCommand(request: Request): Command {
//        return when (request) {
//            is Request.Build -> Build(
//                buildKind = request.buildkind,
//                coordinates = request.coordinates
//            )
//
//            is Request.BuildInitSettl -> BuildInitSettlment(
//                coordinates = request.coordinates
//            )
//
//            is Request.Chat -> Chat(request.message)
//            is Request.ClaimVictory -> TODO()
//            is Request.Create -> CreateGroup(request.settings)
//            is Request.EndTurn -> TODO()
//            is Request.GuestRegister -> Register(AuthCredentials.Guest(request.name))
//            is Request.Init -> InitGame
//            is Request.InitTrade -> TODO()
//            is Request.Join -> JoinGroup
//            is Request.Leave -> LeaveGroup
//            is Request.MoveRobber -> TODO()
//            is Request.Register -> Register(AuthCredentials.User(request.username))
//            is Request.RespondTrade -> TODO()
//            is Request.RollDice -> RollDice
//            is Request.Steal -> TODO()
//        }
//    }
//}
