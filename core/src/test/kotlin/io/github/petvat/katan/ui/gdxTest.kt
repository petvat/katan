package io.github.petvat.katan.lwjgl3

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import io.github.petvat.katan.ui.LobbyViewTest

fun gdxTest(title: String, testListener: ApplicationListener, windowSize: Vector2 = vec2(1280f, 960f)) {
    Lwjgl3Application(testListener, Lwjgl3ApplicationConfiguration().apply {
        setTitle(title)
        setWindowedMode(windowSize.x.toInt(), windowSize.y.toInt())
    })
}


fun main() = gdxTest("UI Lobby test", LobbyViewTest())

