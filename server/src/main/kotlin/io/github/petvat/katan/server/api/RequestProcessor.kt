package io.github.petvat.katan.server.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import io.github.petvat.katan.server.action.ActionResponse
import io.github.petvat.katan.server.dto.ActionRequest
import io.github.petvat.katan.server.dto.RequestDeserializer

object RequestProcessor {
    private val _gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(ActionRequest::class.java, RequestDeserializer())
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .create()

    fun process(jsonObject: JsonObject): Boolean {
        val request: ActionRequest
        try {
            request = _gson.fromJson(jsonObject, ActionRequest::class.java)
        } catch (e: JsonParseException) {
            return false
        }
        KatanAPI.performAction(request)
        return true
    }

}

object ResponseProcessor {
    private val _gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(ActionResponse::class.java, RequestDeserializer())
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .create()

    fun process(actionResponse: ActionResponse) {
        val json = _gson.toJson(actionResponse)
    }
}
