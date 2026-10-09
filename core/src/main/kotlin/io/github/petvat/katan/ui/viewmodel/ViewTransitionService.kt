package io.github.petvat.katan.ui.viewmodel


enum class ScreenType {
    MENU, GAME, GROUP, LOBBY, LOGIN
}

typealias ViewTransitionService = (ScreenType) -> Unit
