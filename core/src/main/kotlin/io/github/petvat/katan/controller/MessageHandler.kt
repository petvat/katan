package io.github.petvat.katan.controller

import io.github.petvat.katan.event.*
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.protocol.ActionCode
import io.github.petvat.katan.shared.protocol.dto.*


/**
 * TODO: Too complex system, refact.
 *
 */
interface MessageHandler<PayloadDTO> {
    fun handle(response: PayloadDTO, model: KatanModel)
}

object AckMessageHandler : MessageHandler<Response.ConnectionAccept> {
    override fun handle(response: Response.ConnectionAccept, model: KatanModel) {
        model.sessionId = response.sessionId
        InEventBus.fire(LobbyEvent)
    }
}

object LoginMessageHandler : MessageHandler<Response.Login> {
    override fun handle(response: Response.Login, model: KatanModel) {
        model.userInfo = response.userInfo
        model.accessToken = response.token
        InEventBus.fire(LoginEvent)
    }
}

object CreateMessageHandler : MessageHandler<Response.Create> {
    override fun handle(response: Response.Create, model: KatanModel) {
        model.group = (
            PrivateGroupView(
                response.groupId,
                mutableMapOf(model.sessionId!! to "PLACEHOLDER"),
                response.level,
                chatLog = mutableListOf(),
                response.settings
            )
            )
        InEventBus.fire(CreateEvent)
    }
}

object GetGroupsMessageHandler : MessageHandler<Response.Groups> {
    override fun handle(response: Response.Groups, model: KatanModel) {
        model.groups.addAll(response.groups)
        InEventBus.fire(GetGroupsEvent)
    }
}

object ChatMessageHandler : MessageHandler<Response.Chat> {
    override fun handle(response: Response.Chat, model: KatanModel) {
        model.group!!.chatLog += (response.senderId to response.message)
        // views.forEach { it.showNewChatMessage() }
        InEventBus.fire(ChatEvent)
    }
}

object InitMessageHandler : MessageHandler<Response.Init> {
    override fun handle(response: Response.Init, model: KatanModel) {
        model.game = response.privateGameState
        InEventBus.fire(InitEvent)
    }
}

object JoinMessageHandler : MessageHandler<Response.Join> {

    override fun handle(response: Response.Join, model: KatanModel) {
        if (response.joinedUser != null) {
            model.group!!.clients[response.joinedUser!!.id] = "PLACEHOLDER"
            InEventBus.fire(
                UserJoinedEvent(response.joinedUser!!)
            )
        } else {
            model.group = response.groupView
            InEventBus.fire(
                JoinEvent(response.groupView!!)
            )
        }
        // views.forEach { view -> view.showGroupView() }
    }

}

object ActionMessageHandler : MessageHandler<ActionResponse> {

    override fun handle(response: ActionResponse, model: KatanModel) {
        when (response.actionCode) {
            ActionCode.SETUP_END -> SetupEndHandler.handle(response as ActionResponse.SetupEnded)
            ActionCode.ROLL_DICE -> RollDiceHandler.handle(response as ActionResponse.RollDice, model)
            ActionCode.MOVE_ROBBER -> TODO()
            ActionCode.STEAL_CARD -> TODO()
            ActionCode.BUILD -> BuildHandler.handle(response as ActionResponse.Build, model)
            ActionCode.INIT_TRADE -> TODO()
            ActionCode.RESPOND_TRADE -> TODO()
            ActionCode.TURN_END -> TurnEndHandler.handle(response as ActionResponse.EndTurn, model)
            ActionCode.CLAIM_VICTORY -> TODO()
            ActionCode.INIT_SETTL -> TODO()
        }
        // Shared
    }

    object SetupEndHandler : MessageHandler<ActionResponse.SetupEnded> {
        override fun handle(response: ActionResponse.SetupEnded, model: KatanModel) {
            model.game!!.turnPlayer = response.nextPlayer
            InEventBus.fire(NextTurnEvent(model.game!!.turnPlayer))
        }
    }

    object TurnEndHandler : MessageHandler<ActionResponse.EndTurn> {
        override fun handle(response: ActionResponse.EndTurn, model: KatanModel) {
            model.game!!.turnPlayer = response.nextPlayer
            InEventBus.fire(NextTurnEvent(model.game!!.turnPlayer))
        }
    }

    object RollDiceHandler : MessageHandler<ActionResponse.RollDice> {
        override fun handle(response: ActionResponse.RollDice, model: KatanModel) {
            // TODO: Visual change by calling model change after

            //val gameModel = model.gameViewModel!!

            // Model change

            model.game!!.player.inventory = response.resources
            model.game!!.otherPlayers.forEach {
                it.cardCount = response.otherPlayersResources[it.playerNumber]!!
            }

            // TODO:  Decouple this logic
            //gameModel.thisPlayer.inventory = response.resources
            //val playerDiff = gameModel.thisPlayer.inventory.difference(response.resources)
            //val otherPlayersDiff = mutableMapOf<Int, Int>()
            // TODO: refact
//            response.otherPlayersResources.forEach { (id, cardcount) ->
//                val player = gameModel.otherPlayers.find { it.playerNumber == id }
//                otherPlayersDiff[id] = -(player!!.cardCount - cardcount)
//                player.cardCount = cardcount // Model change
//            }

            // NOTE: Have GameModelView as EventListener with RollDiceEvent no input.
            // NOTE: Should View use ViewModel only to understand state?
            // EventService.fire(RollDiceEvent)
            // NOTE: OR
            InEventBus.fire(RolledDiceEvent(response.roll1, response.roll2, response.moveRobber, null, null))
            // EventService.fireRollDiceEvent(RollDiceEvent(response.roll1, response.roll2, playerDiff, otherPlayersDiff))
            //views.forEach { it.}
        }
    }


    /**
     * Handles response of any build message.
     */
    object BuildHandler : MessageHandler<ActionResponse.Build> {
        override fun handle(response: ActionResponse.Build, model: KatanModel) {
            // val gameModel = model.gameViewModel!!

            when (response.buildKind) {
                is BuildKind.Road -> {

                    if (response.coordinate in model.game!!.board.paths.map { it.coordinate }) {
                        model.game!!.board.paths
                            .find { it.coordinate == response.coordinate }
                            .road
                    }

                }

                is BuildKind.Village -> {
                    if ((response.buildKind as BuildKind.Village).kind == VillageKind.SETTLEMENT) {
                        model.game!!.board.intersections += IntersectionView(
                            response.coordinate as ICoordinates,
                            VillageView(VillageKind.SETTLEMENT, response.playerNumber)
                        )
                    }

                    if ((response.buildKind as BuildKind.Village).kind == VillageKind.CITY) {
                        response.
                    }
                }
            }
            model.game!!.board

//            if (gameModel.thisPlayer.playerNumber == response.playerNumber) {
//                gameModel.thisPlayer.victoryPoints = response.victoryPoints
//            } else {
//                val player = gameModel.otherPlayers.find { it.playerNumber == response.playerNumber }
//                player!!.victoryPoints = response.victoryPoints
//            }
            // NOTE: OR RENDER WHOLE THING
            InEventBus.fire(BuildEvent(response.playerNumber, response.buildKind, response.coordinate))
        }
    }
}



