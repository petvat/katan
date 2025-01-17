package io.github.petvat.katan.server

import io.github.petvat.katan.server.nio.TestClient

fun main() {
    val client = TestClient()
    client.start(portNumber = ServerConstants.PORT)
    println("Test client. DO NOT USE newline")
    //val scanner = Scanner(System.`in`)
    Thread {
        while (true) {
            val message = client.messageQueue.poll() // Poll with timeout
            if (message != null) {
                println(message)
            }
        }
    }.start()
    while (true) {
        val line = readlnOrNull()

        client.forwardRequest(line!!)
    }

}

