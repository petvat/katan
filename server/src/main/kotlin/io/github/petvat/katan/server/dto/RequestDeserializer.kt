package io.github.petvat.katan.server.dto

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import io.github.petvat.katan.server.action.ActionCode
import java.lang.reflect.Type

class RequestDeserializer : JsonDeserializer<ActionRequest> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ActionRequest {
        if (json != null && context != null) {
            val requestClass = when (json.asJsonObject.get("actionCode").asString) {
                ActionCode.GAME_CREATE.name -> NewGameRequest::class.java
                ActionCode.GAME_START.name -> TODO()
                ActionCode.ROLL_DICE.name -> RollDiceRequest::class.java
                ActionCode.MOVE_ROBBER.name -> MoveRobberRequest::class.java
                ActionCode.STEAL_CARD.name -> StealCardRequest::class.java
                ActionCode.BUILD.name -> BuildRequest::class.java
                ActionCode.INIT_TRADE.name -> InitiateTradeRequest::class.java
                ActionCode.RESPOND_TRADE.name -> RespondTradeRequest::class.java
                ActionCode.TURN_END.name -> EndTurnRequest::class.java
                else -> throw JsonParseException("Not correct format. Missing 'actionCode' element.")
            }
            return context.deserialize(json, requestClass)
        } else {
            throw JsonParseException("Not correct format.")
        }
    }
}
