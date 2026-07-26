package io.github.petvat.katan.controller

import io.github.petvat.katan.event.*
import io.github.petvat.katan.model.*
import io.github.petvat.katan.shared.protocol.OutMessage
import io.github.petvat.katan.shared.protocol.Response

class ResponseProcessor(
    private val tracker: PendingRequestTracker,
    private val state: ClientState,
    private val eventBus: EventSystem
) {
    fun update(message: OutMessage) {

        // TODO: Track requests

        when (val response = message.payload) {
            is Response.Error -> {
                eventBus.fire(ErrorEvent(reason = response.detail, code = response.code))
            }

            is Response.GroupCreated -> {
                state.group = GroupState(response.groupId, mutableMapOf(state.id to state.name), response.settings)
                response.chatId?.let {
                    state.chat = ChatState(response.chatId!!, mutableMapOf(state.id to state.name), mutableListOf())
                }
                eventBus.fire(CreateEvent)
            }

            is Response.Registered -> {
                state.id = response.clientId
                state.lobby = response.lobby
                state.resumeToken = response.resumeToken
                state.name = response.name
                eventBus.fire(LoginEvent)

            }

            is Response.DiceRolled -> {
                state.game = state.game.rolledDice(response.resources, response.othersResources, response.moveRobber)
                eventBus.fire(RolledDiceEvent(response.roll1, response.roll2, response.moveRobber))
            }

            is Response.Build -> {
                state.game = state.game.addBuilding(
                    response.builder,
                    response.buildkind,
                    response.coordinates,
                    response.victoryPoints
                )
                eventBus.fire(BuildEvent(response.builder, response.buildkind, response.coordinates))
            }

            is Response.Chat -> {
                state.chat = state.chat?.chatMessage(response.from, response.message)
                eventBus.fire(ChatEvent(response.from, response.message))
            }

            is Response.Joined -> { /* update groups, fire event */
                state.group = GroupState(
                    response.groupId,
                    response.members.toMutableMap(),
                    response.settings
                )
                eventBus.fire(JoinEvent)
            }

            else -> Unit
        }
    }
}


//
//
//class ResponseProcessor(private val bus: EventSystem, private val state: ClientState) {
//
//    private val handlers = mapOf(
//        handleWith<Response.DiceRolled> { r, _ -> diceRolled(r) },
//        handleWith<Response.Chat> { r, _ -> chat(r) },
//        handleWith<Response.Joined> { r, _ -> joined(r) },
//        handleWith<Response.UserJoined> { r, _ -> userJoined(r) },
//        handleWith<Response.Init> { r, _ -> init(r) },
//        handleWith<Response.SetupEnded> { r, _ -> setupEnded(r) },
//        handleWith<Response.Build> { r, _ -> build(r) },
//        handleWith<Response.InitBuildingPlaced> { r, _ -> initialSettlementPlaced(r) },
//    )
//
//    private inline fun <reified T : Response> handleWith(
//        crossinline handler: (T, ResponseProcessor) -> Event
//    ): Pair<KClass<out Response>, (Response) -> Event> {
//        return T::class to { r -> handler(r as T, this) }
//    }
//
//    fun update(response: Response) {
//        val handler = handlers[response::class]!!
//        val event = handler(response)
//        bus.fire(event)
//    }
//
//    private fun setupEnded(response: Response.SetupEnded): SetupEndedEvent {
//
//        return SetupEndedEvent(
//            -1, // TODO
//            response.builder,
//            response.buildkind,
//            response.coordinates
//        )
//    }
//
//    private fun initialSettlementPlaced(response: Response.InitBuildingPlaced): PlaceInitialSettlementEvent {
//
//        val building = response.buildkind
//        val coordinates = response.coordinates
//        val builder = response.builder
//        val vp = response.victoryPoints
//
//        state.gameFocus = state.gameFocus.addBuilding(builder, building, coordinates, vp)
//
//        return PlaceInitialSettlementEvent(
//            response.builder,
//            response.coordinates as ICoordinates
//        )
//    }
//
//    private fun init(response: Response.Init): InitEvent {
//        state.gameFocus = response.privateGameState.toClientModel()
//        return InitEvent
//    }
//
//    private fun userJoined(response: Response.UserJoined): UserJoinedEvent {
//        state.groups = state.groups.map { group ->
//            if (group.id == response.id) {
//                group.copy(
//                    clients = (group.clients + (response.userId to response.name)).toMutableMap()
//                )
//            } else {
//                group
//            }
//        }.toMutableList()
//
//        return UserJoinedEvent
//    }
//
//    private fun joined(response: Response.Joined): JoinEvent {
//        val g = GroupState(
//            response.id,
//            clients = response.clients.toMutableMap(),
//            settings = response.settings
//        )
//        state.groups += g
//        state.groupFocus = g
//        return JoinEvent
//    }
//
//    private fun diceRolled(response: Response.DiceRolled): RolledDiceEvent {
//        state.gameFocus.rolledDice(response.resources, response.othersResources, response.moveRobber)
//
//        // TODO: add MoveRobber. LocalGameState class?
//
//        return RolledDiceEvent(
//            response.roll1,
//            response.roll2,
//            response.moveRobber,
//            response.resources,
//            response.othersResources.mapValues { it.value }
//        )
//    }
//
//    private fun chat(response: Response.Chat): Event {
//        val fromId = response.from
//        val message = response.message
//
//        state.chatFocus.chatLog.add(fromId to message)
//        return ChatEvent(state.chatFocus.members[fromId]!!, message) // !
//    }
//
//    private fun build(response: Response.Build): BuildEvent {
//        val builder = response.builder
//        val coordinates = response.coordinates
//        val building = response.buildkind
//        val vp = response.victoryPoints
//
//        state.gameFocus = state.gameFocus.addBuilding(builder, building, coordinates, vp)
//        return BuildEvent(
//            response.builder,
//            response.buildkind,
//            response.coordinates
//        )
//    }
//}


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

