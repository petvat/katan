package io.github.petvat.katan.server.api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.server.service.concurrency.ConnectedClient

import io.github.petvat.katan.server.group.GroupId
import io.github.petvat.katan.shared.model.SessionId
import io.github.petvat.katan.shared.protocol.ErrorCode
import io.github.petvat.katan.shared.protocol.MTypes
import io.github.petvat.katan.shared.protocol.Request
import io.github.petvat.katan.shared.protocol.Response
import json.KatanJson
import kotlin.reflect.KSuspendFunction1

//
//class RequestDispatcher(
//    private val requestRegistry: RequestHandlerRegistry,
//) {
//    private val logger = KotlinLogging.logger { }
//
//    suspend fun handleRequest(
//        raw: String,
//        client: ConnectedClient, // What about passing in a ConnectedClient?
//        callback: KSuspendFunction1<Map<SessionId, String>, Unit>
//    ) {
//        val sid = client.id
//
//        suspend fun processResponses(): Map<SessionId, Response> {
//            val request = parseRequest(raw) ?: return handleError(sid, null, ErrorCode.FMT, "Invalid request format")
//
//            val handler = requestRegistry.getHandler(request.type) ?: return handleError(
//                sid, request.seq, ErrorCode.DENIED, "Unhandled request type"
//            )
//
//            return executeSafely(handler, client, request)
//        }
//
//        callback(processResponses().mapValues { (_, response) -> KatanJson.toJson(response) })
//    }
//
//
//    /**
//     * Interesting idea - dispatcher prepares all resources, handlers execute in total isolation, does not care about locking.
//     * Problem is to determine all locks before processing. Have to disect some of it before (groupId validation).
//     */
//    private fun lockTarget(client: ConnectedClient, request: Request): GroupId? {
//        return when (request.type) {
//            MTypes.REQ_GAMEACTION, MTypes.REQ_CHAT, MTypes.REQ_INIT, MTypes.REQ_LEAVE -> client.activity.groupId
//            MTypes.REQ_JOIN -> {
//                GroupId((request as Request.Join).groupId) // Must check if group exists
//            }
//
//            else -> null
//        }
//    }
//
//    /**
//     * Executes processor, catching all exceptions.
//     */
//    private suspend fun executeSafely(
//        handler: RequestHandler,
//        client: ConnectedClient,
//        request: Request,
//    ): Map<SessionId, Response> {
//        return try {
//            handler(client, request)
////            lockTarget(client, request)?.let {
////                lockManager.callInMutex(it) {
////                    handler(client, request)
////                }
////            } ?: handler(client, request)
//        } catch (e: Exception) {
//            logger.error { "${e.cause}: ${e.message}" }
//            handleError(client.sessionId, request.seq, code = ErrorCode.DENIED, "Internal error")
//        }
//    }
//
//    private fun parseRequest(raw: String): Request? {
//        return try {
//            KatanJson.toRequest(raw)
//        } catch (e: Exception) {
//            null
//        }
//    }
//}
