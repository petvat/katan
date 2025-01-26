package io.github.petvat.katan.ui.model

import io.github.petvat.katan.ui.KatanUI

enum class ScreenType {
    MENU, GAME, GROUP, LOBBY
}


interface ViewTransitionService {
    fun transition(to: ScreenType)
}

//abstract class ViewTransitionService<T : KatanUI>(
//    val ui: T
//) {
//
//    fun transition(to: ScreenType) {
//        ui.showScreen(to)
//    }
//}
