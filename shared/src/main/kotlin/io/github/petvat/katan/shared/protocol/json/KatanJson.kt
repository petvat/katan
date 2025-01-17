package io.github.petvat.katan.shared.protocol.json

import io.github.petvat.katan.shared.protocol.Message
import io.github.petvat.katan.shared.protocol.dto.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


/**
 * Main Gson parser.
 *
 * Parse Json -> RequestMessage, ResponseMessage -> Json
 */
//object GsonParser {
//    private val _gson: Gson = GsonBuilder()
//        .registerTypeAdapter(RequestMessage::class.java, RequestMessageAdapter())
//        .registerTypeAdapter(ResponseMessage::class.java, ResponseMessageAdapter())
//        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
//        .setPrettyPrinting()
//        .create()
//
//    fun toJson(any: Any): JsonObject {
//        return _gson.toJsonTree(any).asJsonObject
//    }
//
//    fun toMessage(jsonObject: JsonObject): Message<*> {
//        return _gson.fromJson(jsonObject, Message::class.java)
//    }
//
//    fun toReqMsg(jsonObject: JsonObject): RequestMessage {
//        return _gson.fromJson(jsonObject, RequestMessage::class.java)
//    }
//
//    fun toResMsg(jsonObject: JsonObject): ResponseMessage {
//        return _gson.fromJson(jsonObject, ResponseMessage::class.java)
//    }
//}


/**
 * Json parser for [Message] objects.
 */
object KatanJson {

    fun messageToJson(message: Message<PayloadDTO>): String {
        return json.encodeToString(Message.serializer(PayloadDTO.serializer()), message)
    }

    fun jsonToRequest(json: String): Message<Request> {
        return this.json.decodeFromString(Message.serializer(Request.serializer()), json)
    }

    fun jsonToResponse(json: String): Message<Response> {
        return this.json.decodeFromString(Message.serializer(Response.serializer()), json)
    }


    fun jsonToMessage(json: String): Message<PayloadDTO> {
        return this.json.decodeFromString(Message.serializer(PayloadDTO.serializer()), json)
    }


    private val module = SerializersModule {
        polymorphic(PayloadDTO::class) {
            // Requests
            subclass(Request.Join::class, Request.Join.serializer())
            subclass(Request.Create::class, Request.Create.serializer())
            subclass(Request.Login::class, Request.Login.serializer())
            subclass(Request.Chat::class, Request.Chat.serializer())
            subclass(Request.Groups::class, Request.Groups.serializer())
            subclass(Request.Empty::class, Request.Empty.serializer())
            subclass(Request.Init::class, Request.Init.serializer())
            subclass(Request.Leave::class, Request.Leave.serializer())

            // Action Requests
            subclass(ActionRequest.Build::class, ActionRequest.Build.serializer())
            subclass(ActionRequest.InitiateTrade::class, ActionRequest.InitiateTrade.serializer())
            subclass(ActionRequest.RespondTrade::class, ActionRequest.RespondTrade.serializer())
            subclass(ActionRequest.StealCard::class, ActionRequest.StealCard.serializer())
            subclass(ActionRequest.MoveRobber::class, ActionRequest.MoveRobber.serializer())

            // Responses
            subclass(Response.Chat::class, Response.Chat.serializer())
            subclass(Response.Create::class, Response.Create.serializer())
            subclass(Response.Join::class, Response.Join.serializer())
            subclass(Response.Login::class, Response.Login.serializer())
            subclass(Response.Groups::class, Response.Groups.serializer())
            subclass(Response.Init::class, Response.Init.serializer())
            subclass(Response.Empty::class, Response.Empty.serializer())
            subclass(Response.ConnectionAccept::class, Response.ConnectionAccept.serializer())


            // Action Responses
            subclass(ActionResponse.RollDice::class, ActionResponse.RollDice.serializer())
            subclass(ActionResponse.MoveRobber::class, ActionResponse.MoveRobber.serializer())
            subclass(ActionResponse.StealCard::class, ActionResponse.StealCard.serializer())
            subclass(ActionResponse.Build::class, ActionResponse.Build.serializer())
            subclass(ActionResponse.InitiateTrade::class, ActionResponse.InitiateTrade.serializer())
            subclass(ActionResponse.RespondTrade::class, ActionResponse.RespondTrade.serializer())
            subclass(ActionResponse.EndTurn::class, ActionResponse.EndTurn.serializer())
            subclass(ActionResponse.SetupEnded::class, ActionResponse.SetupEnded.serializer())
        }
    }

    val json = Json {
        serializersModule = module
        classDiscriminator = "type"  // "type" is the field used in the JSON to distinguish subclasses
    }


}
