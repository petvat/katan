package io.github.petvat.katan.view.cli

import io.github.petvat.katan.controller.SimpleCliInputController
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.view.KatanView
import java.util.*

/**
 * Simple CLI view.
 *
 */
class SimpleCliView(val model: KatanModel) : KatanView, Runnable {
    private val scanner = Scanner(System.`in`)

    private lateinit var _controller: SimpleCliInputController // Backing property

    var controller: SimpleCliInputController
        get() = _controller
        set(value) {
            // Add custom logic here if needed
            _controller = value
        }

    fun prompt(message: String, r: Boolean = true) {
        if (r) {
            println("\r$message")
        } else {
            println(message)
        }
    }

    fun promptResponse(message: String): String {
        prompt(message)
        return readInput()
    }

    private fun readInput(): String {
        return scanner.nextLine().trim()
    }

    override fun run() {
        var continueLoop = true
        prompt(
            "Running Katan CLI\n" +
                "Connect to a server"
        )

        val success = controller.connectClient() // Ignore input for now

        if (success) {
            prompt("Connected to server at ???.")
        }

        try {
            while (!Thread.currentThread().isInterrupted || continueLoop) {

                println("Select an option:")
                println("1. Login")
                println("2. Create group")
                println("3. Join group")
                println("4. Send message")
                println("5. Get group.")
                println("6. Exit")

                print("Enter your choice: ")
                val choice = scanner.nextLine().trim()
                // NOTE: Consider moving below to controller via observers.forEach(it.observe(choice))

                when (choice) {
                    "1" -> {
                        controller.handleLogin()
                    }

                    "2" -> controller.handleCreate()
                    "3" -> controller.handleJoin()
                    "4" -> controller.handleChat()
                    "5" -> controller.handelGetGroups()
                    "6" -> {
                        println("Exiting...")
                        continueLoop = false
                        controller.handleClose()
                    }

                    "7" -> {}

                    else -> println("Invalid choice, please try again.")
                }
            }

        } catch (e: InterruptedException) {
            println("Interrupt")
        }
    }

    override fun showNewChatMessage() {
        TODO("Not yet implemented")
    }

    override fun showLoggedInView() {
        TODO("Not yet implemented")
    }

    override fun showGroupView() {
        TODO("Not yet implemented")
    }

    override fun showGameView() {
        TODO("Not yet implemented")
    }

    override fun showLobbyView() {
        println("Groups")
        for ((i, session) in model.groups.withIndex()) {
            println("$i: ${session.users}, joinable: ${session.joinable}")
        }
    }

    override fun showErrorView(error: String) {
        println("ERROR: $error")
    }

    override fun showGameUpdate() {
        TODO("Not yet implemented")
    }

    override fun updateGroupsView(groups: List<Pair<String, String>>) {
        groups.forEach { g ->
            println("NAME: , MODE: ${g.second}, ID: ${g.second}")
        }
    }


}


