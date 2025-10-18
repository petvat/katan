package io.github.petvat.katan.server.api

import io.github.petvat.katan.server.service.client.ConnectedClient
import io.github.petvat.katan.shared.model.SessionId
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response

fun handleError(
    sid: SessionId,
    requestId: Int? = null,
    code: ErrorCode,
    description: String? = null
): Map<SessionId, Response.Error> {
    return mapOf(
        sid to Response.Error(
            seq = requestId ?: -1,
            code = code,
            description = description ?: "No description."
        )
    )
}


fun handleError(
    connectedClient: ConnectedClient,
    requestId: Int? = null,
    code: ErrorCode,
    description: String? = null
): Map<ConnectedClient, Response.Error> {
    return mapOf(
        connectedClient to Response.Error(
            seq = requestId ?: -1,
            code = code,
            description = description ?: "No description."
        )
    )
}
