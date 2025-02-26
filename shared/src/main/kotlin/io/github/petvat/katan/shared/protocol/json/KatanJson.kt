package json

import io.github.petvat.katan.shared.protocol.Request
import io.github.petvat.katan.shared.protocol.Response
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass


/**
 * Json parser for [Message] objects.
 */
object KatanJson {

//    fun messageToJson(message: Message<PayloadDTO>): String {
//        return json.encodeToString(Message.serializer(PayloadDTO.serializer()), message)
//    }
//
//    fun jsonToRequest(json: String): Message<Request> {
//        return this.json.decodeFromString(Message.serializer(Request.serializer()), json)
//    }
//
//    fun jsonToResponse(json: String): Message<Response> {
//        return this.json.decodeFromString(Message.serializer(Response.serializer()), json)
//    }
//
//
//    fun jsonToMessage(json: String): Message<PayloadDTO> {
//        return this.json.decodeFromString(Message.serializer(PayloadDTO.serializer()), json)
//    }


    fun <T : Request> toJson(request: T): String {
        return json.encodeToString(Request.serializer(), request)
    }

    fun <T : Response> toJson(response: T): String {
        return json.encodeToString(Response.serializer(), response)
    }

    fun toRequest(json: String): Request {
        return this.json.decodeFromString(Request.serializer(), json)
    }

    fun toResponse(json: String): Response {
        return this.json.decodeFromString(Response.serializer(), json)
    }

    private val module = SerializersModule {
        polymorphic(Request::class) {
            subclass(Request.GuestRegister::class, Request.GuestRegister.serializer())

            subclass(Request.Join::class, Request.Join.serializer())
            subclass(Request.Create::class, Request.Create.serializer())
            subclass(Request.Chat::class, Request.Chat.serializer())
            subclass(Request.Init::class, Request.Init.serializer())
            subclass(Request.Leave::class, Request.Leave.serializer())

            // Action Requests
            subclass(Request.RollDice::class, Request.RollDice.serializer())
            subclass(Request.Build::class, Request.Build.serializer())
        }

        polymorphic(Response::class) {
            subclass(Response.Registered::class, Response.Registered.serializer())

            subclass(Response.OK::class, Response.OK.serializer())
            subclass(Response.Error::class, Response.Error.serializer())

            subclass(Response.Init::class, Response.Init.serializer())
            subclass(Response.GroupCreated::class, Response.GroupCreated.serializer())
            subclass(Response.Joined::class, Response.Joined.serializer())
            subclass(Response.LobbyUpdate::class, Response.LobbyUpdate.serializer())
            subclass(Response.DiceRolled::class, Response.DiceRolled.serializer())
            subclass(Response.UserJoined::class, Response.UserJoined.serializer())
        }
    }

    val json = Json {
        serializersModule = module
        classDiscriminator = "type"  // "type" is the field used in the JSON to distinguish subclasses
    }


//
//    private val module = SerializersModule {
//        polymorphic(PayloadDTO::class) {
//            // Requests
//            subclass(Request.Join::class, Request.Join.serializer())
//            subclass(Request.Create::class, Request.Create.serializer())
//            subclass(Request.Login::class, Request.Login.serializer())
//            subclass(Request.Chat::class, Request.Chat.serializer())
//            subclass(Request.Groups::class, Request.Groups.serializer())
//            subclass(Request.Empty::class, Request.Empty.serializer())
//            subclass(Request.Init::class, Request.Init.serializer())
//            subclass(Request.Leave::class, Request.Leave.serializer())
//
//            // Action Requests
//            subclass(ActionRequest.Build::class, ActionRequest.Build.serializer())
//            subclass(ActionRequest.InitiateTrade::class, ActionRequest.InitiateTrade.serializer())
//            subclass(ActionRequest.RespondTrade::class, ActionRequest.RespondTrade.serializer())
//            subclass(ActionRequest.StealCard::class, ActionRequest.StealCard.serializer())
//            subclass(ActionRequest.MoveRobber::class, ActionRequest.MoveRobber.serializer())
//            subclass(ActionRequest.EndTurn::class, ActionRequest.EndTurn.serializer())
//
//            // Responses
//            subclass(Response.Chat::class, Response.Chat.serializer())
//            subclass(Response.Create::class, Response.Create.serializer())
//            subclass(Response.Join::class, Response.Join.serializer())
//            subclass(Response.Login::class, Response.Login.serializer())
//            subclass(Response.Groups::class, Response.Groups.serializer())
//            subclass(Response.Init::class, Response.Init.serializer())
//            subclass(Response.Empty::class, Response.Empty.serializer())
//            subclass(Response.ConnectAck::class, Response.ConnectAck.serializer())
//            subclass(Response.GroupPush::class, Response.GroupPush.serializer())
//
//
//            // Action Responses
//            subclass(ActionResponse.RollDice::class, ActionResponse.RollDice.serializer())
//            subclass(ActionResponse.MoveRobber::class, ActionResponse.MoveRobber.serializer())
//            subclass(ActionResponse.StealCard::class, ActionResponse.StealCard.serializer())
//            subclass(ActionResponse.Build::class, ActionResponse.Build.serializer())
//            subclass(ActionResponse.InitiateTrade::class, ActionResponse.InitiateTrade.serializer())
//            subclass(ActionResponse.RespondTrade::class, ActionResponse.RespondTrade.serializer())
//            subclass(ActionResponse.EndTurn::class, ActionResponse.EndTurn.serializer())
//            subclass(ActionResponse.SetupEnded::class, ActionResponse.SetupEnded.serializer())
//        }
//    }
//
//    val json = Json {
//        serializersModule = module
//        classDiscriminator = "type"  // "type" is the field used in the JSON to distinguish subclasses
//    }


}
