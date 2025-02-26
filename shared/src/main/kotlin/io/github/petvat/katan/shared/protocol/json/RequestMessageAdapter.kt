package io.github.petvat.katan.shared.protocol.json
//
//import com.google.gson.*
//import io.github.petvat.katan.shared.protocol.ActionCode
//import io.github.petvat.katan.shared.protocol.Header
//import io.github.petvat.katan.shared.protocol.RequestMessage
//import io.github.petvat.katan.shared.protocol.dto.ActionDTO
//import io.github.petvat.katan.shared.protocol.dto.ActionRequest
//import io.github.petvat.katan.shared.protocol.dto.Request
//import io.github.petvat.katan.shared.protocol.MessageType
//import io.github.petvat.katan.shared.protocol.Payload
//import java.lang.reflect.Type
//
//
//class RequestMessageAdapter : JsonSerializer<RequestMessage>, JsonDeserializer<RequestMessage> {
//
//    override fun serialize(src: RequestMessage, p1: Type?, context: JsonSerializationContext): JsonElement {
//
//        val payloadJson = JsonObject()
//        if (src.payload.description != null) payloadJson.addProperty("Description", src.payload.description)
//
//        payloadJson.add(
//            "Data",
//            when (src.header.messageType) {
//                MessageType.CHAT -> context.serialize(src.payload.data as Request.Chat)
//                MessageType.JOIN -> context.serialize(src.payload.data as Request.Join)
//                MessageType.CREATE -> context.serialize(src.payload.data as Request.Create)
//                MessageType.INIT -> null
//                MessageType.LOGIN -> context.serialize(src.payload.data as Request.Login)
//                MessageType.GET_GROUPS -> null
//                MessageType.ACTION -> {
//                    when ((src.payload.data as ActionDTO).actionCode) {
//                        ActionCode.SETUP_END -> null
//                        ActionCode.ROLL_DICE -> null
//                        ActionCode.MOVE_ROBBER -> context.serialize(src.payload.data as ActionRequest.MoveRobber)
//                        ActionCode.STEAL_CARD -> context.serialize(src.payload.data as ActionRequest.StealCard)
//                        ActionCode.BUILD -> context.serialize(src.payload.data as ActionRequest.Build)
//                        ActionCode.INIT_TRADE -> context.serialize(src.payload.data as ActionRequest.InitiateTrade)
//                        ActionCode.RESPOND_TRADE -> context.serialize(src.payload.data as ActionRequest.RespondTrade)
//                        ActionCode.TURN_END -> null
//                        ActionCode.CLAIM_VICTORY -> null
//                        ActionCode.INIT_BUILD -> context.serialize(src.payload.data as ActionRequest.Build)
//                    }
//                }
//
//                MessageType.ACK -> TODO()
//                MessageType.GROUP_PUSH -> TODO()
//            }
//        )
//
//        return JsonObject().apply {
//            add("Header", context.serialize(src.header))
//            add("Payload", payloadJson)
//        }
//
//    }
//
//    override fun deserialize(src: JsonElement, p1: Type, context: JsonDeserializationContext): RequestMessage {
//
//        val requestJson = src.asJsonObject
//        val header = context.deserialize(requestJson.get("Header"), Header::class.java) as Header
//        val messageType = header.messageType
//
//        val payloadJson = requestJson.get("Payload").asJsonObject
//
//        val desc = if (payloadJson.get("Description") != null) payloadJson.get("Description").toString() else null
//        val data = payloadJson.get("Data").asJsonObject
//
//        val payload = Payload(
//            description = desc,
//            data = when (messageType) {
//                MessageType.CHAT -> context.deserialize(data, Request.Chat::class.java) as Request.Chat
//                MessageType.JOIN -> context.deserialize(data, Request.Join::class.java) as Request.Join
//                MessageType.CREATE -> context.deserialize(data, Request.Create::class.java) as Request.Create
//                MessageType.INIT -> null
//                MessageType.LOGIN -> context.deserialize(data, Request.Login::class.java) as Request.Login
//                MessageType.GET_GROUPS -> null
//                MessageType.ACTION -> {
//                    val code = ActionCode.valueOf(data.get("ActionCode").toString())
//                    when (code) {
//                        ActionCode.SETUP_END -> null
//                        ActionCode.ROLL_DICE -> null
//                        ActionCode.MOVE_ROBBER -> context.deserialize(
//                            data,
//                            ActionRequest.MoveRobber::class.java
//                        ) as ActionRequest.MoveRobber
//
//                        ActionCode.STEAL_CARD -> context.deserialize(
//                            data,
//                            ActionRequest.StealCard::class.java
//                        ) as ActionRequest.StealCard
//
//                        ActionCode.BUILD -> context.deserialize(
//                            data,
//                            ActionRequest.Build::class.java
//                        ) as ActionRequest.Build
//
//                        ActionCode.INIT_TRADE -> context.deserialize(
//                            data,
//                            ActionRequest.InitiateTrade::class.java
//                        ) as ActionRequest.InitiateTrade
//
//                        ActionCode.RESPOND_TRADE -> context.deserialize(
//                            data,
//                            ActionRequest.RespondTrade::class.java
//                        ) as ActionRequest.RespondTrade
//
//                        ActionCode.TURN_END -> null
//                        ActionCode.CLAIM_VICTORY -> TODO()
//                        ActionCode.INIT_BUILD -> context.deserialize(
//                            data,
//                            ActionRequest.Build::class.java
//                        )
//                    }
//                }
//
//                MessageType.ACK -> TODO()
//                else -> TODO()
//            }
//        )
//
//        return RequestMessage(
//            header, payload
//        )
//
//    }
//
//}
//
//fun main() {
//    val req = RequestMessage(
//        Header(messageType = MessageType.CHAT), Payload(
//            data = Request.Chat(
//                "Hello", emptySet()
//            )
//        )
//    )
//
//    val gson = GsonBuilder()
//        .registerTypeAdapter(RequestMessage::class.java, RequestMessageAdapter())
//        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
//        .setPrettyPrinting()
//        .create()
//
//    val json = gson.toJsonTree(req).asJsonObject
//
//    println(json)
//
//    val backToReq = gson.fromJson(json, RequestMessage::class.java)
//
//    println(json)
//
//}
