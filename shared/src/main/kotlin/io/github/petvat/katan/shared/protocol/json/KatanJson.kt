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
}
