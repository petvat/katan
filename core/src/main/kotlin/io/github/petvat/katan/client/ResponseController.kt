package io.github.petvat.katan.client

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.shared.protocol.MessageType
import io.github.petvat.katan.shared.protocol.dto.Response
import io.github.petvat.katan.shared.protocol.dto.RestrictedGroupView
import io.github.petvat.katan.shared.protocol.json.KatanJson
import io.github.petvat.katan.view.KatanView
import kotlin.math.log


class ResponseController(val model: KatanModel) {
    val views = mutableListOf<KatanView>()
    private val logger = KotlinLogging.logger { }


    fun process(json: String) {
        val response = KatanJson.jsonToMessage(json)

        val function: (KatanView) -> Unit

        response.payload.description?.let { logger.info { it } }

        if (response.payload.success!!) {

            function = when (response.header.messageType) {

                MessageType.ACK -> {

                    logger.info { "Processing ACK message." }

                    model.sessonId = (response.payload.data as Response.ConnectionAccept).sessionId
                    { } // Hack, nothing
                }

                MessageType.CHAT -> {
                    val delta = response.payload.data as Response.Chat

                    model.group!!.chatLog[delta.senderId] = delta.message
                    { view -> view.showNewChatMessage() } // TODO: Make KatanModel immutable?
                }

                MessageType.ACTION -> {
                    TODO()
                }

                MessageType.JOIN -> {
                    val delta = response.payload.data as Response.Join

                    if (delta.joinedUser != null) {
                        model.group!!.clients[delta.joinedUser!!.id] = "PLACEHOLDER"
                    } else {
                        model.group = delta.groupView
                    }
                    { view -> view.showGroupView() }
                }

                MessageType.CREATE -> {
                    logger.debug { "Processing CREATE message." }
                    val delta = (response.payload.data) as Response.Create
                    model.group = RestrictedGroupView(
                        delta.groupId,
                        mutableMapOf(model.sessonId!! to "PLACEHOLDER"),
                        delta.level,
                        chatLog = mutableMapOf(),
                        delta.settings
                    ); // HACK
                    { view ->
                        logger.debug { "Reached view show." }
                        view.showGroupView()
                    }
                }

                MessageType.INIT -> {
                    val delta = (response.payload.data) as Response.Init
                    model.gameState =
                        delta.publicGameState
                    { view -> view.showGameView() }
                }

                MessageType.LOGIN -> {
                    val delta = (response.payload.data as Response.Login)
                    model.userInfo = delta.userInfo
                    model.accessToken = delta.token
                    { it.showLoggedInView() }
                }

                MessageType.GET_GROUPS -> {
                    val delta = (response.payload.data) as Response.Groups
                    { it.updateGroupsView(delta.groups.map { g -> g.id to g.settings.gameMode.name }) }
                }

            }
        } else {
            function = { it.showErrorView(response.payload.description!!) }
            //view.showErrorView(response.payload.description!!)
        }

        views.forEach {
            function(it)
        }

    }
}

