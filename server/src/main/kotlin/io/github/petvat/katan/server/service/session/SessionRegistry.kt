package io.github.petvat.katan.server.service.session

import io.github.petvat.katan.shared.model.SessionId
import java.util.concurrent.ConcurrentHashMap

class SessionRegistry {
    private val sessions = ConcurrentHashMap<SessionId, Session>()

    operator fun get(id: SessionId): Session {
        return sessions[id] ?: error("Session $id does not exist.")
    }

    fun add(session: Session) {
        sessions[session.id] = session
    }

    fun remove(id: SessionId) {
        sessions.remove(id)
    }
}
