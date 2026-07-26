package io.github.petvat.katan.lwjgl3.tests

import io.github.petvat.katan.ui.ktx.screen.loadUISkin
import io.github.petvat.katan.ui.ktx.widget.error
import ktx.app.KtxGame
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d

fun main() = gdxTest("Error test", ErrorTest())

private class ErrorTest : KtxGame<ErrorViewTest>() {
    override fun create() {
        loadUISkin()
        addScreen(ErrorViewTest())
        setScreen<ErrorViewTest>()
    }
}


private class ErrorViewTest : AbstractTestScreen() {
    override fun setup() {
        stage.addActor(
            scene2d.error("This is a test error! Does it look good?", Scene2DSkin.defaultSkin)
        )

    }

}


