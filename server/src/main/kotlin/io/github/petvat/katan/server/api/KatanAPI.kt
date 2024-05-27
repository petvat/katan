package io.github.petvat.katan.server.api

import com.google.gson.*
import io.github.petvat.katan.server.action.ActionCode
import io.github.petvat.katan.server.action.ActionResponse
import io.github.petvat.katan.server.board.Player
import io.github.petvat.katan.server.dto.*
import io.github.petvat.katan.server.game.GameProgress
import java.util.concurrent.locks.ReentrantLock

/**
 * UPDATE: All game actions go through the KatanAPI for processing.
 * KatanAPI is responsible for translating client requests into game actions and performing them,
 * then prompt the Network handler.
 */
object KatanAPI : API {

    private val lock = ReentrantLock()
    val ongoingGames: MutableList<GameProgress> = mutableListOf()

    override fun serviceRequest(request: JsonObject): Map<Int, JsonObject> {
        val responses: Map<Int, ActionResponse> = performAction(KatanParser.toRequest(request))
        val responsesJson: MutableMap<Int, JsonObject> = mutableMapOf()
        for ((pid, ar) in responses) {
            responsesJson[pid] = KatanParser.toJson(ar)
        }
        return responsesJson
    }

    private fun performAction(actionRequest: ActionRequest): Map<Int, ActionResponse> {

        synchronized(lock) {
            var responses: Map<Int, ActionResponse> = mutableMapOf()

            try {
                if (actionRequest is NewGameRequest) {
                    responses = createNewGame(actionRequest);
                } else {
                    val game = getGameByID(actionRequest.gameID)
                    responses = when (actionRequest) {
                        is RollDiceRequest -> game.gameState.rollDice(actionRequest.playerID)
                        is InitiateTradeRequest -> game.gameState.initiateTrade(actionRequest)
                        is RespondTradeRequest -> game.gameState.respondTrade(actionRequest)
                        is BuildRequest -> game.gameState.build(actionRequest)
                        is MoveRobberRequest -> game.gameState.moveRobber(actionRequest)
                        is EndTurnRequest -> game.gameState.endTurn(actionRequest.playerID)
                        else -> throw IllegalArgumentException("Unknown action request.")
                    }
                }
            } catch (e: RuntimeException) {
                responses as MutableMap
                responses.clear() // Ensure empty
                responses[actionRequest.playerID] = ActionResponse(
                    actionRequest.actionCode,
                    false, e.message ?: ("Unknown exception" +
                        "occured."), null
                )
            }
            return responses
        }
    }

    private fun createNewGame(newGameRequest: NewGameRequest): Map<Int, ActionResponse> {
        // TODO: find free game ID
        val newGame = GameProgress(0)
        // if (newGameRequest.gameSettings != null) GameProgress(0, newGameRequest.gameSettings) else GameProgress()
        // TODO: Data base fetch or other to get player
        newGame.addPlayer(Player(0, "PLACEHOLDER", 0))
        ongoingGames.add(newGame)
        val response: MutableMap<Int, ActionResponse> = mutableMapOf()
        response[0] = ActionResponse(
            ActionCode.GAME_CREATE, true, "Game created.", GameCreatedDTO(
                0, 0
            )
        )
        return response
    }

    fun getGameByID(ID: Int): GameProgress {
        return ongoingGames.find { g -> g.ID == ID } ?: throw IllegalArgumentException("No such game exist.")
    }


    /**
     * Parse Json -> ActionRequest, ActionResponse -> Json
     */
    object KatanParser {
        private val _gson: Gson =
            GsonBuilder()
                .registerTypeAdapter(ActionRequest::class.java, RequestDeserializer())
                .registerTypeAdapter(ActionResponse::class.java, ResponseSerializer())
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create()

        fun toRequest(jsonObject: JsonObject): ActionRequest {
            return _gson.fromJson(jsonObject, ActionRequest::class.java)
        }

        fun toJson(actionResponse: ActionResponse): JsonObject {
            return JsonPrimitive(_gson.toJson(actionResponse)).asJsonObject
        }
    }

}
