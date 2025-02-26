package io.github.petvat.katan.shared.protocol

import io.github.petvat.katan.shared.protocol.dto.PayloadDTO
import io.github.petvat.katan.shared.protocol.dto.Request
import kotlinx.serialization.Serializable

/**
 *
 * RecordId: current -> proc = false -> RecordId: current
 * RecordId: current -> proc = true -> RecordId: current + 1
 *
 *
 * New incoming, not req/res -> current + 1
 * New incoming, not req/res -> <=current -> Discard
 */
//@Serializable
//data class Header(
//    val messageId: Int? = null,
//    val groupId: String? = null,
//    val messageType: MessageType,
//    val version: String = PROTOCOL_VERSION,
//    val timestamp: Long = System.currentTimeMillis(),
//    val token: String? = null
//)
//
//
//@Serializable
//data class Payload<out R : PayloadDTO>( // TODO: Review
//    val success: Boolean? = null,
//    val description: String? = null,
//    val data: R? = null // Todo: Rename to delta
//)
//
//
//@Serializable
//data class Message<out R : PayloadDTO>(
//    val header: Header,
//    val payload: Payload<R>
//)
//
//data class RequestMessage(
//    val header: Header,
//    val payload: Payload<Request>
//)

//abstract class RequestMessage2<out Request>(
//    open val messageId: Int? = null,
//    open val groupId: String? = null,
//    open val messageType: MessageType,
//    open val version: String = PROTOCOL_VERSION,
//    open val timestamp: Long = System.currentTimeMillis(),
//    open val token: String? = null,
//    open val data: Request?
//)
//
//open class ResponseMessage2<out Response>(
//    open val messageId: Int? = null,
//    open val groupId: String? = null,
//    open val messageType: MessageType,
//    open val version: String = PROTOCOL_VERSION,
//    open val timestamp: Long = System.currentTimeMillis(),
//    open val payload: Payload<Response>
//)
//
//data class ChatRequest(
//    override val data: Request.Chat,
//    override val groupId: String,
//    override val messageId: Int?,
//    override val token: String,
//
//    ) :
//    RequestMessage2<Request.Chat>(
//        messageId = messageId,
//        token = token,
//        groupId = groupId,
//        messageType = MessageType.CHAT,
//        data = data
//    )
//
//data class InitRequest(
//    override val messageId: Int?,
//    override val token: String,
//) : RequestMessage2<Nothing>(
//    messageId = messageId,
//    token = token,
//    messageType = MessageType.CHAT,
//    data = null
//) {
//    override fun accept(visitor: RequestVisitor) {
//        visitor.processInit()
//    }
//}
//
//
//data class ChatResponse(
//    override val payload: Payload<Response.Chat>,
//    override val messageId: Int?,
//
//    ) : ResponseMessage2<Response.Chat>(
//    messageId = messageId,
//    messageType = MessageType.CHAT,
//    payload = payload
//)
//
//
//data class ResponseMessage(
//    val header: Header,
//    val payload: Payload<Response>
//)


////abstract class Message<H, T>(
////    open val header: H,
////    open val payload: T
////)
//
//// NOTE: Either Payload only or Response/RequestPayload only
//sealed class Payload<out D>(
//    open val success: Boolean? = null,
//    open val description: String? = null,
//    open val data: D? = null
//)
//
//data class Header(
//    val groupId: String? = null,
//    val messageType: MessageType,
//    val version: String = PROTOCOL_VERSION,
//    val timestamp: Long = System.currentTimeMillis(),
//    val token: String? = null
//)
//
//
///**
// *
// * @property senderId The id of the sender. If null, the sender is not identified and the client is only exposed to IDENTIFY.
// */
//data class RequestHeader(
//    val senderId: Int? = null,
//    val groupId: String? = null,
//    val messageType: MessageType,
//    val version: String = PROTOCOL_VERSION,
//    val timestamp: Long = System.currentTimeMillis(),
//    val token: String? = null
//)
//
///**
// * Response header sent back by server.
// *
// * @property recipientId Intended recipient
// * @property groupId The session context
// */
//data class ResponseHeader(
//    val recipientId: Int,
//    val groupId: String?,
//    val messageType: MessageType,
//    val version: String = PROTOCOL_VERSION,
//    val timestamp: Long = System.currentTimeMillis(),
//)
//
//
///**
// * Payload for responses, meaning that success property is required.
// *
// * @property success If request was successful
// * @property description Text message describing action
// * @property data Additional data
// */
//data class ResponsePayLoad<out R : PayloadDTO>(
//    val success: Boolean,
//    val description: String,
//    val data: R?
//)
//
///**
// * Payload for requests, meaning that success property is not required. Description is optional.
// *
// * @property description This could be helpful for debugging
// * @property data Additional data
// */
//data class RequestPayLoad<out R : PayloadDTO>(
//    val description: String = "No description.",
//    val data: R?,
//)
//
///**
// * Base response message.
// */
//open class ResponseMessage<out R : PayloadDTO>(
//    val header: ResponseHeader,
//    val payload: Payload<R>
//)
//
///**
// * @property header Meta-data about the request message
// * @property payload
// */
//open class RequestMessage<R : PayloadDTO>(
//    val header: RequestHeader,
//    val payload: RequestPayLoad<R>
//) {
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as RequestMessage<*>
//
//        if (header != other.header) return false
//        if (payload != other.payload) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = header.hashCode()
//        result = 31 * result + payload.hashCode()
//        return result
//    }
//
//}
//
//fun main() {
//    val messages = RequestMessage(
//        RequestHeader(1, "", MessageType.CHAT),
//        RequestPayLoad("", ChatRequestDTO("", mutableSetOf("")))
//    )
//
//    val msg = MessageFactory.createRequest(
//        1, "",
//        MessageType.CHAT, ChatRequestDTO(
//            "", mutableSetOf("")
//        )
//    )
//
//    println(msg)
//    val json = GsonParser.toJson(msg)
//    println(json)
//
//    val requestBack = GsonParser.toReqMsg(json)
//    println(requestBack)
//
//    println(msg == requestBack)
//
//
//    val rsp = ResponseMessage2(
//        recipientId = 1,
//        groupId = "",
//        messageType = MessageType.CHAT,
//        description = "",
//        success = true,
//        payload = ChatResponseDTO(1, "Msg")
//    )
//
//}





