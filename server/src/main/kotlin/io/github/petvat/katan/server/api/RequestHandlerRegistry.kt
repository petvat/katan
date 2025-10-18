package io.github.petvat.katan.server.api


//import io.github.petvat.katan.server.client.*
//import io.github.petvat.katan.server.group.Group
//import io.github.petvat.katan.server.group.GroupId
//import io.github.petvat.katan.server.group.GroupManager
//import io.github.petvat.katan.shared.model.SessionId
//import io.github.petvat.katan.shared.protocol.ErrorCode
//import io.github.petvat.katan.shared.protocol.MTypes
//import io.github.petvat.katan.shared.protocol.Request
//import io.github.petvat.katan.shared.protocol.Response
//import io.github.petvat.katan.shared.protocol.dto.PublicGroupDTO
//
//typealias RequestHandler = suspend (client: ConnectedClient, request: Request) -> Map<SessionId, Response>
//// This is weird because of the fact that group could be non-existent when this is called. Must in that case be called within
//// callInMutex(gid) { group = getGroup(gid); handler(client, group, request); }
//// Yeah ... could work but not that much overhead to simply call callInMutex when needed.
//// Could be nice to not be having to write it for all.
//// TODO: Call in group Id mutex
//typealias GroupRequestHandler = suspend (client: ConnectedClient, group: Group, request: Request) -> Map<SessionId, Response>
//
//// TODO: rename to
//class RequestHandlerRegistry(
//    private val lockManager: LockManager,
//    private val groupManager: GroupManager,
//    private val clientManager: ClientManager
//) {
//
//    /**
//     * Not used. But should find a way to remove LockManager in RequestDispatcher.
//     */
//    suspend fun processRequest(client: ConnectedClient, request: Request): Map<SessionId, Response>? {
//        return lockManager.callInMutex(client.sessionId) {
//            handlers[request.type]?.let { it(client, request) }
//        }
//    }
//
//    // TODO: Create wrapper for group mutex, takes
//
//    fun getHandler(type: MTypes) = handlers[type]
//
//    private val registerAsGuest: RequestHandler = { client, request ->
//        val sid = client.sessionId
//        request as Request.GuestRegister
//
//        updateClient(sid) {
//            client.copy(auth = GuestAuth(request.name))
//        }
//        mapOf(sid to Response.Registered(sid.value, request.name, "Registered as guest"))
//    }
//
//    private val actionProcessor: RequestHandler = { client, request ->
//        val sid = client.sessionId
//        val gid = client.activity.groupId!!
//        lockManager.callInMutex(gid) {
//            val game = groupManager.getGame(gid)!!
//            ActionApi.serviceRequest(sid, request, game) // <- GameApi, then
//        }
//    }
//
//    private val joinProcessor: RequestHandler = { client, request ->
//        request as Request.Join
//        val sid = client.sessionId
//        val gid = GroupId(request.groupId)
//
//        // Simple first, no names, no GroupMember, just SessionId.
//        // Enure that join matches anon and non-anon properly. Anon can not join non-anon and vice versa.
//
//        lockManager.callInMutex(gid) {
//            val group = groupManager.groups[gid] ?: return@callInMutex handleError(
//                sid,
//                request.seq,
//                code = ErrorCode.NOT_FOUND,
//                "Group does not exist."
//            )
//
//            try {
//                groupManager.addToGroup(client, group)
//            } catch (e: IllegalArgumentException) {
//                return@callInMutex handleError(sid, request.seq, ErrorCode.DENIED, e.message)
//            }
//
//            updateClient(sid) {
//                client.copy(
//                    activity = InGroup(gid)
//                )
//            }
//
//            // PubSubService.subscribe()
//            // PubSubService.publish(Topic.Group(gid), Event.Join)
//
//            // Subscriber model?
//            val recipients = group.members.keys
//            val message = Response.UserJoined(
//                sessionId = sid.value,
//                name = (clientManager.getClient(sid)!!.auth as GuestAuth).name, // NOTE: Might be better to use GET for this. For TCP, idk. TODO: REWRITE CONNECTED CLIENT.
//                description = "User joined."
//            )
//
//            val responses: MutableMap<SessionId, Response> = recipients.associateWith { message }.toMutableMap()
//            responses[sid] = Response.Joined(
//                groupDTO = groupManager.toPrivate(group = group, excluding = sid),
//                description = "Joined group."
//            )
//
//            responses + lobbyPublish(sid, groupManager.toPublic(group))
//        }
//    }
//
//    /**
//     * Simple fix for lobby broadcasts. Later it makes sense build a more sophisticated pub/sub system. Maybe redis.
//     */
//    private fun lobbyPublish(exluding: SessionId, group: PublicGroupDTO): Map<SessionId, Response.LobbyUpdate> {
//        return clientManager.clients.values.filter { it.activity == Idle && it.sessionId != exluding }.associate {
//            it.sessionId to Response.LobbyUpdate(group, "Group updated.")
//        }
//    }
//
//    private val initProcessor: RequestHandler = { client, request ->
//        val gid = client.activity.groupId!!
//        val sid = client.sessionId
//
//
//        lockManager.callInMutex(gid) {
//            val group = groupManager.groups[gid] ?: return@callInMutex handleError(
//                sid,
//                request.seq,
//                ErrorCode.DENIED,
//                "Internal error."
//            )
//            val game = groupManager.elevateToGame(gid)!! // <- Already failing
//
//            val upd: (id: SessionId) -> Unit = { id ->
//                updateClient(id) {
//                    clientManager.getClient(id)!!.copy(
//                        activity = InGroup(groupId = gid)
//                    )
//                }
//            }
//            // We don't need to lock, because the members are already indirectly blocked with the group lock
//            group.members.keys.forEach { upd(it) }
//
//            group.members.keys.associateWith {
//                Response.Init(
//                    game.viewGame(it), // pov of player
//                    description = "Game has started!",
//                )
//            } + lobbyPublish(sid, groupManager.toPublic(group)) // TODO: Sid is not necessary here. Kind of ugly.
//
//        }
//    }
//
//    private val chatProcessor: RequestHandler = { client, request ->
//        request as Request.Chat
//        val gid = client.activity.groupId!!
//        val sid = client.sessionId
//
//
//        lockManager.callInMutex(gid) {
//            // val recipients = group.members.keys.filter { it.value != sid.value }
//
//            val group =
//                groupManager.groups[gid] ?: return@callInMutex handleError( // TODO: Remove explicit error handler
//                    sid,
//                    request.seq,
//                    ErrorCode.DENIED,
//                    "Internal error."
//                )
//
//            val responses: MutableMap<SessionId, Response> = group.members.keys.associateWith {
//                Response.Chat(
//                    sid.value,
//                    request.message,
//                    null
//                )
//            }.toMutableMap()
//
//            // responses[sid] = Response.OK(request.seq, "Chat success.")
//            responses
//        }
//    }
//
//    private val leaveProcessor: RequestHandler = { client, request ->
//        val gid = client.activity.groupId!!
//        lockManager.callInMutex(gid) {
//            val sid = client.sessionId
//            groupManager.removeFromGroup(sid, gid)
//            val group = groupManager.groups[gid]!!
//
//            val responseToOthers = Response.Left(sid.value, "User left.")
//            val recipients = group.members.keys.filter { it.value != sid.value }
//
//            val responses: MutableMap<SessionId, Response> = recipients.associateWith {
//                responseToOthers
//            }.toMutableMap()
//
//            responses[sid] = Response.OK(request.seq, "Leave success.")
//            responses + lobbyPublish(exluding = sid, groupManager.toPublic(group))
//        }
//    }
//
//
//    private val createProcessor: RequestHandler = { client, request ->
//        request as Request.Create
//        val sid = client.sessionId
//
//        val newGroup = groupManager.addGroup(client, request.settings)
//        val responses = mutableMapOf<SessionId, Response>()
//
//        val responseToSender = mapOf(
//            sid to Response.GroupCreated(
//                groupId = newGroup.id.value,
//                level = client.auth.level,
//                newGroup.settings,
//                "Success group create."
//            )
//        )
//
//        // TODO: Add off-radar mode (silent)
//
//        // groupLockMap[newGroup.id] = Mutex() // Add lock a for this group, shouldn't be necessary.
//
//        updateClient(sid) {
//            client.copy(activity = InGroup(newGroup.id))
//        }
//        responseToSender + lobbyPublish(sid, groupManager.toPublic(newGroup))
//    }
//
//    private fun updateClient(sessionId: SessionId, upd: () -> ConnectedClient) {
//        clientManager.updateClient(sessionId, upd())
//    }
//
//
//    // NOTE: Maybe wrap in lock somehow? Return a mutex to be used?
//    //  Need to decompose into lock part and logic part.
//    //  In order to lock we need the gid, we ...
//    //  Create a new type of request called GroupRequest, which takes in GroupId?
//    // TODO: Instead of handler below, use switch, then find groupId and perform request here with GroupRequest
//    //  Dispatcher needs groupManager reference?
//    private val handlers = mapOf(
//        MTypes.REQ_INIT to initProcessor,
//        MTypes.REQ_JOIN to joinProcessor,
//        MTypes.REQ_CHAT to chatProcessor,
//        MTypes.REQ_LEAVE to leaveProcessor,
//        MTypes.REQ_GAMEACTION to actionProcessor,
//        MTypes.REQ_CREATE to createProcessor,
//        MTypes.REQ_REG_GST to registerAsGuest
//    )
//}
