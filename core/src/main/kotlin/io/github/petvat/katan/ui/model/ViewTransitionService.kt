package io.github.petvat.core.ui.model


enum class ScreenType {
    MENU, GAME, GROUP, LOBBY, LOGIN
}


typealias ViewTransitionService = (ScreenType) -> Unit

