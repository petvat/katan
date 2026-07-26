@file:JvmName("ServerLauncher")

package io.github.petvat.katan.server


import io.github.petvat.katan.server.nio.NioServer
import io.github.petvat.katan.server.nio.ServerConstants
import io.github.petvat.katan.server.service.channel.ChannelRegistry
import io.github.petvat.katan.server.service.channel.ResumeTokenStore
import io.github.petvat.katan.server.service.client.ClientRegistry
import io.github.petvat.katan.server.service.client.UserRegistry
import io.github.petvat.katan.server.service.concurrency.LockManager
import io.github.petvat.katan.server.service.gateway.*
import io.github.petvat.katan.server.service.handler.CommandHandlerRegistry
import io.github.petvat.katan.server.service.presenter.PresenterRegistry

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import ch.qos.logback.classic.*
import io.github.petvat.katan.server.service.channel.ChannelId
import io.github.petvat.katan.server.service.channel.LobbyChannel
import java.util.concurrent.ConcurrentHashMap

/**
 * Launches the TCP server application.
 */
fun main() = runBlocking<Unit> {

    val loggingLevel = System.getProperty("logging.level", "DEBUG")

    setLoggingLevel(loggingLevel)

    val server = buildServer()

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

fun buildServer(): NioServer {
    val clientRegistry = ClientRegistry()
    val userRegistry = UserRegistry()
    val channelManager = ChannelRegistry()
    val lockManager = LockManager()
    val presenterRegistry = PresenterRegistry(userRegistry)
    val handlerRegistry = CommandHandlerRegistry(channelManager)
    val tokenStore = ResumeTokenStore()

    val lobbyChannel = LobbyChannel(ChannelId("lobby:main"), ConcurrentHashMap()) // exactly one, ever
    channelManager.register(lobbyChannel.id, lobbyChannel)


    val authService = AuthService(clientRegistry, userRegistry, channelManager, lobbyChannel, tokenStore)
    val lobbyService =
        LobbyService(handlerRegistry, presenterRegistry, lockManager, channelManager, clientRegistry, userRegistry)
    val groupService =
        GroupService(handlerRegistry, presenterRegistry, lockManager, channelManager, clientRegistry, userRegistry)
    val gameService =
        GameService(handlerRegistry, presenterRegistry, lockManager, channelManager, clientRegistry, userRegistry)
    val chatService =
        ChatService(handlerRegistry, presenterRegistry, lockManager, channelManager, clientRegistry, userRegistry)

    val requestProcessor = RequestProcessor(authService, lobbyService, groupService, gameService, chatService)

    return NioServer(requestProcessor, clientRegistry)
}


fun setLoggingLevel(level: String) {
    val targetLevel = when (level.uppercase()) {
        "DEBUG" -> Level.DEBUG
        "INFO" -> Level.INFO
        "WARN" -> Level.WARN
        "ERROR" -> Level.ERROR
        else -> Level.DEBUG // Default to INFO
    }

    // Adjust the root logger level dynamically
    val rootLogger = org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
        as Logger
    rootLogger.level = targetLevel
}
