package io.github.petvat.katan.lwjgl3

import io.github.petvat.katan.controller.ResponseProcessor
import io.github.petvat.katan.controller.NioController
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.ui.cli.SimpleCliView


fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
        return

    val model = KatanModel()
    val mainCtrl = NioController(ResponseProcessor(model))

    val cliView = SimpleCliView(model)


    // Set up controllers
    val responseController = ResponseProcessor(model)
    // responseController.views.add(cliView)

    // Thread { cliView.run() }.start()
}
