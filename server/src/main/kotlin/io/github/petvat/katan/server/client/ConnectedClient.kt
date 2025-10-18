package io.github.petvat.katan.server.client

import io.github.petvat.katan.shared.User
import io.github.petvat.katan.shared.model.PermissionLevel
import io.github.petvat.katan.shared.model.SessionId
import io.github.petvat.katan.shared.protocol.*

/**
 * A connected client associated with a socket through a [SessionId].
 *
 * Exists in-memory.
 *
 * @property auth The authentication state of this client
 * @property activity The current activity state of this client
 */
//data class ConnectedClient(
//    val sessionId: SessionId,
//    val auth: AuthState,
//    val activity: ActivityState
//) {
//    fun getAllowedRequests(): Set<MTypes> {
//        return buildSet {
//            // Combine permissions from auth + activity
//            addAll(permissions())
//        }
//    }
//
//    private fun permissions() =
//        if (auth == UnAuth) {
//            setOf(MTypes.REQ_REG, MTypes.REQ_REG_GST)
//        } else {
//            activityPermissions()
//        }
//
//    private fun activityPermissions() = when (activity) {
//        Idle -> setOf(
//            MTypes.REQ_JOIN,
//            MTypes.REQ_CREATE
//        )
//
//        is InGroup -> setOf(
//            MTypes.REQ_CHAT,
//            MTypes.REQ_LEAVE,
//            MTypes.REQ_INIT
//        )
//
//        is Playing -> setOf(
//            MTypes.REQ_CHAT,
//            MTypes.REQ_GAMEACTION
//        )
//    }
//}
//
//
//sealed interface AuthState {
//    val level: PermissionLevel
//    val name: String?
//}
//
///**
// * Represents an authenticated client. Has to register as guest or user.
// */
//data object UnAuth : AuthState {
//    override val level = PermissionLevel.UNAUTH
//    override val name = null
//}
//
///**
// * Represents a guest authenticated client.
// */
//data class GuestAuth(override val name: String) : AuthState {
//    override val level = PermissionLevel.GUEST
//}
//
//
///**
// * Represents a user authenticated client.
// */
//data class UserAuth(
//    val user: User
//) : AuthState {
//    override val level = PermissionLevel.USER
//    override val name = user.username
//}
//
//sealed interface ActivityState {
//    data object Idle : ActivityState
//    data object InGroup : ActivityState
//
//    data object Playing : ActivityState
//}
//
//
