package io.github.petvat.katan.server.service.channel

import io.github.petvat.katan.server.service.client.ClientId
import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap


@JvmInline
value class ResumeToken(val value: String) {
    companion object {
        fun generate(): ResumeToken {
            val bytes = ByteArray(32)
            SecureRandom().nextBytes(bytes)
            return ResumeToken(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes))
        }
    }
}


// TODO: USE REGISTRY?
class ResumeTokenStore(
    private val ttl: Duration = Duration.ofMinutes(5)
) {
    private data class Entry(val clientId: ClientId, val expiresAt: Instant)

    private val tokens = ConcurrentHashMap<ResumeToken, Entry>()

    fun issue(clientId: ClientId): ResumeToken {
        val token = ResumeToken.generate()
        tokens[token] = Entry(clientId, Instant.now().plus(ttl))
        return token
    }

    fun resolve(token: ResumeToken): ClientId? {
        val entry = tokens[token] ?: return null
        if (Instant.now().isAfter((entry.expiresAt))) {
            tokens.remove(token)
            return null
        }
        return entry.clientId
    }

    /**
     * Removes stale token
     */
    fun revoke(token: ResumeToken) {
        tokens.remove(token)
    }

    fun revokeAllFor(clientId: ClientId) {
        tokens.entries.removeIf { it.value.clientId == clientId }
    }
}
