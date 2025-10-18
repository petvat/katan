package io.github.petvat.katan.server.service.service

import io.github.petvat.katan.server.api.handleError
import io.github.petvat.katan.server.service.channel.Channel
import io.github.petvat.katan.server.service.channel.ChannelId
import io.github.petvat.katan.server.service.client.Auth
import io.github.petvat.katan.server.service.client.AuthCredentials
import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.server.service.command.*
import io.github.petvat.katan.server.service.session.Session
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Request
import json.KatanJson
import kotlin.reflect.KSuspendFunction1


class RequestProcessor(
    lobbyService: LobbyService,
    groupService: GroupService,
    gameService: GameService,
) {

    private val services = mapOf(
        GameCommand::class to gameService,
        LobbyCommand::class to lobbyService,
        GroupCommand::class to groupService,
    )

    @Suppress("UNCHECKED_CAST")
    suspend fun handle(
        client: ConnectedClient,
        rawRequest: String,
        callback: KSuspendFunction1<Map<Session, String>, Unit>
    ) {
        val result = try {
            val request = KatanJson.toRequest(rawRequest)
            val channel = ChannelId(request.channel) // TODO: Pass in as channel id.
            val command = toCommand(request)

            val response = (services[command::class] as? CommandDispatcher<Command, Channel<*>>)
                ?.handleCommand(client, channel, command, request.seq)
                ?: throw IllegalArgumentException("Unknown command type: ${command::class}")
            response

        } catch (e: Exception) {
            handleError(client, code = ErrorCode.FMT)
        }
        callback(result.flatMap { (c, r) -> c.sessions.map { s -> s to r } }
            .toMap() // TODO: Maybe better to have just s here (not s.id), as we then need to lookup the session again ...
            .mapValues { KatanJson.toJson(it.value) })
    }

    private fun toCommand(request: Request): Command {
        val channelId = ChannelId(request.channel)
        return when (request) {
            is Request.Build -> Build(
                gameId = channelId,
                buildKind = request.buildkind,
                coordinates = request.coordinates
            )

            is Request.BuildInitSettl -> TODO()
            is Request.Chat -> TODO()
            is Request.ClaimVictory -> TODO()
            is Request.Create -> TODO()
            is Request.EndTurn -> TODO()
            is Request.GuestRegister -> TODO()
            is Request.Init -> TODO()
            is Request.InitTrade -> TODO()
            is Request.Join -> TODO()
            is Request.Leave -> TODO()
            is Request.MoveRobber -> TODO()
            is Request.Register -> Register(AuthCredentials.User(request.username))
            is Request.RespondTrade -> TODO()
            is Request.RollDice -> TODO()
            is Request.Steal -> TODO()
        }
    }
}
