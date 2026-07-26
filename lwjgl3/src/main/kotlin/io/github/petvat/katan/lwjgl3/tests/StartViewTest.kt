package io.github.petvat.katan.lwjgl3.tests

import io.github.petvat.katan.event.EventSystem
import io.github.petvat.katan.ui.ktx.screen.loadUISkin
import io.github.petvat.katan.ui.ktx.view.startView
import io.github.petvat.katan.ui.viewmodel.StartMenuViewModel
import ktx.app.KtxGame
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors

fun main() = gdxTest("Start view test", StartViewTest())

private class StartViewTest : KtxGame<StartTest>() {
    override fun create() {
        loadUISkin()
        addScreen(StartTest())
        setScreen<StartTest>()
    }
}

private class StartTest : AbstractTestScreen() {

    val bus = EventSystem()

    val viewModel = StartMenuViewModel(MockNetworkSession(), {}, bus)

    override fun setup() {
        stage.actors {
            startView(viewModel, Scene2DSkin.defaultSkin)
        }
    }

}
