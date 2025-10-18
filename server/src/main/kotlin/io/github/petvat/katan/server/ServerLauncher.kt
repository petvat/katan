@file:JvmName("ServerLauncher")

package io.github.petvat.katan.server


import io.github.petvat.katan.server.nio.NioServer
import io.github.petvat.katan.server.nio.ServerConstants
import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.client.ClientRegistry
import io.github.petvat.katan.server.service.concurrency.LockManager
import io.github.petvat.katan.server.service.handler.CommandHandlerRegistry
import io.github.petvat.katan.server.service.presenter.PresenterRegistry
import io.github.petvat.katan.server.service.service.GameService
import io.github.petvat.katan.server.service.service.GroupService
import io.github.petvat.katan.server.service.service.LobbyService
import io.github.petvat.katan.server.service.service.RequestProcessor

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Launches the TCP server application.
 */
fun main() = runBlocking<Unit> {

    val loggingLevel = System.getProperty("logging.level", "INFO")

    setLoggingLevel(loggingLevel)

    val locks = LockManager()

    val clients = ClientRegistry()
    val commandHandler = CommandHandlerRegistry()
    val channelManager = ChannelRegistry()
    val presenters = PresenterRegistry()

    val lobbyService = LobbyService(commandHandler, presenters, locks, channelManager, clients)
    val groupService = GroupService(commandHandler, presenters, locks, channelManager, clients)
    val gameService = GameService(commandHandler, presenters, locks, channelManager, clients)
    // val chatService = ChatService()

    val requestDispatcher = RequestProcessor(lobbyService, groupService, gameService)
    val server = NioServer(
        lockManager,
        requestDispatcher,
        clientManager
    )


    server.start(ServerConstants.PORT)

    launch {
        println("Server started. Press Enter to shut down.")
        val input = readlnOrNull()
        if (input == null) {
            println("Input is null: stdin might not be connected.")
        }
        println("Shutting down server.")
    }
}


fun setLoggingLevel(level: String) {
    val targetLevel = when (level.uppercase()) {
        "DEBUG" -> ch.qos.logback.classic.Level.DEBUG
        "INFO" -> ch.qos.logback.classic.Level.INFO
        "WARN" -> ch.qos.logback.classic.Level.WARN
        "ERROR" -> ch.qos.logback.classic.Level.ERROR
        else -> ch.qos.logback.classic.Level.INFO // Default to INFO
    }

    // Adjust the root logger level dynamically
    val rootLogger = org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
        as ch.qos.logback.classic.Logger
    rootLogger.level = targetLevel
}
