package io.github.petvat.katan.server.service.command

import io.github.petvat.katan.server.service.client.AuthCredentials
import io.github.petvat.katan.shared.model.game.Settings

data class CreateGroup(val settings: Settings) : LobbyCommand

data class Register(val auth: AuthCredentials) : LobbyCommand
