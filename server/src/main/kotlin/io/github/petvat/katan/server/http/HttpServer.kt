package io.github.petvat.katan.server.http

import io.github.petvat.katan.server.api.KatanApi
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

// NOTE: Maybe use channels, i.e. a channel for each group -> pub/sub


/**
 * All active sessions.
 */
private val sessions = ConcurrentHashMap<SessionId, WebSocketSession>()

/**
 * Sends back message to each [WebSocketSession].
 */
private suspend fun sendMessages(messages: Map<SessionId, String>) {
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

        // Currently the websocket is exactly the same as the TCP server.
        //
        webSocket("/socket") {

            val sessionId = SessionId(UUID.randomUUID().toString())

            sessions[sessionId] = this

            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val json = frame.readText()
                            KatanApi.handleRequest(json, sessionId, ::sendMessages)
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
    }
}


