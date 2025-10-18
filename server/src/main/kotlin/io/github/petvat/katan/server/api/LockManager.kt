package io.github.petvat.katan.server.api

import io.github.petvat.katan.server.group.GroupId
import io.github.petvat.katan.shared.model.SessionId
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class LockManager {
    private val groupLocks = ConcurrentHashMap<GroupId, Mutex>()
    private val sessionLocks = ConcurrentHashMap<SessionId, Mutex>()

    fun cleanUp(groupId: GroupId) = groupLocks.remove(groupId)

    fun cleanUp(sessionId: SessionId) = sessionLocks.remove(sessionId)


    fun add(gid: GroupId) {
        groupLocks[gid] = Mutex()
    }

    fun add(sid: SessionId) {
        sessionLocks[sid] = Mutex()
    }

    /**
     * HACK: Need to change this!
     *  In requestHandler, we first get group (group = gm.get) before locking, this means
     *  that group could be removed once we use it (we would probably be dealing with a "ghost group")
     *  Therefore we MUST lock before accessing group.
     *  Compute if absent feels weird because, we could create a mutex for a group that does not exist anymore.
     *  So, callInMutex should not create a mutex -> for now make it throw exception. All mutexes are made explicilty.
     *  We need to wrap mutex around GroupRequests.
     *  We create a GroupRequestHandler(client, group, request), Registry delegates to this.
     *  OR ! We determine the mutex before-hand in RequestDispatcher.
     *  This way we just make RequestHandlers atomic and they should always work given that RequestDispatcher calls them correctly.
     *  But RequestHandler also need to remove locks, so yeah, RequestHandler handles locking.
     *  It should evt. delegate to GroupRequestHandler
     *
     *
     */
    suspend fun <R> callInMutex(gid: GroupId, function: suspend () -> R): R {
        // return wrap(groupLocks[gid], function) }
        return wrap(groupLocks.computeIfAbsent(gid) { Mutex() }, function)
    }

    suspend fun <R> callInMutex(sid: SessionId, function: suspend () -> R): R {
        return wrap(sessionLocks.computeIfAbsent(sid) { Mutex() }, function)
    }

    private suspend fun <R> wrap(mutex: Mutex, function: suspend () -> R) =
        // mutex?.let { it.withLock { function() } } ?: throw IllegalArgumentException("Entry does not exist")
        mutex.withLock { function() }

}

