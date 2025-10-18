package io.github.petvat.katan.server.service.concurrency


//class GameRegistry {
//    private val sessions = ConcurrentHashMap<GameId, GameSession>()
//
//    operator fun get(id: GameId) = sessions[id]
//    operator fun minusAssign(id: GameId) {
//        sessions.remove(id)
//    }
//
//    operator fun plusAssign(gameSession: GameSession) {
//        sessions[gameSession.id] = gameSession
//    }
//}

/**
 * Controlls and synchronizes shared access to all mutable state.
 *
 * TODO: Hm, single responsibility principle? Should separate these?
 */
//class RealmManager(private val lockManager: LockManager) {
////    private val gameSessions = ConcurrentHashMap<GameId, GameSession>()
////    private val groupSessions = ConcurrentHashMap<GroupId, GroupSession>()
////    private val chatSessions = ConcurrentHashMap<ChatScopeId, ChatSession>()
////    private val clientSessions = ConcurrentHashMap<SessionId, ConnectedClient>()
//
//    private val realms = ConcurrentHashMap<RealmId, Realm>()
//
//    operator fun get(id: RealmId) = realms[id]
//    operator fun plusAssign(channel: Realm) {
//        realms[channel.id] = channel
//    }
//
//    operator fun minusAssign(id: RealmId) {
//        realms.remove(id)
//    }

//    /**
//     *
//     */
//    fun getGameSession(gameId: GameId) = gameSessions[gameId]
//    fun getGroupSession(groupId: GroupId) = groupSessions[groupId]
//    fun getChatSession(chatScopeId: ChatScopeId) = chatSessions[chatScopeId]
//

//    fun add(session: Lockable) {
//        when (session) {
//            is GameSession -> gameSessions[session.gameId] = session
//            is GroupSession -> groupSessions[session.groupId] = session
//            is ChatSession -> chatSessions[session.chatScopeId] = session
//        }
//    }

//    fun remove(gameId: GameId) {
//        gameSessions.remove(gameId)
//    }
//
//    fun remove(groupId: GroupId) {
//        groupSessions.remove(groupId)
//    }
//
//    fun remove(chatScopeId: ChatScopeId) {
//        chatSessions.remove(chatScopeId)
//    }
//
//    fun getCurrentGroup(sessionId: SessionId) = groupSessions[clientSessions[sessionId]?.groups?.first()]
//    fun getCurrentGame(sessionId: SessionId) = gameSessions[clientSessions[sessionId]?.games?.first()]
//
//
//    companion object {
//        // TODO: Move this some other place
//        fun createId() = UUID.randomUUID()
//    }
//}
