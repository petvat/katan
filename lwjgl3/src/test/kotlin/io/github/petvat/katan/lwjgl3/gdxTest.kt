package io.github.petvat.katan.lwjgl3


import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.math.Vector2
import ktx.app.KtxApplicationAdapter
import ktx.math.vec2


fun gdxTest(title: String, testListener: KtxApplicationAdapter, windowSize: Vector2 = vec2(640F, 480F)) {

    Lwjgl3Application(testListener, Lwjgl3ApplicationConfiguration().apply {
        setTitle(title)
        setWindowedMode(windowSize.x.toInt(), windowSize.y.toInt())
    })
}
