package io.github.petvat.core.controller

import io.github.petvat.katan.event.*
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.protocol.*

typealias MessageHandler<PayloadDTO> = (response: PayloadDTO, model: KatanModel) -> Event

//val ackHandler: MessageHandler<Response.ConnectAck> = { response, model ->
//    model.sessionId = response.sessionId
//    LobbyEvent
//}

//val loginHandler: MessageHandler<Response.Login> = { response, model ->
//    model.userInfo = response.userInfo
//    model.accessToken = response.token
//    LoginEvent
//}

val groupPushHandler: MessageHandler<Response.LobbyUpdate> = { response, model ->
    // TODO: MODEL!
    val group = response.groupDTO
    GroupUpdateEvent(group.id, group.level, group.mode, group.numClients, group.maxClients)
}

val createHandler: MessageHandler<Response.GroupCreated> = { response, model ->
    model.createGroup(response.groupId, response.level, response.settings)
    CreateEvent
}


//val getGroupsHandler: MessageHandler<Response.Groups> = { response, model ->
//    // model.groups.addAll(response.groups) // NOTE: Don't need to store this in model.
//    GetGroupsEvent(response.groups)
//}


val chatHandler: MessageHandler<Response.Chat> = { response, model ->
    model.group.chatLog += response.from to response.message
    ChatEvent(model.group.chatLog.last().first, model.group.chatLog.last().second)
}

val initHandler: MessageHandler<Response.Init> = { response, model ->
    model.game = response.privateGameState
    InitEvent
}

val joinHandler: MessageHandler<Response.Joined> = { response, model ->
    model.group = response.groupDTO
    JoinEvent
}

val playerJoinedHandler: MessageHandler<Response.UserJoined> = { response, model ->
    model.userJoin(response.sessionId, response.name)
    UserJoinedEvent
}

// GAME ACTIONS:

val setupHandler: MessageHandler<Response.SetupEnded> = { _, model ->
    model.incrementTurn()
    NextTurnEvent(model.game.turnPlayer)
}
val turnEndedHandler: MessageHandler<Response.EndTurn> = { _, model ->
    model.incrementTurn()
    NextTurnEvent(model.game.turnPlayer)
}

val rollDiceHandler: MessageHandler<Response.DiceRolled> = { response, model ->
    model.diceRolled(response.resources, response.othersResources, response.moveRobber)
    RolledDiceEvent(
        response.roll1,
        response.roll2,
        response.moveRobber,
        response.resources,
        response.othersResources.mapValues { it.value.count() }
    )
}


val initSettlHandler: MessageHandler<Response.Build> = { response, model ->
    model.newBuilding(response.builder, BuildKind.Village(VillageKind.SETTLEMENT), response.coordinates)
    PlaceInitialSettlementEvent(
        playerNumber = response.builder,
        coordinates = response.coordinates as ICoordinates
    )
}

val errorHandler: MessageHandler<Response.Error> = { response, _ ->
    ErrorEvent(response.description ?: "No description.")
}

val buildHandler: MessageHandler<Response.Build> = { response, model ->
    model.newBuilding(response.builder, response.buildkind, response.coordinates)
    BuildEvent(response.builder, response.buildkind, response.coordinates)
}

val moveRobberHandler: MessageHandler<Response.RobberMoved> = { response, model ->
    TODO()
}

val loginHandler: MessageHandler<Response.Registered> = { response, model ->
    model.sessionId = response.sid
    LoginEvent

}


