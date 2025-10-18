@file:JvmName("Lwjgl3Launcher")

package io.github.petvat.katan.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import io.github.petvat.katan.controller.GameService
import io.github.petvat.katan.model.ClientState
import io.github.petvat.katan.ui.ktx.KtxKatan

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
        return

    val model = ClientState()
    val mainCtrl = GameService(model)
    val ktxView = KtxKatan(model)

    ktxView.controller = mainCtrl


    val dm = Lwjgl3ApplicationConfiguration.getDisplayMode();
    val config = Lwjgl3ApplicationConfiguration().apply {
        setTitle("Katan - v0.0.1")
        setWindowedMode(dm.width / 2, dm.height / 2)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    }

    Lwjgl3Application(ktxView, config)


}
