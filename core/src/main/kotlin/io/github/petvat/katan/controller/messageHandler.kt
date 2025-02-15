package io.github.petvat.katan.controller

import io.github.petvat.katan.event.*
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.protocol.ActionCode
import io.github.petvat.katan.shared.protocol.dto.*

typealias MessageHandler<PayloadDTO> = (response: PayloadDTO, model: KatanModel) -> Event

val ackHandler: MessageHandler<Response.ConnectionAccept> = { response, model ->
    model.sessionId = response.sessionId
    LobbyEvent
}

val loginHandler: MessageHandler<Response.Login> = { response, model ->
    model.userInfo = response.userInfo
    model.accessToken = response.token
    LoginEvent
}

val createHandler: MessageHandler<Response.Create> = { response, model ->
    model.createGroup(response.groupId, response.level, response.settings)
    CreateEvent
}

val getGroupsHandler: MessageHandler<Response.Groups> = { response, model ->
    model.groups.addAll(response.groups)
    GetGroupsEvent
}


val chatHandler: MessageHandler<Response.Chat> = { response, model ->
    model.group.chatLog += response.senderId to response.message
    ChatEvent
}

val initHandler: MessageHandler<Response.Init> = { response, model ->
    model.game = response.privateGameState
    InitEvent
}

val joinHandler: MessageHandler<Response.Join> = { response, model ->
    if (response.joinedUser != null) {
        model.userJoin(response.joinedUser!!.id, response.joinedUser!!.username)
        UserJoinedEvent
    } else {
        model.group = response.groupView!!
        JoinEvent
    }
}

val actionHandler: MessageHandler<ActionResponse> = { response, model ->
    when (response.actionCode) {
        ActionCode.SETUP_END -> setupHandler(response as ActionResponse.SetupEnded, model)
        ActionCode.ROLL_DICE -> rollDiceHandler(response as ActionResponse.RollDice, model)
        ActionCode.MOVE_ROBBER -> moveRobber(response as ActionResponse.MoveRobber, model)
        ActionCode.STEAL_CARD -> TODO()
        ActionCode.BUILD -> buildHandler(response as ActionResponse.Build, model)
        ActionCode.INIT_TRADE -> TODO()
        ActionCode.RESPOND_TRADE -> TODO()
        ActionCode.TURN_END -> turnEndedHandler(response as ActionResponse.EndTurn, model)
        ActionCode.CLAIM_VICTORY -> TODO()
        ActionCode.INIT_SETTL -> initSettlHandler(response as ActionResponse.PlaceInitSettlement, model)
    }
}

// GAME ACTIONS:

val setupHandler: MessageHandler<ActionResponse.SetupEnded> = { _, model ->
    model.incrementTurn()
    NextTurnEvent
}
val turnEndedHandler: MessageHandler<ActionResponse.EndTurn> = { _, model ->
    model.incrementTurn()
    NextTurnEvent
}

val rollDiceHandler: MessageHandler<ActionResponse.RollDice> = { response, model ->
    model.diceRolled(response.resources, response.otherPlayersResources, response.moveRobber)
    RolledDiceEvent(response.roll1, response.roll2, response.moveRobber, null, null)
}


val initSettlHandler: MessageHandler<ActionResponse.PlaceInitSettlement> = { response, model ->
    model.newBuilding(response.playerNumber, BuildKind.Village(VillageKind.SETTLEMENT), response.coordinates)
    PlaceInitialSettlementEvent(
        playerNumber = response.playerNumber,
        coordinates = response.coordinates as ICoordinates
    )
}

val buildHandler: MessageHandler<ActionResponse.Build> = { response, model ->
    model.newBuilding(response.playerNumber, response.buildKind, response.coordinate)
    BuildEvent(response.playerNumber, response.buildKind, response.coordinate)
}

val moveRobber: MessageHandler<ActionResponse.MoveRobber> = { response, model ->
    TODO()
}


