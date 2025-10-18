package io.github.petvat.katan.shared.model

/**
 * Unique Session identifier associated with a client connection.
 */
@JvmInline
value class SessionId(val value: String)


@JvmInline
value class GameId(val value: String)

@JvmInline
value class PlayerId(val value: Int)


@JvmInline
value class ClientId(val value: String)
