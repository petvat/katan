package io.github.petvat.katan.server.client

import io.github.petvat.katan.server.group.GroupId
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.model.PermissionLevel
import io.github.petvat.katan.shared.model.SessionId
import io.github.petvat.katan.shared.protocol.*

data class ConnectedClient(
    val sessionId: SessionId,
    val auth: AuthState,
    val activity: ActivityState
) {
    fun getAllowedRequests(): Set<MTypes> {
        return buildSet {
            // Combine permissions from auth + activity
            addAll(permissions())
        }
    }

    private fun permissions() =
        if (auth == UnAuth) {
            setOf(MTypes.REQ_REG, MTypes.REQ_REG_GST)
        } else {
            activityPermissions()
        }

    private fun activityPermissions() = when (activity) {
        Idle -> setOf(
            MTypes.REQ_JOIN,
            MTypes.REQ_CREATE
        )

        is InGroup -> setOf(
            MTypes.REQ_CHAT,
            MTypes.REQ_LEAVE,
            MTypes.REQ_INIT
        )

        is Playing -> setOf(
            MTypes.REQ_CHAT,
            MTypes.REQ_GAMEACTION
        )
    }


}


sealed interface AuthState {
    val userId: UserId?
    val level: PermissionLevel
}

data object UnAuth : AuthState {
    override val level = PermissionLevel.UNAUTH
    override val userId = null
}

data class GuestAuth(val tempName: String) : AuthState {
    override val level = PermissionLevel.GUEST
    override val userId = null
}

data class LoggedInAuth(
    override val userId: UserId
) : AuthState {
    override val level = PermissionLevel.USER
}

sealed interface ActivityState {
    val groupId: GroupId?
}

data object Idle : ActivityState {
    override val groupId: GroupId? = null
}

data class InGroup(override val groupId: GroupId) : ActivityState
data class Playing(override val groupId: GroupId) : ActivityState
