package io.github.petvat.katan.controller

//import io.github.oshai.kotlinlogging.KotlinLogging
//import io.github.petvat.katan.event.ErrorEvent
//import io.github.petvat.katan.event.EventBus
//import io.github.petvat.katan.shared.protocol.Request
//import io.github.petvat.katan.shared.protocol.Response
//import json.KatanJson

//class ResponseProcessor(val model: KatanModel) {
//    private val logger = KotlinLogging.logger { }
//
//    /**
//     * All pending requests. Flushes on received message.
//     */
//    val pendingRequests = mutableListOf<Request>()
//
//    fun process(json: String) {
//        val response = KatanJson.toResponse(json)
//
//        response.description?.let { logger.info { it } }
//
//        // For now, don't care about monitoring pendingRequests.
//        // NOTE: !!!
//        // TODO: Refresh the request queue after a successful request.
//
//        val event = when (response) {
//            // Login
//            is Response.Registered -> loginHandler(response, model)
//            is Response.GroupCreated -> createHandler(response, model)
//
//            // Group
//            is Response.Init -> initHandler(response, model)
//            is Response.Left -> TODO()
//            is Response.LobbyUpdate -> groupPushHandler(response, model)
//            is Response.Joined -> joinHandler(response, model)
//            is Response.UserJoined -> playerJoinedHandler(response, model)
//
//            // Game
//            is Response.Build -> buildHandler(response, model)
//            is Response.Chat -> chatHandler(response, model)
//            is Response.DiceRolled -> rollDiceHandler(response, model)
//            is Response.EndTurn -> turnEndedHandler(response, model)
//            is Response.InitTrade -> TODO()
//            is Response.RobberMoved -> TODO()
//            is Response.SetupEnded -> TODO()
//            is Response.TradeResponse -> TODO()
//            is Response.VictoryClaimed -> TODO()
//
//            // Other
//            is Response.Error -> {
//                errorHandler(response, model)
//            }
//            // Hmm, this one causes issues
//            // What we could do is creating a Event -> func.
//            // Better to make explicit responses I think.
//            is Response.OK -> {
//                ErrorEvent(response.description ?: "No description")
//            }
//        }
//
//        EventBus.fire(event)
//    }
//}

