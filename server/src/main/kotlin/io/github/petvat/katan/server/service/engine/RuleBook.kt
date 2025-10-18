package io.github.petvat.katan.server.service.engine

import io.github.petvat.katan.shared.model.game.Settings

data class RuleBook(
    val moveRobberOn: Int,
    val cardLimit: Int
) {
    companion object {
        fun from(settings: Settings): RuleBook = TODO()
    }
}
