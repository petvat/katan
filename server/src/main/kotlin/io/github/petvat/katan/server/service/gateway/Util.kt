package io.github.petvat.katan.server.service.gateway

import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.Response

fun handleError(
    userId: UserId,
    code: ErrorCode,
    detail: String? = null
): Map<UserId, Response> {
    return mapOf(
        userId to
            Response.Error(
                code = code,
                detail = detail ?: "No description."
            )
    )
}
