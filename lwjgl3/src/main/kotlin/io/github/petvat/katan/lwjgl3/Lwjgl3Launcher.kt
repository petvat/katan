@file:JvmName("Lwjgl3Launcher")

package io.github.petvat.katan.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import io.github.petvat.katan.controller.ResponseProcessor
import io.github.petvat.katan.controller.NioController
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.ui.ktx.KtxKatan

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
        return

    val model = KatanModel()
    val responseController = ResponseProcessor(model)
    val mainCtrl = NioController(responseController)

    val ktxView = KtxKatan(model)

    ktxView.controller = mainCtrl

    val config = Lwjgl3ApplicationConfiguration().apply {
        setTitle("Katan")
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    }

    Lwjgl3Application(ktxView, config)


}
