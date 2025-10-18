package io.github.petvat.katan.controller

import io.github.petvat.katan.event.*
import io.github.petvat.katan.model.*
import io.github.petvat.katan.shared.hexlib.ICoordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.protocol.*


typealias MessageHandler<R> = (response: R, state: ClientState) -> Event

val groupPushHandler: MessageHandler<Response.LobbyUpdate> = { response, model ->
    val group = response.groupDTO
    GroupUpdateEvent(group.id, group.level, group.mode, group.numClients, group.maxClients)
}

val createHandler: MessageHandler<Response.GroupCreated> = { response, model ->
    model.groupModel = GroupState(
        groupId = response.groupId,
        chatLog = mutableListOf(),
        level = response.level,
        settings = response.settings,
        clients = mutableMapOf()
    )
    CreateEvent
}

val chatHandler: MessageHandler<Response.Chat> = { response, model ->
    model.groupModel.chatLog += model.groupModel.clients[response.from]!! to response.message
    ChatEvent(model.groupModel.chatLog.last().first, model.groupModel.chatLog.last().second) // !
}

val initHandler: MessageHandler<Response.Init> = { response, model ->
    model.gameModel = response.privateGameState.toClientModel()
    InitEvent
}

val joinHandler: MessageHandler<Response.Joined> = { response, model ->
    model.groupModel = response.groupDTO.toClientModel()
    JoinEvent
}

val playerJoinedHandler: MessageHandler<Response.UserJoined> = { response, model ->
    model.groupModel.clients += response.sessionId to response.name
    UserJoinedEvent
}

// GAME ACTIONS:

val setupHandler: MessageHandler<Response.SetupEnded> = { _, model ->
    //model.incrementTurn()
    NextTurnEvent(model.gameModel.turnPlayer)
}
val turnEndedHandler: MessageHandler<Response.EndTurn> = { _, model ->
    // model.incrementTurn()
    NextTurnEvent(model.gameModel.turnPlayer)
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
    //model.newBuilding(response.builder, BuildKind.Village(VillageKind.SETTLEMENT), response.coordinates)
    PlaceInitialSettlementEvent(
        playerNumber = response.builder,
        coordinates = response.coordinates as ICoordinates
    )
}

val errorHandler: MessageHandler<Response.Error> = { response, _ ->
    ErrorEvent(response.description ?: "No description.")
}

val buildHandler: MessageHandler<Response.Build> = { response, model ->

    // model.newBuilding(response.builder, response.buildkind, response.coordinates)
    BuildEvent(response.builder, response.buildkind, response.coordinates)
}

val moveRobberHandler: MessageHandler<Response.RobberMoved> = { response, model ->
    TODO()
}

val loginHandler: MessageHandler<Response.Registered> = { response, model ->
    model.sessionId = response.sid
    model.name = response.name
    LoginEvent

}


