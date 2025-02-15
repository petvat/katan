package io.github.petvat.katan.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.event.ErrorEvent
import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.EventBus
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.shared.protocol.MessageType
import io.github.petvat.katan.shared.protocol.dto.ActionResponse
import io.github.petvat.katan.shared.protocol.dto.Response
import io.github.petvat.katan.shared.protocol.json.KatanJson


class ResponseParser(val model: KatanModel) {
    private val logger = KotlinLogging.logger { }

    fun process(json: String) {
        val response = KatanJson.jsonToMessage(json)

        response.payload.description?.let { logger.info { it } }

        val event: Event
        if (response.payload.success!!) {
            val data = response.payload.data

            event = when (response.header.messageType) {
                MessageType.CHAT -> chatHandler(data as Response.Chat, model)
                MessageType.ACTION -> actionHandler(data as ActionResponse, model)
                MessageType.JOIN -> joinHandler(data as Response.Join, model)
                MessageType.CREATE -> createHandler(data as Response.Create, model)
                MessageType.INIT -> initHandler(data as Response.Init, model)
                MessageType.LOGIN -> loginHandler(data as Response.Login, model)
                MessageType.GET_GROUPS -> getGroupsHandler(data as Response.Groups, model)
                MessageType.ACK -> ackHandler(data as Response.ConnectionAccept, model)
            }
        } else {
            // Should only be catched by a screen, as this will create a new window
            event = ErrorEvent(response.payload.description ?: "No description")
        }
        EventBus.fire(event)
    }
}

