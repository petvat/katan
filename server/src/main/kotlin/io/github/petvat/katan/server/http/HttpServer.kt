package io.github.petvat.katan.http

import io.github.petvat.katan.server.api.handleRequest
import io.github.petvat.katan.server.client.*
import io.github.petvat.katan.shared.protocol.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * All active sessions.
 */
val sessions = ConcurrentHashMap<SessionId, WebSocketSession>()

suspend fun sendMessages(messages: Map<SessionId, String>) {
    messages.forEach { (sid, msg) ->
        sessions[sid]?.send(msg)
    }
}

fun Application.configureRouting() {
    install(WebSockets) { }

    routing {

        post("/login") {
            // JWTs
        }

        post("/register") {
            // JWTs
        }

        post("/guest-register") {
            val sessionId = SessionId(UUID.randomUUID().toString())

            val params = call.receiveParameters()

            val name = params["name"] ?: "Guest"

            ClientRepository.addClient(
                ConnectedClient(
                    sessionId = sessionId,
                    auth = GuestAuth(name),
                    activity = Idle
                )
            )

            call.respond(
                mapOf("sessionId" to sessionId), null // ?
            )
        }

        webSocket("/ws") {

            // Streams groups depending on the permission level of the client.
            // JWT?
            // Parameter from header
            val sessionId = SessionId(call.parameters["sid"] ?: run {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Missing session ID"))
                return@webSocket
            })

            sessions[sessionId] = this

            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val json = frame.readText()
                            handleRequest(json, sessionId, ::sendMessages)
                        }

                        is Frame.Close -> {
                            close()
                        }

                        else -> Unit
                    }
                }
            } finally {
                // Cleanup
                sessions.remove(sessionId)
                ClientRepository.removeClient(sessionId)
            }
        }

        webSocket("/group") {

        }
    }
}


