@file:JvmName("ServerLauncher")

package io.github.petvat.katan.server

import io.github.petvat.katan.server.nio.NioServer
import io.github.petvat.katan.shared.protocol.SessionId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.channels.SocketChannel


/**
 * Launches the server application.
 */
fun main() = runBlocking<Unit> {

    val loggingLevel = System.getProperty("logging.level", "INFO")

    setLoggingLevel(loggingLevel)

    val scope = CoroutineScope(Dispatchers.IO)

    val requestChannel = Channel<Pair<SessionId, String>>();
    val responseChannel = Channel<Pair<SocketChannel, String>>();

    val server = NioServer(requestChannel, responseChannel)

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
