package io.github.petvat.katan.server.service.gateway

import io.github.petvat.katan.server.service.channel.*
import io.github.petvat.katan.server.service.client.*
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response
import java.util.*

class AuthService(
    private val clientRegistry: ClientRegistry,
    private val userRegistry: UserRegistry,
    private val channelRegistry: ChannelRegistry,
    private val lobbyChannel: LobbyChannel, // TODO: Could ask client to specify lobby.
    private val resumeTokenStore: ResumeTokenStore,
) {

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
        // Subscribe to lobby
        lobbyChannel.subscribe(auth.id, LobbySubscriber.Member)

        val token = resumeTokenStore.issue(client.id)
        return Response.Registered(
            name = auth.name,
            clientId = client.id.value,
            resumeToken = token.value,
            lobby = lobbyChannel.id.value
        )
    }

    private fun resolveRegisteredUser(credentials: AuthCredentials.User): Auth.User {
        val id = UserId(generateId(IdType.USER))
        val user = Auth.User(credentials.name, credentials.psw, id)
        return user
    }

    private fun resolveAdmin(credentials: AuthCredentials.Admin): Auth.User? {
        TODO()
    }
}
