@file:JvmName("Lwjgl3Launcher")

package io.github.petvat.katan.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import io.github.petvat.katan.controller.ResponseController
import io.github.petvat.katan.controller.KtxInputController
import io.github.petvat.katan.controller.MainController
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.ui.ktx.KtxKatan

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
        return

    val model = KatanModel()
    val responseController = ResponseController(model)
    val mainCtrl = MainController(model, responseController)

    // Thread { cliView.run() }.start()

    val ktxView = KtxKatan(model)
    // val cliView = SimpleCliView(model)

    ktxView.controller = (KtxInputController(mainCtrl, ktxView))
    // cliView.controller = (SimpleCliInputController(mainCtrl, cliView))

    // Set up controllers
    responseController.views.add(ktxView)
    // responseController.views.add(cliView)

    // dm = Lwjgl3ApplicationConfiguration.getDisplayMode();
    //.setWindowedMode(dm.width / 2, dm.height / 2)


    Lwjgl3Application(ktxView, Lwjgl3ApplicationConfiguration().apply {
        setTitle("Katan")
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })


}
