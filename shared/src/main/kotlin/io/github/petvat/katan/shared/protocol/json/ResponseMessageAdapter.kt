package io.github.petvat.katan.shared.protocol.json

//import com.google.gson.*
//import io.github.petvat.katan.shared.protocol.ActionCode
//import io.github.petvat.katan.shared.protocol.*
//import io.github.petvat.katan.shared.protocol.dto.ActionDTO
//import io.github.petvat.katan.shared.protocol.dto.ActionResponse
//import io.github.petvat.katan.shared.protocol.dto.Response
//import java.lang.reflect.Type
//
//class ResponseMessageAdapter : JsonSerializer<ResponseMessage>, JsonDeserializer<ResponseMessage> {
//
//    override fun serialize(src: ResponseMessage, p1: Type?, context: JsonSerializationContext): JsonElement {
//
//        val payloadJson = JsonObject()
//        payloadJson.addProperty("Description", src.payload.description)
//        payloadJson.addProperty("Success", src.payload.success)
//
//        payloadJson.add(
//            "Data",
//            when (src.header.messageType) {
//                MessageType.CHAT -> context.serialize(src.payload.data as Response.Chat)
//                MessageType.JOIN -> context.serialize(src.payload.data as Response.Join)
//                MessageType.CREATE -> context.serialize(src.payload.data as Response.Create)
//                MessageType.INIT -> context.serialize(src.payload.data as Response.Create)
//                MessageType.LOGIN -> context.serialize(src.payload.data as Response.Login)
//                MessageType.GET_SESSION -> context.serialize(src.payload.data as Response.Sessions)
//                MessageType.ACTION -> {
//                    when ((src.payload.data as ActionDTO).actionCode) {
//                        ActionCode.SETUP_END -> context.serialize(src.payload.data as ActionResponse.SetupEnded)
//                        ActionCode.ROLL_DICE -> context.serialize(src.payload.data as ActionResponse.RollDice)
//                        ActionCode.MOVE_ROBBER -> context.serialize(src.payload.data as ActionResponse.MoveRobber)
//                        ActionCode.STEAL_CARD -> context.serialize(src.payload.data as ActionResponse.StealCard)
//                        ActionCode.BUILD -> context.serialize(src.payload.data as ActionResponse.Build)
//                        ActionCode.INIT_TRADE -> context.serialize(src.payload.data as ActionResponse.InitiateTrade)
//                        ActionCode.RESPOND_TRADE -> context.serialize(src.payload.data as ActionResponse.RespondTrade)
//                        ActionCode.TURN_END -> context.serialize(src.payload.data as ActionResponse.EndTurn)
//                    }
//
//                }
//
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
//    override fun deserialize(src: JsonElement, p1: Type, context: JsonDeserializationContext): ResponseMessage {
//
//        val requestJson = src.asJsonObject
//        val header = context.deserialize(requestJson.get("Header"), Header::class.java) as Header
//        val messageType = header.messageType
//
//        val payloadJson = requestJson.get("Payload").asJsonObject
//
//        val desc = payloadJson.get("Description").toString()
//        val data = payloadJson.get("Data").asJsonObject
//        val success = payloadJson.get("Success").asBoolean
//
//        val payload = Payload(
//            success = success,
//            description = desc,
//            data = when (messageType) {
//                MessageType.CHAT -> context.deserialize(data, Response.Chat::class.java) as Response.Chat
//                MessageType.JOIN -> context.deserialize(data, Response.Join::class.java) as Response.Join
//                MessageType.CREATE -> context.deserialize(data, Response.Create::class.java) as Response.Create
//                MessageType.INIT -> null
//                MessageType.LOGIN -> context.deserialize(data, Response.Login::class.java) as Response.Login
//                MessageType.GET_SESSION -> null
//                MessageType.ACTION -> {
//                    val code = ActionCode.valueOf(data.get("ActionCode").toString())
//                    when (code) {
//                        ActionCode.SETUP_END -> null
//                        ActionCode.ROLL_DICE -> null
//                        ActionCode.MOVE_ROBBER -> context.deserialize(
//                            data,
//                            ActionResponse.MoveRobber::class.java
//                        ) as ActionResponse.MoveRobber
//
//                        ActionCode.STEAL_CARD -> context.deserialize(
//                            data,
//                            ActionResponse.StealCard::class.java
//                        ) as ActionResponse.StealCard
//
//                        ActionCode.BUILD -> context.deserialize(
//                            data,
//                            ActionResponse.Build::class.java
//                        ) as ActionResponse.Build
//
//                        ActionCode.INIT_TRADE -> context.deserialize(
//                            data,
//                            ActionResponse.InitiateTrade::class.java
//                        ) as ActionResponse.InitiateTrade
//
//                        ActionCode.RESPOND_TRADE -> context.deserialize(
//                            data,
//                            ActionResponse.RespondTrade::class.java
//                        ) as ActionResponse.RespondTrade
//
//                        ActionCode.TURN_END -> null
//                    }
//                }
//            }
//        )
//
//        return ResponseMessage(
//            header, payload
//        )
//
//    }
//
//}
//
//
