package json

import io.github.petvat.katan.shared.protocol.InMessage
import io.github.petvat.katan.shared.protocol.OutMessage
import kotlinx.serialization.json.Json


/**
 * Json (de)serialization for the [InMessage]/[OutMessage] wire protocol.
 *
 */
object KatanJson {
    private val json = Json {
        classDiscriminator = "type"
        ignoreUnknownKeys = true // lets an older server tolerate a newer client's extra fields
    }

    fun toJson(inMessage: InMessage): String = json.encodeToString(inMessage)
    fun toJson(outMessage: OutMessage): String = json.encodeToString(outMessage)

    fun toInMessage(raw: String): InMessage = json.decodeFromString(raw)
    fun toOutMessage(raw: String): OutMessage = json.decodeFromString(raw)
}


///**
// * Json parser for [Request] and [Response2] objects.
// */
//object KatanJson {
//
//    fun <T : Request> toJson(request: T): String {
//        return json.encodeToString(Request.serializer(), request)
//    }
//
//    fun <T : Response2> toJson(response: T): String {
//        return json.encodeToString(Response2.serializer(), response)
//    }
//
//    fun toRequest(json: String): Request {
//        return this.json.decodeFromString(Request.serializer(), json)
//    }
//
//    fun toResponse(json: String): Response2 {
//        return this.json.decodeFromString(Response2.serializer(), json)
//    }
//
//    private val module = SerializersModule {
//        polymorphic(Request::class) {
//            subclass(Request.GuestRegister::class, Request.GuestRegister.serializer())
//
//            subclass(Request.Join::class, Request.Join.serializer())
//            subclass(Request.Create::class, Request.Create.serializer())
//            subclass(Request.Chat::class, Request.Chat.serializer())
//            subclass(Request.Init::class, Request.Init.serializer())
//            subclass(Request.Leave::class, Request.Leave.serializer())
//
//            // Action Requests
//            subclass(Request.RollDice::class, Request.RollDice.serializer())
//            subclass(Request.Build::class, Request.Build.serializer())
//        }
//
//        polymorphic(Response2::class) {
//            subclass(Response2.Registered::class, Response2.Registered.serializer())
//
//            subclass(Response2.OK::class, Response2.OK.serializer())
//            subclass(Response2.Error::class, Response2.Error.serializer())
//            subclass(Response2.Connected::class, Response2.Connected.serializer())
//
//            subclass(Response2.Init::class, Response2.Init.serializer())
//            subclass(Response2.GroupCreated::class, Response2.GroupCreated.serializer())
//            subclass(Response2.Joined::class, Response2.Joined.serializer())
//            subclass(Response2.GroupUpdate::class, Response2.GroupUpdate.serializer())
//            subclass(Response2.DiceRolled::class, Response2.DiceRolled.serializer())
//            subclass(Response2.UserJoined::class, Response2.UserJoined.serializer())
//        }
//    }
//
//    val json = Json {
//        serializersModule = module
//        classDiscriminator = "type"  // "type" is the field used in the JSON to distinguish subclasses
//    }
//}
