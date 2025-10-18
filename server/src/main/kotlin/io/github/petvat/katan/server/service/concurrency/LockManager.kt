package io.github.petvat.katan.server.service.concurrency


import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class LockManager {
    private val locks = ConcurrentHashMap<Lockable, Mutex>()

    suspend fun <T> callInMutex(lockable: Lockable, block: suspend () -> T): T {
        val mutex = locks.computeIfAbsent(lockable) { Mutex() }
        return mutex.withLock {
            block()
        }
    }

    fun destroyLock(session: Lockable) {
        locks.remove(session)
    }
}
