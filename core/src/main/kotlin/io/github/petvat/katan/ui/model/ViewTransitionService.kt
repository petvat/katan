package io.github.petvat.katan.ui.model


enum class ScreenType {
    MENU, GAME, GROUP, LOBBY, LOGIN
}


typealias ViewTransitionService = (ScreenType) -> Unit

