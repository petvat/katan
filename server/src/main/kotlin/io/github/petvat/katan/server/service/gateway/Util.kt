package io.github.petvat.katan.server.service.gateway

import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.InMessage
import io.github.petvat.katan.shared.protocol.OutMessage
import io.github.petvat.katan.shared.protocol.Response
import io.ktor.http.ContentType
import jdk.internal.joptsimple.internal.Messages.message

fun handleErrorResponse(
    userId: UserId,
    code: ErrorCode,
    detail: String? = null,
): Map<UserId, Response> {
    return mapOf(
        userId to Response.Error(
            code = code,
            detail = detail ?: "No description"
        )
    )
}

fun <T> handleError(
    message: InMessage,
    recipient: T,
    code: ErrorCode,
    detail: String? = null
): Map<T, OutMessage> {
    return mapOf(
        recipient to OutMessage(
            replyTo = message.seq,
            channelSeq = null,
            payload = Response.Error(
                code = code,
                detail = detail ?: "No description."
            )
        )
    )
}
