package io.github.petvat.katan.server

import com.google.gson.JsonParser
import io.github.petvat.katan.server.api.RequestProcessor
import java.io.*
import java.net.ServerSocket
import java.net.Socket


/**
 * KatanServer
 *
 * Communicates with clients through
 */
class KatanServer(private val serverSocket: ServerSocket) {

    private val sessions: MutableMap<String, Session> = mutableMapOf()

    fun startServer() {
        try {
            while (!serverSocket.isClosed) {
                val clientSocket = serverSocket.accept() // Blocks thread until new connection

                Thread { handleNewClient(clientSocket) }.start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun handleNewClient(socket: Socket) {
        val inn = BufferedReader(InputStreamReader(socket.getInputStream()))
        val out = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

        out.write("DEBUG: Session name")
        val sessionName = inn.readLine()

        // If session does not exist, create new Session
        synchronized(sessions) {
            if (sessions[sessionName] == null) {
                sessions[sessionName] = Session(sessionName)
            }
        }
        // TODO: Give playerID - or UserID, log in
        val clientHandler = ClientHandler(0, socket, sessions[sessionName]!!)
        // Execute run
        Thread(clientHandler).start()
    }
}


class Session(val name: String) {
    private val _clientHandlers: MutableSet<ClientHandler> = mutableSetOf()

    val clients: Set<ClientHandler> get() = _clientHandlers.toSet()

    fun getClientBy(playerID: Int): ClientHandler {
        return _clientHandlers.find { c -> c.playerID == playerID }
            ?: throw IllegalArgumentException("No such client exist.")
    }

    fun addClient(clientHandler: ClientHandler) {
        _clientHandlers.add(clientHandler)
        // Load in UserData, name, profilepicture
    }

    fun removeClient(clientHandler: ClientHandler) {
        _clientHandlers.remove(clientHandler)
    }
}

/**
 * PC pattern
 */
class Client(val socket: Socket, val playerID: Int) {
    private val bufferedWriter = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
    private val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))


    fun sendMessage(message: String) {
        bufferedWriter.write(message) // This should wake up the clientHandler
        bufferedWriter.newLine()
        bufferedWriter.flush()
    }

    fun receiveMessage(message: String) {
        println(message)
    }

    fun listenForMessage() {
        Thread {
            while (socket.isConnected) {
                try {
                    val message = bufferedReader.readLine()
                    // Simple check for now
                    println(message)
                } catch (e: IOException) {
                    TODO()
                }
            }
        }.start()

    }
}


class ClientHandler(val playerID: Int, private val clientSocket: Socket, val session: Session) : Runnable {
    private val inn = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
    private val out = BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream()))

    override fun run() {
        try {
            session.addClient(this)

            var message: String
            while (inn.readLine().also { message = it } != null) {
                val request = JsonParser.parseString(message).asJsonObject
                val success = RequestProcessor.handleRequest(this, request, session)
                if (!success) {
                    out.write("Request failed.")
                }
            }
        } catch (e: IOException) {
            TODO()
        }
    }

    fun sendMessage(message: String) {
        out.write(message)
    }
}


fun main() {
    val server = KatanServer(ServerSocket(1234))
    server.startServer()
}
