package io.github.petvat.katan.lwjgl3

import io.github.petvat.katan.controller.RequestController
import io.github.petvat.katan.event.ChatEvent
import io.github.petvat.katan.event.ConnectionEvent
import io.github.petvat.katan.event.EventBus
import io.github.petvat.katan.shared.hexlib.Coordinates
import io.github.petvat.katan.shared.model.board.BuildKind
import io.github.petvat.katan.shared.model.game.Settings


class MockController : RequestController {
    
    override fun handleInit() {
        TODO("Not yet implemented")
    }

    override fun handleCreate(settings: Settings) {
        TODO("Not yet implemented")
    }

    override fun handleJoin(sessionId: String) {
        TODO("Not yet implemented")
    }

    override fun handleGetGroup(pagination: Int) {
        TODO("Not yet implemented")
    }

    override fun connectClient(host: String?, port: Int?): Boolean {
        EventBus.fire(ConnectionEvent)
        return true
    }

    override fun handleChat(message: String, recipients: Set<String>?) {
        EventBus.fire(ChatEvent("You (loop-back)", message))
    }

    override fun handleLogin(username: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun handleClose() {
        TODO("Not yet implemented")
    }

    override fun handleRollDice() {
        TODO("Not yet implemented")
    }

    override fun handleBuild(buildKind: BuildKind, coordinates: Coordinates) {
        TODO("Not yet implemented")
    }

    override fun handleRegister(name: String) {
        TODO("Not yet implemented")
    }

}
