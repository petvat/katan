package io.github.petvat.katan.server.nio

import io.github.petvat.katan.shared.protocol.Message
import io.github.petvat.katan.shared.protocol.dto.Response
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * TODO: Implement
 *
 * This class is responsible for synchronizing write pushes to a client.
 * To avoid ambuiguity on the client side, messages to a client should not be written in parallel.
 * Upon a new write request, if there is already a message that is being written,
 * the request should put on the message queue.
 *
 * @param messageQueue
 *
 */
class MessageWriter {

    val messageQueue = ConcurrentLinkedQueue<Message<Response>>()


}
