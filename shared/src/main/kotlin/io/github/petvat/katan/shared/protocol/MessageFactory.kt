package io.github.petvat.katan.shared.protocol

import io.github.petvat.katan.shared.protocol.dto.Request


/**
 * Factory to build requests and responses.
 *
 * TODO: Annoying to use.
 */
//object MessageFactory {
//
//
//    fun <R : Request> create(
//        messageId: Int? = null,
//        groupId: String? = null,
//        messageType: MessageType,
//        data: R? = null,
//        description: String = ""
//    ): Message<Request> {
//        // Validate groupId for certain message types
//        when (messageType) {
//            MessageType.CHAT, MessageType.ACTION, MessageType.INIT -> {
//                requireNotNull(groupId) { "groupId cannot be null for messageType $messageType" }
//            }
//
//            else -> {
//                require(groupId == null) { "groupId must be null for messageType $messageType" }
//            }
//        }
//        val header = Header(messageId = messageId, groupId = groupId, messageType = messageType)
//        return Message<Request>(
//            header,
//            Payload(description = description, data = data)
//        )
//
//    }
//
//    /**
//     * TODO: Refact no messageType + Exceptions
//     */
//    fun <R : Response> create(
//        groupId: String? = null,
//        messageType: MessageType,
//        data: R? = null,
//        description: String = "",
//        success: Boolean,
//    ): Message<R> {
//        // Validate groupId for certain message types
//        when (messageType) {
//            MessageType.CHAT, MessageType.ACTION, MessageType.INIT -> {
//                requireNotNull(groupId) { "groupId cannot be null for messageType $messageType" }
//            }
//
//            else -> {
//                require(groupId == null) { "groupId must be null for messageType $messageType" }
//            }
//        }
//        val header = Header(groupId = groupId, messageType = messageType)
//        val payload = Payload(description = description, success = success, data = data)
//
//        return Message(
//            header,
//            payload
//        )
//
//    }
//}
