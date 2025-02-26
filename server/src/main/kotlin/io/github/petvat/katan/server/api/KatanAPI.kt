package io.github.petvat.katan.server.api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.server.client.*
import io.github.petvat.katan.server.group.*
import io.github.petvat.katan.server.nio.clientLockMap
import io.github.petvat.katan.server.nio.wrapWithMutex
import io.github.petvat.katan.shared.model.SessionId
import io.github.petvat.katan.shared.protocol.*
import io.github.petvat.katan.shared.protocol.dto.PublicGroupDTO

import json.KatanJson
import kotlinx.coroutines.sync.Mutex
import kotlin.reflect.KSuspendFunction1

private typealias RequestHandler = suspend (client: ConnectedClient, request: Request) -> Map<SessionId, Response>

object KatanApi {

    suspend fun handleError(
        sid: SessionId,
        requestId: Int?,
        code: ErrorCode,
        description: String?
    ): Map<SessionId, Response.Error> {
        return mapOf(
            sid to Response.Error(
                requestId = requestId ?: -1,
                code = code,
                description = description ?: "No description."
            )
        )
    }

    private val logger = KotlinLogging.logger { }

    /**
     * Avoid race conditions by aquiring its lock from this point. TODO: Use this!
     */
    private val groupLockMap = mutableMapOf<GroupId, Mutex>()


//@Suppress("UNCHECKED_CAST")
//private val loginProcessor: RequestProcessor = { sid, _, _, request ->
//    request as Message<Request.Login>
//    val userId = UserService.authenticate(request.payload.data!!.username, request.payload.data!!.password)
//    val user = UserService.getUser(userId)
//
//    // TODO: Client state! UPDATE
//
//    val response = mutableMapOf<SessionId, Message<Response.Login>>()
//    response[sid] = MessageFactory.create(
//        messageType = MessageType.LOGIN,
//        description = "Login sucessful.",
//        success = true,
//        data = Response.Login(user.toPrivate(), "TOKEN: TODO")
//    )
//    response
//}

//private val getGroupsProcessor: RequestProcessor = { sid, _, _, _ ->
//
//    val response = mutableMapOf<SessionId, Message<Response.Groups>>()
//    response[sid] = MessageFactory.create(
//        messageType = MessageType.GET_GROUPS,
//        description = "Retrieved groups from server.",
//        success = true,
//        data = Response.Groups(GroupService.getGroupsPublic()) // TODO: Buffer overflow!
//    )
//    response
//}


    private val registerAsGuest: RequestHandler = { client, request ->
        val sid = client.sessionId
        request as Request.GuestRegister

        updateClient(sid) {
            client.copy(auth = GuestAuth(request.name))
        }
        mapOf(sid to Response.Registered(sid.value, "Registered as guest"))
    }

    private val actionProcessor: RequestHandler = { client, request ->
        val sid = client.sessionId
        val gid = client.activity.groupId!!
        val game = GroupService.getGameFromGroupId(gid)!!
        wrapWithMutex(groupLockMap[gid]!!) {
            ActionAPI.serviceRequest(sid, request, game)
        }.invoke()
    }

    private val joinProcessor: RequestHandler = { client, request ->
        request as Request.Join
        val sid = client.sessionId
        val gid = GroupId(request.groupId)
        val group = GroupService.groups[gid]

        if (group == null) {
            handleError(sid, request.requestId, code = ErrorCode.NOT_FOUND, "Group does not exist.")
        } else {
            // Simple first, no names, no GroupMember, just SessionId.
            // Enure that join matches anon and non-anon properly. Anon can not join non-anon and vice versa.

            wrapWithMutex(groupLockMap[gid]!!) {

                try {
                    GroupService.addToGroup(client, group)
                } catch (e: IllegalArgumentException) {
                    handleError(sid, request.requestId, ErrorCode.DENIED, e.message)
                }

                updateClient(sid) {
                    client.copy(
                        activity = InGroup(gid)
                    )
                }

                // Subscriber model?
                val recipients = group.clients.keys
                val message = Response.UserJoined(
                    sessionId = sid.value,
                    name = "TODO: name.", // NOTE: Might be better to use GET for this. For TCP, idk.
                    description = "User joined."
                )

                val responses: MutableMap<SessionId, Response> = recipients.associateWith { message }.toMutableMap()
                responses[sid] = Response.Joined(
                    groupDTO = GroupService.toPrivate(group = group, excluding = sid),
                    description = "Joined group."
                )

                responses + lobbyPublish(sid, GroupService.toPublic(group))
            }.invoke()

        }
    }

    /**
     * Simple fix for lobby broadcasts. Later it makes sense build a more sophisticated pub/sub system. Maybe redis.
     */
    private fun lobbyPublish(exluding: SessionId, group: PublicGroupDTO): Map<SessionId, Response.LobbyUpdate> {
        return ClientRepository.clients.values.filter { it.activity == Idle && it.sessionId != exluding }.associate {
            it.sessionId to Response.LobbyUpdate(group, "Group updated.")
        }
    }

    private val initProcessor: RequestHandler = { client, _ ->
        val gid = client.activity.groupId!!
        val sid = client.sessionId
        val group = GroupService.groups[gid]!!
        val game = GroupService.elevateToGame(gid)!!

        wrapWithMutex(groupLockMap[gid]!!) {

            val upd: (id: SessionId) -> Unit = { id ->
                updateClient(id) {
                    ClientRepository.getClient(id)!!.copy(
                        activity = InGroup(groupId = gid)
                    )
                }
            }
            // We don't need to lock, because the clients are already indirectly blocked with the group lock
            group.clients.keys.forEach { upd(it) }

            group.clients.keys.associateWith {
                Response.Init(
                    game.viewGame(it), // pov of player
                    description = "Game has started!",
                )
            } + lobbyPublish(sid, GroupService.toPublic(group)) // TODO: Sid is not necessary here. Kind of ugly.


        }.invoke()
    }

    private val chatProcessor: RequestHandler = { client, request ->
        request as Request.Chat
        val gid = client.activity.groupId!!
        val sid = client.sessionId
        val group = GroupService.groups[gid]!!

        wrapWithMutex(groupLockMap[gid]!!) {
            val recipients = group.clients.keys.filter { it.value != sid.value }

            val responses: MutableMap<SessionId, Response> = recipients.associateWith {
                Response.Chat(
                    sid.value,
                    request.message,
                    null
                )
            }.toMutableMap()

            responses[sid] = Response.OK(request.requestId, "Chat success.")
            responses
        }.invoke()
    }

    private val leaveProcessor: RequestHandler = { client, request ->
        val gid = client.activity.groupId!!
        val sid = client.sessionId
        wrapWithMutex(groupLockMap[gid]!!) {
            GroupService.removeFromGroup(sid, gid)
            val group = GroupService.groups[gid]!!;

            val responseToOthers = Response.Left(sid.value, "User left.")
            val recipients = group.clients.keys.filter { it.value != sid.value }

            val responses: MutableMap<SessionId, Response> = recipients.associateWith {
                responseToOthers
            }.toMutableMap()

            responses[sid] = Response.OK(request.requestId, "Leave success.")
            responses + lobbyPublish(exluding = sid, GroupService.toPublic(group))
        }.invoke()
    }


    private val createProcessor: RequestHandler = { client, request ->
        request as Request.Create
        val sid = client.sessionId

        val newGroup = GroupService.addGroup(client, request.settings)
        val responses = mutableMapOf<SessionId, Response>()

        val responseToSender = mapOf(
            sid to Response.GroupCreated(
                groupId = newGroup.id.value,
                level = client.auth.level,
                newGroup.settings,
                "Success group create."
            )
        )

        // TODO: Add off-radar mode (silent)

        updateClient(sid) {
            client.copy(activity = InGroup(newGroup.id))
        }
        responseToSender + lobbyPublish(sid, GroupService.toPublic(newGroup))
    }

    private fun updateClient(sessionId: SessionId, upd: () -> ConnectedClient) {
        ClientRepository.updateClient(sessionId, upd())
    }

    /**
     * Chooses the appropriate processor to execute a request based on the state of the requester.
     */
    private suspend fun processor(state: ConnectedClient, request: Request): Map<SessionId, Response> {
        return if (request.type in state.getAllowedRequests()) {
            val processors = mapOf(
                MTypes.REQ_INIT to initProcessor,
                MTypes.REQ_JOIN to joinProcessor,
                MTypes.REQ_CHAT to chatProcessor,
                MTypes.REQ_LEAVE to leaveProcessor,
                MTypes.REQ_GAMEACTION to actionProcessor,
                MTypes.REQ_CREATE to createProcessor,
                MTypes.REQ_REG_GST to registerAsGuest
            )
            processors[request.type]?.invoke(state, request)
                ?: throw IllegalArgumentException("Unhandled request type.")
        } else {
            handleError(
                state.sessionId,
                request.requestId,
                ErrorCode.DENIED,
                "The request is not supported in current state."
            )
        }
    }


    /**
     * Handles a request from a user from a channel.
     *
     * @param json The request in JSON format
     * @param sid The session ID of the requester
     * @param callback Callback function run with the
     */
    suspend fun handleRequest(
        json: String,
        sid: SessionId,
        callback: KSuspendFunction1<Map<SessionId, String>, Unit>
    ) {
        val request: Request
        var responses: Map<SessionId, Response>

        try {
            request = KatanJson.toRequest(json)
            // Refact
            try {
                responses = ClientRepository.getClient(sid)?.let {
                    wrapWithMutex(clientLockMap.computeIfAbsent(sid) { Mutex() }) { // HACK: clientLockMap has to be defined here I think!
                        processor(
                            it,
                            request
                        )
                    }.invoke()
                } ?: handleError(sid, request.requestId, code = ErrorCode.NOT_FOUND, "Client does not exist")

            } catch (e: Exception) {
                logger.error { "${e.cause}: ${e.message}" }
                responses =
                    handleError(sid, request.requestId, code = ErrorCode.DENIED, "Internal error. DEBUG: ${e.message}")
            }

        } catch (e: Exception) {
            logger.error { "Parsing: Invalid request fmt. " }
            responses = handleError(sid, null, code = ErrorCode.FMT, "Invalid request format.")
        }

        callback(responses.mapValues { (_, response) -> KatanJson.toJson(response) })
    }

//suspend fun handleRequest(
//    json: String,
//    sid: SessionId,
//    callback: suspend (Map<SessionId, Message<Response>>) -> Unit
//) {
//    try {
//
//        val request = KatanJson.jsonToRequest(json)
//        val (validator, processor) = getValidatorAndProcessor(request.header.messageType)
//        val stateToken = TokenManager.verifyToken(request.header.token!!)
//        if (stateToken != null && !validator(stateToken)) {
//            // return early
//        }
//        val responses = processor(clientID, stateToken?.groupId ?: "", request) // TODO: Fix ""
//        callback(responses)
//    } catch (e: Exception) {
//
//    }
//
//
//}

}

