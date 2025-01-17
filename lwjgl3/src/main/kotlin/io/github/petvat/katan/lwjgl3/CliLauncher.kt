package io.github.petvat.katan.lwjgl3

import io.github.petvat.katan.client.ResponseController
import io.github.petvat.katan.controller.MainController
import io.github.petvat.katan.controller.SimpleCliInputController
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.view.cli.SimpleCliView


fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
        return

    val model = KatanModel()
    val mainCtrl = MainController(model, ResponseController(model))

    val cliView = SimpleCliView(model)

    cliView.controller = (SimpleCliInputController(mainCtrl, cliView))

    // Set up controllers
    val responseController = ResponseController(model)
    responseController.views.add(cliView)

    Thread { cliView.run() }.start()
}
