package io.github.petvat.katan.client

import io.github.petvat.katan.server.group.GroupId
import io.github.petvat.katan.shared.protocol.PermissionLevel
import io.github.petvat.katan.shared.protocol.SessionId
import io.github.petvat.katan.shared.UserId

//
//sealed interface ClientState {
//    val sessionId: SessionId
//    val level: PermissionLevel
//    fun getAllowedMessageTypes(): Set<MessageType>
//}
//
//data class GuestState(
//    override val sessionId: SessionId,
//) : ClientState {
//    override val level = PermissionLevel.GUEST
//    override fun getAllowedMessageTypes() = setOf(
//        MessageType.LOGIN,
//        MessageType.JOIN,
//        MessageType.CREATE,
//        MessageType.GET_GROUPS
//    )
//}
//
//data class LoggedInState(
//    override val sessionId: SessionId,
//    val userId: UserId,
//) : ClientState {
//    override val level = PermissionLevel.USER
//    override fun getAllowedMessageTypes() = setOf(
//        MessageType.JOIN,
//        MessageType.CREATE,
//        MessageType.GET_GROUPS
//    )
//}
//
//data class InGroupState(
//    val base: ClientState, // FIX:
//    val groupId: GroupId
//) : ClientState by base {
//    override fun getAllowedMessageTypes() = setOf(
//        MessageType.INIT,
//        MessageType.CHAT,
//    )
//}
//
//data class PlayingState(
//    val base: InGroupState
//) : ClientState by base {
//    override fun getAllowedMessageTypes() = setOf(
//        MessageType.ACTION,
//        MessageType.CHAT
//    )
//}
