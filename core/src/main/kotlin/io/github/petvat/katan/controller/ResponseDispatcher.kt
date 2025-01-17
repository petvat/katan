package io.github.petvat.katan.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.client.NioKatanClient
import io.github.petvat.katan.client.ResponseController


class ResponseDispatcher(private val client: NioKatanClient, private val responseProcessor: ResponseController) :
    Runnable {
    private val logger = KotlinLogging.logger { }
    override fun run() {
        logger.debug { "Dispatcher init." }

        Thread {
            while (true) {
                val message = client.messageQueue.poll()
                if (message != null) {
                    logger.debug { "Polled message from NIO client message queue." }

                    responseProcessor.process(message)
                }
            }
        }.start()
    }
}
