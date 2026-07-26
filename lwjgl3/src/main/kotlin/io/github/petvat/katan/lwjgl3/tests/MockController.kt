package io.github.petvat.katan.lwjgl3.tests

import io.github.petvat.katan.controller.IChatActions
import io.github.petvat.katan.controller.IGameActions
import io.github.petvat.katan.controller.ILobbyActions
import io.github.petvat.katan.event.BuildEvent
import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.event.RolledDiceEvent
import io.github.petvat.katan.model.GameState
import io.github.petvat.katan.model.addBuilding
import io.github.petvat.katan.model.addDiceRoll
import io.github.petvat.katan.networking.INetworkSession
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.board.VillageKind
import io.github.petvat.katan.shared.model.game.ResourceMapData
import io.github.petvat.katan.shared.model.game.Settings
import kotlin.random.Random


class MockNetworkSession : INetworkSession {
    override fun connect(host: String?, port: Int?): Boolean {
        TODO("Not yet implemented")
    }

    override fun poll() {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

}

class MockGameActions(private val eventSystem: EventSystem? = null, private val state: GameState? = null) :
    IGameActions {
    override fun rollDice(): Int {
        if (eventSystem == null || state == null) return -1

        val roll1 = 1
        val roll2 = 3


        val resources = ResourceMapData(2, 2, 2, 2, 2)
        val otherResources = mapOf(2 to 10, 3 to 10, 4 to 10)

        state.addDiceRoll(resources, otherResources)
        eventSystem.fire(RolledDiceEvent(roll1, roll2, false))
        return 0
    }

    override fun build(buildKind: BuildKind, coordinates: Coordinates): Int {
        if (eventSystem == null || state == null) return -1

        when (buildKind) {
            is BuildKind.Road -> TODO()
            is BuildKind.Village -> when (buildKind.kind) {
                VillageKind.SETTLEMENT -> state.addBuilding(state.player, buildKind, coordinates, state.victoryPoints)
                VillageKind.CITY -> TODO()
            }
        }
        eventSystem.fire(BuildEvent(state.player, buildKind, coordinates))
        return 0
    }

    override fun init(): Int {
        TODO("Not yet implemented")
    }
}

class MockChatActions : IChatActions {
    override fun sendMessage(message: String): Int {
        TODO("Not yet implemented")
    }
}

class MockLobbyActions : ILobbyActions {
    override fun register(name: String): Int {
        TODO("Not yet implemented")
    }

    override fun create(settings: Settings): Int {
        TODO("Not yet implemented")
    }

    override fun join(channelId: String): Int {
        TODO("Not yet implemented")
    }

}

//
//
//class MockController : RequestController {
//
//    override fun handleInit() {
//        TODO("Not yet implemented")
//    }
//
//    override fun handleCreate(settings: Settings) {
//        TODO("Not yet implemented")
//    }
//
//    override fun handleJoin(sessionId: String) {
//        TODO("Not yet implemented")
//    }
//
//    override fun handleGetGroup(pagination: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun connectClient(host: String?, port: Int?): Boolean {
//        EventBus.fire(ConnectionEvent)
//        return true
//    }
//
//    override fun handleChat(message: String, recipients: Set<String>?) {
//        println("reached chat mock handling")
//        EventBus.fire(ChatEvent("You (loop-back)", message))
//    }
//
//    override fun handleLogin(username: String, password: String) {
//        TODO("Not yet implemented")
//    }
//
//    override fun handleClose() {
//        TODO("Not yet implemented")
//    }
//
//    override fun handleRollDice() {
//        TODO("Not yet implemented")
//    }
//
//    override fun handleBuild(buildKind: BuildKind, coordinates: Coordinates) {
//        TODO("Not yet implemented")
//    }
//
//    override fun handleRegister(name: String) {
//        TODO("Not yet implemented")
//    }
//
//}
