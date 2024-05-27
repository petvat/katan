package io.github.petvat.katan.server.api

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import io.github.petvat.katan.server.ClientHandler
import io.github.petvat.katan.server.KatanServer
import io.github.petvat.katan.server.Session
import io.github.petvat.katan.server.action.ActionCode
import io.github.petvat.katan.server.dto.NewGameRequest
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.Scanner


/**
 * Responible for servicing a request.
 */
interface API {
    fun serviceRequest(request: JsonObject): Map<Int, JsonObject>
}

object RequestProcessor {
    private val _APIs: Collection<API> = listOf(KatanAPI) // TODO: ADD chat API

    /**
     * Attempts to service a request and forward it to clients of session.
     *
     * @param session the
     */
    fun handleRequest(sender: ClientHandler, request: JsonObject, session: Session): Boolean {

        try {
            // Check valid sender
            if (sender.playerID != request.get("playerID").asString.toInt() || !session.clients.contains(sender)) {
                println("Sender is not allowed to send request in this context.")
                return false
            }
            // Find matching API
            if (request.has("requestType")) {
                val api: API = when (request.get("requestType").asString) {
                    "action" -> KatanAPI
                    "chat" -> TODO()
                    else -> throw JsonSyntaxException("Unsupported request type. ")
                }
                // service request
                val responses: Map<Int, JsonObject> = api.serviceRequest(request)
                // Send to clients
                responses.forEach { (pid, resp) ->
                    session.getClientBy(pid).sendMessage(resp.asString)
                }
                return true
            }
        } catch (e: JsonSyntaxException) {
            println(e.message)
        }
        return false
    }
}
}

fun main() {
    val scanner = Scanner(System.`in`)
    val socket = Socket("localhost", 1234)


}

// object KatanResponseProcessor {
//     private val _gson: Gson =
//         GsonBuilder()
//             .registerTypeAdapter(ActionResponse::class.java, RequestDeserializer())
//             .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
//             .setPrettyPrinting()
//             .create()
//
//     fun process(actionResponse: ActionResponse) {
//         val json = _gson.toJson(actionResponse)
//     }
// }
