package io.github.petvat.katan.lwjgl3

import io.github.petvat.katan.ui.Assets
import io.github.petvat.katan.ui.ktx.screen.MenuScreen
import io.github.petvat.katan.ui.ktx.screen.loadUISkin
import io.github.petvat.katan.ui.ktx.screen.loadVisUISkin
import io.github.petvat.katan.ui.ktx.view.startView
import io.github.petvat.katan.ui.model.StartMenuViewModel
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

    val viewModel = StartMenuViewModel(MockController()) { }

    override fun setup() {
        stage.actors {
            startView(viewModel, Scene2DSkin.defaultSkin)
        }
    }

}
