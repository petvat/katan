package io.github.petvat.katan.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.event.ErrorEvent
import io.github.petvat.katan.event.InEventBus
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.shared.protocol.MessageType
import io.github.petvat.katan.shared.protocol.dto.ActionResponse
import io.github.petvat.katan.shared.protocol.dto.Response
import io.github.petvat.katan.shared.protocol.json.KatanJson


class ResponseController(val model: KatanModel) {
    // val views = mutableListOf<KatanView>()
    private val logger = KotlinLogging.logger { }

    fun process(json: String) {
        val response = KatanJson.jsonToMessage(json)

        response.payload.description?.let { logger.info { it } }
        if (response.payload.success!!) {
            val data = response.payload.data
            when (response.header.messageType) {
                MessageType.CHAT -> ChatMessageHandler.handle(data as Response.Chat, model)
                MessageType.ACTION -> ActionMessageHandler.handle(data as ActionResponse, model)
                MessageType.JOIN -> JoinMessageHandler.handle(data as Response.Join, model)
                MessageType.CREATE -> CreateMessageHandler.handle(data as Response.Create, model)
                MessageType.INIT -> InitMessageHandler.handle(data as Response.Init, model)
                MessageType.LOGIN -> LoginMessageHandler.handle(data as Response.Login, model)
                MessageType.GET_GROUPS -> GetGroupsMessageHandler.handle(data as Response.Groups, model)
                MessageType.ACK -> AckMessageHandler.handle(data as Response.ConnectionAccept, model)
            }
        } else {
            // Should only be catched by a screen, as this will create a new window
            InEventBus.fire(ErrorEvent(response.payload.description ?: "No description"))
        }
    }
}

