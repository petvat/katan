package io.github.petvat.katan.server.dto

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import io.github.petvat.katan.server.action.ActionCode
import io.github.petvat.katan.server.action.ActionResponse
import java.lang.reflect.Type

class ResponseSerializer : JsonSerializer<ActionResponse> {
    override fun serialize(src: ActionResponse?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if (src == null || context == null) {
            throw JsonParseException("Err.")
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("actionCode", src.actionCode.name)
        jsonObject.addProperty("success", src.actionCode.name)
        if (src.data != null) {
            jsonObject.add("data", context.serialize(src.data))
        }
        return jsonObject
    }

    //// NOTE: Treng ikkje dette fordi likt for alle.
    //class DTOSerializer: JsonSerializer<ActionDTO> {
    //    override fun serialize(src: ActionDTO?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
    //       if (src == null || context == null) {
    //           throw  JsonParseException("Err.")
    //       }
    //        when (src) {
    //            ActionCode
    //        }
    //    }

    //}

}
