package io.github.petvat.katan.server.client

import io.github.petvat.katan.shared.User
import io.github.petvat.katan.shared.UserId

object UserService {

    private val _users = mutableMapOf<UserId, User>()

    /**
     * Returns user id if can be authenticated.
     *
     * @throws IllegalArgumentException If no user matches username and password
     */
    fun authenticate(username: String, password: String): UserId {
        val targetUser =
            _users.values.find { user -> user.password == password && user.username == username }
                ?: throw IllegalArgumentException("No user matches username and password.")
        targetUser.active = true
        return targetUser.id
    }

    fun auth(username: String, password: String): User? {
        val targetUser =
            _users.values.find { user -> user.password == password && user.username == username } ?: return null
        targetUser.active = true
        return targetUser
    }

    fun getUser(id: UserId): User {
        return _users[id]!!
    }

}
