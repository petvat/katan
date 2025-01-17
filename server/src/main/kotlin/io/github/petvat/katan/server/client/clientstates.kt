package io.github.petvat.katan.server.client

import io.github.petvat.katan.server.group.GroupId
import io.github.petvat.katan.shared.protocol.PermissionLevel
import io.github.petvat.katan.shared.protocol.SessionId
import io.github.petvat.katan.shared.UserId
import io.github.petvat.katan.shared.protocol.MessageType


sealed interface ClientState {
    val sessionId: SessionId
    val level: PermissionLevel
    fun getAllowedMessageTypes(): Set<MessageType>
}

data class GuestState(
    override val sessionId: SessionId,
) : ClientState {
    override val level = PermissionLevel.GUEST
    override fun getAllowedMessageTypes() = setOf(
        MessageType.LOGIN,
        MessageType.JOIN,
        MessageType.CREATE,
        MessageType.GET_GROUPS
    )
}

data class LoggedInState(
    override val sessionId: SessionId,
    val userId: UserId,
) : ClientState {
    override val level = PermissionLevel.USER
    override fun getAllowedMessageTypes() = setOf(
        MessageType.JOIN,
        MessageType.CREATE,
        MessageType.GET_GROUPS
    )
}

data class InGroupState(
    val base: ClientState, // FIX:
    val groupId: GroupId
) : ClientState by base {
    override fun getAllowedMessageTypes() = setOf(
        MessageType.INIT,
        MessageType.CHAT,
    )
}

data class PlayingState(
    val base: InGroupState
) : ClientState by base {
    override fun getAllowedMessageTypes() = setOf(
        MessageType.ACTION,
        MessageType.CHAT
    )
}


//sealed class ClientState(open val groupId: SessionId, open val level: PermissionLevel) {
//    data class Guest(
//        override val groupId: SessionId
//    ) : ClientState(groupId, PermissionLevel.USER)
//
//    data class LoggedIn(
//        override val groupId: SessionId,
//        val userId: UserId,
//    ) : ClientState(groupId, PermissionLevel.USER)
//
//
//    data class InGroup(
//        override val groupId: SessionId,
//        val userId: UserId?,
//        override val level: PermissionLevel
//    ) : ClientState(groupId, level)
//
//}


