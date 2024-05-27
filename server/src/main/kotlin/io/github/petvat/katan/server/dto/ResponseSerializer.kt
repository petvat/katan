package io.github.petvat.katan.server.dto

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import io.github.petvat.katan.server.action.ActionResponse
import java.lang.reflect.Type

class ResponseSerializer : JsonSerializer<ActionResponse> {
    override fun serialize(src: ActionResponse?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        TODO("Not yet implemented")
    }
}
