package io.github.petvat.katan.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.petvat.katan.client.NioKatanClient

/**
 * This class listens on the message queue of a [NioKatanClient] and forwards any polled message to a [ResponseProcessor].
 *
 * NOTE: Runs in separate thread.
 */
class ResponseDispatcher(private val client: NioKatanClient, private val responseProcessor: ResponseProcessor) :
    Runnable {
    private val logger = KotlinLogging.logger { }
    override fun run() {
        logger.debug { "Dispatcher init." }

        while (true) {
            val message = client.messageQueue.poll()
            if (message != null) {
                logger.debug { "Polled message from NIO client message queue." }

                responseProcessor.process(message)
            }
        }
    }
}
