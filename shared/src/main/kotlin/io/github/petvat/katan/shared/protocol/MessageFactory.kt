package io.github.petvat.katan.shared.protocol

import io.github.petvat.katan.shared.protocol.dto.Request
import io.github.petvat.katan.shared.protocol.dto.Response


/**
 * Factory to build requests and responses.
 */
object MessageFactory {


    fun <R : Request> create(
        groupId: String? = null,
        messageType: MessageType,
        data: R? = null,
        description: String = ""
    ): Message<Request> {
        // Validate groupId for certain message types
        when (messageType) {
            MessageType.CHAT, MessageType.ACTION, MessageType.INIT, MessageType.JOIN -> {
                requireNotNull(groupId) { "groupId cannot be null for messageType $messageType" }
            }

            else -> {
                require(groupId == null) { "groupId must be null for messageType $messageType" }
            }
        }
        val header = Header(groupId = groupId, messageType = messageType)
        return Message<Request>(
            header,
            Payload(description = description, data = data)
        )

    }

    /**
     * TODO: Refact no messageType
     */
    fun <R : Response> create(
        groupId: String? = null,
        messageType: MessageType,
        data: R? = null,
        description: String = "",
        success: Boolean,
    ): Message<R> {
        // Validate groupId for certain message types
        when (messageType) {
            MessageType.CHAT, MessageType.ACTION, MessageType.INIT, MessageType.JOIN -> {
                requireNotNull(groupId) { "groupId cannot be null for messageType $messageType" }
            }

            else -> {
                require(groupId == null) { "groupId must be null for messageType $messageType" }
            }
        }
        val header = Header(groupId = groupId, messageType = messageType)
        return Message(
            header,
            Payload(description = description, success = success, data = data)
        )

    }

    /**
     * General function to create Request message.
     */
    //fun <R : Request> createRequest(
//        groupId: String?,
//        messageType: MessageType,
//        data: R?,
//        description: String = ""
//    ): RequestMessage {
//
//        // Validate groupId for certain message types
//        when (messageType) {
//            MessageType.CHAT, MessageType.ACTION, MessageType.INIT, MessageType.JOIN -> {
//                requireNotNull(groupId) { "groupId cannot be null for messageType $messageType" }
//            }
//
//            else -> {
//                require(groupId == null) { "groupId must be null for messageType $messageType" }
//            }
//        }
//        val header = Header(groupId = groupId, messageType = messageType)
//        return RequestMessage(header, Payload(description = description, data = data as R))
//    }
//
//    /**
//     * General function to create Response message.
//     */
//    fun <R : Response> createResponse(
//        groupId: String?,
//        messageType: MessageType,
//        data: R?,
//        success: Boolean,
//        description: String = ""
//    ): ResponseMessage {
//        val header = Header(groupId = groupId, messageType = messageType)
//        return ResponseMessage(header, Payload(success = success, description = description, data = data as R))
//    }
//
//    /**
//     * Creates a response payload of arbituray type [Response].
//     */
//    fun <R : Response> createResponsePayload(
//        success: Boolean,
//        description: String,
//        data: R?
//    ): Payload<R> {
//
//        return Payload(success, description, data as R)
//    }
//
//    fun <R : Request> createRequestPayload(
//        description: String? = "No description.",
//        data: R?
//    ): Payload<R> {
//        return Payload(description = description, data = data as R)
//    }

}
