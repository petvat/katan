package io.github.petvat.katan.server.service.gateway

import io.github.petvat.katan.server.service.channel.*
import io.github.petvat.katan.server.service.client.*
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response
import io.github.petvat.katan.shared.protocol.dto.GroupExternal
import java.util.*

class AuthService(
    ctx: ServerContext,
    val lobbyChannel: LobbyChannel
) : CommandContext by ctx {

    suspend fun handleResume(connection: Connection, token: ResumeToken): Response {
        val clientId = resumeTokenStore.resolve(token)
            ?: return Response.ResumeFailed(reason = "Token expired or unknown — please log in again.")

        val client = clientRegistry.get(clientId)
            ?: return Response.ResumeFailed(reason = "Session expired — please log in again.")

        // Attach the new connection, cancel the pending expiry timer
        clientRegistry.markReconnected(clientId, connection)

        resumeTokenStore.revoke(token)
        val freshToken = resumeTokenStore.issue(clientId)

        return Response.Resumed(resumeToken = freshToken.value)
    }


    suspend fun handleFreshRegister(
        client: ConnectedClient,
        credentials: AuthCredentials
    ): Response {
        if (client.auth !is Auth.Unauth) {
            return Response.Error(code = ErrorCode.ALREADY_AUTHENTICATED, "Already registered.")
        }

        val auth = when (credentials) {
            is AuthCredentials.Guest -> Auth.Guest(name = credentials.name, id = generateUserId(IdType.GUEST))
            is AuthCredentials.User -> resolveRegisteredUser(credentials)
                ?: return Response.Error(code = ErrorCode.DENIED, "Invalid credentials.")

            is AuthCredentials.Admin -> resolveAdmin(credentials)
                ?: return Response.Error(code = ErrorCode.DENIED, "Invalid admin credentials.")
        }

        client.auth = auth
        userRegistry.register(auth.id, auth)
        clientRegistry.link(auth, client.id)

        // Subscribe to lobby
        lobbyChannel.subscribe(auth.id, LobbySubscriber.Member)

        val token = resumeTokenStore.issue(client.id)

        // TODO: Weird place and hacky retrieval
        val existingGroups = channelManager
            .all()
            .filterIsInstance<GroupChannel>()
            .filter { it.lobby.id == lobbyChannel.id }
            .map { GroupExternal(it.id.value, it.subs.size, it.settings.maxPlayers, it.settings.gameMode) }


        return Response.Registered(
            userId = auth.id.value,
            name = auth.name,
            clientId = client.id.value,
            resumeToken = token.value,
            lobby = lobbyChannel.id.value,
            existingGroups = existingGroups
        )
    }

    private fun resolveRegisteredUser(credentials: AuthCredentials.User): Auth.User {
        val id = generateUserId(IdType.USER)
        val user = Auth.User(credentials.name, credentials.psw, id)
        return user
    }

    private fun resolveAdmin(credentials: AuthCredentials.Admin): Auth.User? {
        TODO()
    }
}
