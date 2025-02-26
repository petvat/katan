package io.github.petvat.katan.ui.cli

import io.github.petvat.katan.controller.SimpleCliInputController
import io.github.petvat.katan.event.*
import io.github.petvat.katan.event.EventListener
import io.github.petvat.katan.model.KatanModel
import io.github.petvat.katan.ui.model.ScreenType
import java.util.*

/**
 * NOTE: Not implemented.
 */
class SimpleCliView(val model: KatanModel) {

    private lateinit var _controller: SimpleCliInputController // Backing property


    fun prompt(message: String): String {
        TODO()
    }

    fun promptResponse(message: String): String {
        TODO()
    }
}

/**
 * Simple CLI view.
 *
 */
//class SimpleCliView(val model: KatanModel) : KatanUI, Runnable, EventListener {
//    private val scanner = Scanner(System.`in`)
//
//    private lateinit var _controller: SimpleCliInputController // Backing property
//
//    var controller: SimpleCliInputController
//        get() = _controller
//        set(value) {
//            // Add custom logic here if needed
//            _controller = value
//        }
//
//    fun prompt(message: String, r: Boolean = true) {
//        if (r) {
//            println("\r$message")
//        } else {
//            println(message)
//        }
//    }
//
//    fun promptResponse(message: String): String {
//        prompt(message)
//        return readInput()
//    }
//
//    private fun readInput(): String {
//        return scanner.nextLine().trim()
//    }
//
//    override fun run() {
//        var continueLoop = true
//        prompt(
//            "Running Katan CLI\n" +
//                "Connect to a server"
//        )
//
//        val success = controller.connectClient() // Ignore input for now
//
//        if (success) {
//            prompt("Connected to server at ???.")
//        }
//
//        try {
//            while (!Thread.currentThread().isInterrupted || continueLoop) {
//
//                println("Select an option:")
//                println("1. Login")
//                println("2. Create group")
//                println("3. Join group")
//                println("4. Send message")
//                println("5. Get group.")
//                println("6. Exit")
//
//                print("Enter your choice: ")
//                val choice = scanner.nextLine().trim()
//                // NOTE: Consider moving below to controller via observers.forEach(it.observe(choice))
//
//                when (choice) {
//                    "1" -> {
//                        controller.handleLogin()
//                    }
//
//                    "2" -> controller.handleCreate()
//                    "3" -> controller.handleJoin()
//                    "4" -> controller.handleChat()
//                    "5" -> controller.handelGetGroups()
//                    "6" -> {
//                        println("Exiting...")
//                        continueLoop = false
//                        controller.handleClose()
//                    }
//
//                    "7" -> {}
//
//                    else -> println("Invalid choice, please try again.")
//                }
//            }
//
//        } catch (e: InterruptedException) {
//            println("Interrupt")
//        }
//    }
//
//    fun showMenuView() {
//
//    }
//
//    fun showLobbyView() {
//        println("Groups")
//        for ((i, session) in model.groups.withIndex()) {
//            println("$i: ${session.users}, joinable: ${session.joinable}")
//        }
//    }
//
//    fun showErrorView(error: String) {
//        println("ERROR: $error")
//    }
//
//    fun showGameView() {
//        TODO("Not yet implemented")
//    }
//
//    fun showGroupView(groups: List<Pair<String, String>>) {
//        groups.forEach { g ->
//            println("NAME: , MODE: ${g.second}, ID: ${g.second}")
//        }
//    }
//
//    override fun showScreen(screen: ScreenType) {
//        when (screen) {
//            ScreenType.MENU -> showMenuView()
//            ScreenType.GAME -> showGameView()
//            ScreenType.GROUP -> showGroupView()
//            ScreenType.LOBBY -> showLobbyView()
//        }
//    }
//
//    override fun onEvent(event: Event) {
//        when (event) {
//            is BuildEvent -> TODO()
//            ChatEvent -> TODO()
//            CreateEvent -> TODO()
//            is ErrorEvent -> TODO()
//            is GetGroupsEvent -> TODO()
//            InitEvent -> TODO()
//            is JoinEvent -> TODO()
//            LobbyEvent -> TODO()
//            LoginEvent -> TODO()
//            is NextTurnEvent -> TODO()
//            is PlaceBuildingEvent<*> -> TODO()
//            PlaceInitialSettlementEvent -> TODO()
//            is RolledDiceEvent -> TODO()
//            is SwitchScreenEvent -> TODO()
//            TurnStartEvent -> TODO()
//            is UserJoinedEvent -> TODO()
//        }
//    }
//
//
//}
//

