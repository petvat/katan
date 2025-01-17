package io.github.petvat.katan.server.api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.server.client.*
import io.github.petvat.katan.server.group.*
import io.github.petvat.katan.server.nio.clientLockMap
import io.github.petvat.katan.server.nio.wrapWithMutex
import io.github.petvat.katan.shared.protocol.SessionId
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.model.session.PublicUserView
import io.github.petvat.katan.shared.protocol.*
import io.github.petvat.katan.shared.protocol.dto.ActionRequest
import io.github.petvat.katan.shared.protocol.dto.Request
import io.github.petvat.katan.shared.protocol.dto.Response
import io.github.petvat.katan.shared.protocol.json.KatanJson
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KSuspendFunction1

private val logger = KotlinLogging.logger { }

//private typealias RequestValidator = (token: StateToken?) -> Boolean
//private typealias RequestProcessor = (token: StateToken, request: Message<Request>) -> Map<Int, Message<Response>>

//private val authValidator: RequestValidator = { token -> token!!.permit == Permits.AUTH }
//private val inGroupValidator: RequestValidator = { token -> token!!.permit == Permits.IN_GROUP }
//private val playingValidator: RequestValidator = { token -> token!!.permit == Permits.PLAYING }


/**
 * Avoid race conditions by aquiring its lock from this point.
 */
private val groupLockMap = mutableMapOf<GroupId, Mutex>()

// private typealias StateValidator = suspend (state: ClientState) -> Boolean
private typealias RequestProcessor = suspend (sid: SessionId, uid: UserId?, gid: GroupId?, request: Message<Request>) -> Map<SessionId, Message<Response>>

//private val authValidator: StateValidator = { state -> state is LoggedInState }
//private val inGroupValidator: StateValidator = { state -> state is InGroupState }
//private val playingValidator: StateValidator =
//    { state -> state is InGroupState && GroupService.groups[state.groupId] is Game }
//private val initValidator: StateValidator =
//    { state -> state is InGroupState && GroupService.groups[state.groupId] !is Game }
//private val guestValidator: StateValidator = { state -> state is GuestState }


@Suppress("UNCHECKED_CAST")
private val loginProcessor: RequestProcessor = { sid, _, _, request ->
    request as Message<Request.Login>
    val userId = UserService.authenticate(request.payload.data!!.username, request.payload.data!!.password)
    val user = UserService.getUser(userId)

    // TODO: Client state! UPDATE

    val response = mutableMapOf<SessionId, Message<Response.Login>>()
    response[sid] = MessageFactory.create(
        messageType = MessageType.LOGIN,
        description = "Login sucessful.",
        success = true,
        data = Response.Login(user.toPrivate(), "TOKEN: TODO")
    )
    response
}

private val getGroupsProcessor: RequestProcessor = { sid, uid, gid, request ->
    TODO()
}

private val actionProcessor: RequestProcessor = { sid, _, gid, request ->
    val game = GroupService.getGameFromGroupId(gid!!)!!
    // TODO: Refact format
    // val user = ClientService.getClient(sid) as LoggedInState

    wrapWithMutex(game.mutex) {
        ActionAPI.serviceRequest(sid, request.payload.data!! as ActionRequest, game)
            .mapValues { payl ->
                Message(
                    header = request.header,
                    payload = payl.value
                )
            }
    }.invoke()
}

@Suppress("UNCHECKED_CAST")
private val joinProcessor: RequestProcessor = { sid, uid, gid, request ->
    request as Message<Request.Join>
    val client = ClientService.getClient(sid) ?: TODO("Return failure: User not found") // Either anon or user

    val group = GroupService.groups[gid] ?: TODO("Return failure: Group not found")

    if (group.level != client.level) {
        TODO("In validator")
    }

    // Simple first, no names, no GroupMember, just SessionId.
    // Enure that join matches anon and non-anon properly. Anon can not join non-anon and vice versa.
    val newMember = GroupMember(sid, uid?.let { UserService.getUser(uid).username } ?: "Guest")
    group.add(newMember)
    wrapWithMutex(group.mutex) {
        val recipients = group.clients.keys
        val message = MessageFactory.create(
            messageType = MessageType.JOIN,
            data = Response.Join(
                group.view(sid),
                PublicUserView(newMember.id.value, newMember.name)
            ), // TODO: Refact PublicUserInfo
            success = true,
            description = "${client.sessionId} joined session!"
        )
        val responses = recipients.associateWith { message }.toMutableMap()
        responses[sid] =
            MessageFactory.create(
                messageType = MessageType.JOIN,
                data = Response.Join(group.view(sid)),
                success = true,
                description = "${newMember.name} joined session!",
            )
        val upd: (id: SessionId) -> Unit = { id ->
            updateClient(id) {
                InGroupState(
                    base = ClientService.getClient(sid)!!,
                    groupId = gid!!
                )
            }
        }

        // Update all group clients to IngroupState. Assert update is mutexed
        // Since Mutex is non-reentrant avoid deadlocking
        group.clients.keys.forEach { sessId ->
            if (sessId == sid) {
                upd(sessId)
            } else {
                wrapWithMutex(clientLockMap[sessId]!!) { upd(sessId) }
            }
        }
        responses
    }.invoke()

}

private val initProcessor: RequestProcessor = { sid, _, gid, _ ->
    val group = GroupService.groups[gid]!!
    val game = GroupService.elevateToGame(gid!!)!!
    group.mutex.withLock {
        val responses = group.clients.keys.associateWith {
            MessageFactory.create(
                groupId = gid.value,
                messageType = MessageType.INIT,
                description = "Game has started!",
                success = true,
                data = Response.Init(game.viewGame(it))
            )
        }
        group.clients
        updateClient(sid) {
            PlayingState(
                base = ClientService.getClient(sid) as InGroupState
            )
        }
        responses
    }
}

@Suppress("UNCHECKED_CAST")
private val chatProcessor: RequestProcessor = { sid, _, gid, request ->
    request as Message<Request.Chat>
    val group = GroupService.groups[gid]!!

    group.mutex.withLock {
        val recipients = group.clients.keys.filter { id -> request.payload.data!!.recipients.contains(id.value) }
        val message = MessageFactory.create(
            messageType = MessageType.CHAT,
            description = "Chat message received",
            success = true,
            data = Response.Chat(sid.value, request.payload.data!!.message)
        )
        val responses = recipients.associateWith { message }.toMutableMap()
        responses[sid] = MessageFactory.create( // TODO: Find a better way than this
            messageType = MessageType.CHAT,
            description = "Chat was sent.",
            success = true,
            data = null
        )
        responses
    }
}

@Suppress("UNCHECKED_CAST")
private val createProcessor: RequestProcessor = { sid, uid, _, request ->
    request as Message<Request.Create>
    val client = ClientService.getClient(sid)!!

    // TODO: Add some way to specify guest mode, automatic?

    val newGroup = GroupService.addGroup(client, request.payload.data!!.settings)
    val responses = mutableMapOf<SessionId, Message<Response.Create>>()

    val responseToSender = MessageFactory.create(
        messageType = MessageType.CREATE,
        data = Response.Create(
            groupId = newGroup.id.value,
            level = client.level,
            settings = request.payload.data!!.settings
        ),
        success = true,
        description = "You successfully created a session.",
    )
    val broadcastedResponse = MessageFactory.create(
        messageType = MessageType.CREATE,
        data = Response.Create(
            groupId = newGroup.id.value,
            level = client.level,
            settings = request.payload.data!!.settings
        ),
        success = true,
        description = "$sid created a session and has broadcasted it to you."
    )
    responses[sid] = responseToSender
    updateClient(sid) {
        InGroupState(
            base = ClientService.getClient(sid)!!,
            newGroup.id
        )
    }

    responses
}

//private suspend fun getValidatorAndProcessor(messageType: MessageType): Pair<StateValidator, RequestProcessor> {
//            return when (messageType) {
//                MessageType.CHAT -> inGroupValidator to chatProcessor
//                MessageType.ACTION -> playingValidator to actionProcessor
//                MessageType.JOIN -> authValidator to joinProcessor
//                MessageType.CREATE -> authValidator to createProcessor
//                MessageType.INIT -> initValidator to initProcessor
//                MessageType.LOGIN -> Pair(guestValidator, loginProcessor)
//                MessageType.GET_SESSION -> authValidator to getGroupsProcessor
//    }
//}

private fun updateClient(sessionId: SessionId, upd: () -> ClientState) {
    ClientService.updateClient(sessionId, upd())
}

/**
 * Returns an empty response as default if a request was not accepted.
 */
private fun invalidStateResponse(
    sessionId: SessionId,
    request: Message<Request>
): Map<SessionId, Message<Response.Empty>> {
    val response = mutableMapOf<SessionId, Message<Response.Empty>>()
    response[sessionId] = MessageFactory.create(
        messageType = request.header.messageType,
        data = Response.Empty,
        success = false,
        description = "Cannot perform request in the current state."
    )
    return response
}

/**
 * Chooses the appropriate processor to execute a request based on the state of the requester.
 */
private suspend fun processor(state: ClientState, request: Message<Request>): Map<SessionId, Message<Response>> {
    return if (request.header.messageType in state.getAllowedMessageTypes()) {
        when (request.header.messageType) {
            MessageType.INIT -> {
                state as InGroupState
                initProcessor(state.sessionId, null, state.groupId, request)
            }

            MessageType.CHAT -> {
                state as InGroupState
                chatProcessor(state.sessionId, null, state.groupId, request)
            }

            MessageType.ACTION -> {
                state as PlayingState
                actionProcessor(state.sessionId, null, state.base.groupId, request)
            }

            MessageType.JOIN -> {
                joinProcessor(state.sessionId, if (state is LoggedInState) state.userId else null, null, request)
            }

            MessageType.CREATE -> {
                createProcessor(state.sessionId, if (state is LoggedInState) state.userId else null, null, request)

            }

            MessageType.LOGIN -> {
                loginProcessor(state.sessionId, null, null, request)
            }

            MessageType.GET_GROUPS -> {
                getGroupsProcessor(state.sessionId, null, null, request)
            }

            MessageType.ACK -> throw IllegalArgumentException("ACK is not supported yet.")
        }
    } else {
        invalidStateResponse(state.sessionId, request)
    }
}

suspend fun handleRequest(
    json: String,
    sid: SessionId,
    callback: KSuspendFunction1<Map<SessionId, String>, Unit>
) {
    val request: Message<Request>
    try {
        request = KatanJson.jsonToRequest(json)
        try {
            val client = ClientService.getClient(sid)!!;
            // {} -> ({}) -> (a -> x)
            val responses =
                wrapWithMutex(clientLockMap.computeIfAbsent(sid) { Mutex() }) { processor(client, request) }.invoke()
                    .mapValues { (_, res) -> KatanJson.messageToJson(res) }
            callback(responses)
        } catch (e: Exception) {
            val response = mutableMapOf<SessionId, String>()
            response[sid] = KatanJson.messageToJson(
                MessageFactory.create(
                    messageType = request.header.messageType,
                    data = Response.Empty,
                    success = false,
                    description = e.message ?: "Unknown exception occurred."
                )
            )
            callback(response)
        }
    } catch (e: Exception) {
        logger.error { "Unrecoverable exception. Not enough data to callback (or it has not been implemented yet). ${e.message}, ${e.cause}" }
    }
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
