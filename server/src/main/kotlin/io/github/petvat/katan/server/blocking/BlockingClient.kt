package io.github.petvat.katan.server.blocking

import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.*
import java.net.Socket

interface ResponseHandler<T> {
    fun handleResponse(response: T)
}

/**
 * This class represents the client side endpoint of the communication socket.
 *
 * Through [forwardRequest] and [listenForResponse], the client can send and receive requests from server endpoint.
 * @property _out outgoing requests get sent from here
 * @property _in incoming responses get delivered from here
 * @property Out Object sent to [_out]
 * @property In Object received from [_in]
 * @property responseHandler Collection of [ResponseHandler] ready to handle a incoming request.
 */
abstract class AbstractClient<In, Out>(
    val socket: Socket,
) {
    private val _logger = KotlinLogging.logger {}
    private val _out = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
    private val _in = BufferedReader(InputStreamReader(socket.getInputStream()))
    abstract val responseHandler: ResponseHandler<In>


    init {
        try {
            if (socket.isConnected) {
                _logger.debug { "Client successfully initialized." }
            } else {
                throw IOException("Socket is not connected.")
            }
        } catch (e: Exception) {
            tearDownSocket()
            _logger.error { "Fatal: ${e.message}. Socket disconnected. Socket closed." }
        }
    }

    /**
     * Convert the request message from [Out] to [String]
     */
    protected abstract fun processRequest(request: Out): String

    /**
     * Convert the response message from [String] to [In]
     */
    protected abstract fun processResponse(response: String): In

    /**
     * Writes a request to the out stream.
     *
     * @param request the request message
     */
    fun forwardRequest(request: Out) {
        _out.write(processRequest(request))
        _out.newLine()
        _out.flush()
    }

    /**
     * Listens for responses from client handler and forward them to response handlers.
     *
     * @throws IOException
     */
    fun listenForResponse() {
        Thread {
            while (socket.isConnected) {
                try {
                    val message = _in.readLine() // Blocking function
                    try {

                        responseHandler.handleResponse(processResponse(message)) // TODO: remove dependency on ResponseHandler and make forwardResponse(response: In)

                    } catch (e: Exception) {
                        _logger.error { "Uncaught: ${e.message}" }
                    }
                } catch (e: IOException) {
                    _logger.error { e.message }
                    tearDownSocket()
                }
            }
        }.start()

    }

    private fun tearDownSocket() {
        _out.close()
        _in.close()
        socket.close()
    }
}


///**
// * This class represents the client side endpoint of the communication socket.
// * Through [forwardRequest] and [listenForResponse], the [Client] can send and receive requests from server endpoint.
// * @property _out outgoing requests get sent from here
// * @property _inn incoming responses get delivered from here
// */
//class Client(private val socket: Socket) {
//    private val _logger = KotlinLogging.logger {}
//    private val _out = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
//    private val _inn = BufferedReader(InputStreamReader(socket.getInputStream()))
//
//    init {
//        try {
//            if (socket.isConnected) {
//                _logger.debug { "Client successfully initialized." }
//            } else {
//                throw IOException("Socket is not connected.")
//            }
//        } catch (e: Exception) {
//            tearDownSocket()
//            println("Fatal: ${e.message}. Socket closed.")
//        }
//    }
//
//    /**
//     * Forward message to client handler.
//     *
//     */
//    fun forwardRequest(request: RequestMessage) {
//        val asJson = GsonParser.toJson(request).toString()
//        _logger.debug { "Forward message: $asJson" }
//        _out.write(asJson)
//        _out.newLine()
//        _out.flush()
//    }
//
//    private fun forwardRequest(request: String) {
//        _out.write(request)
//        _out.newLine()
//        _out.flush()
//    }
//
//    /*
//     * Will wait for messageprivate val _logger = KotlinLogging.logger {}
//     *     private val _out = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
//     *     private val _inn = BufferedReader(InputStreamReader(socket.getInputStream()))
//     *
//     *     init {
//     *         try {
//     *             if (socket.isConnected) {
//     *                 _logger.debug { "Client successfully initialized." }
//     *             } else {
//     *                 throw IOException("Socket is not connected.")
//     *             }
//     *         } catch (e: Exception) {
//     *             tearDownSocket()
//     *             println("Fatal: ${e.message}. Socket closed.")
//     *         }
//     *     }
//     *
//     *     /**
//     *      * Forward message to client handler.
//     *      *
//     *      */
//     *     fun forwardRequest(request: RequestMessage) {
//     *         val asJson = GsonParser.toJson(request).toString()
//     *         _logger.debug { "Forward message: $asJson" }
//     *         _out.write(asJson)
//     *         _out.newLine()
//     *         _out.flush()
//     *     }
//     *
//     *     private fun forwardRequest(request: String) {
//     *         println("Forward message: $request")
//     *         _out.write(request)
//     *         _out.newLine()
//     *         _out.flush()
//     *     }
//     *
//     *     /**
//     *      * Will wait for messages sent by ClientHandler.sendMessage
//     *      */
//     *     fun listenForResponse(responseProcessor: ResponseProcessor<*>) {
//     *         Thread {
//     *
//     *             while (socket.isConnected) {
//     *                 try {
//     *                     val message = _inn.readLine()
//     *                     try {
//     *
//     *                         val response = responseProcessor.process(message)
//     *
//     *                     } catch (e: Exception) {
//     *                         println("Uncaught: ${e.message}")
//     *                     }
//     *                 } catch (e: IOException) {
//     *                     _out.close()
//     *                     _inn.close()
//     *                 }
//     *             }
//     *         }.start()
//     *
//     *     }
//     *
//     *
//     *     private fun tearDownSocket() {
//     *         _out.close()
//     *         _inn.close()
//     *         socket.close()
//     *     }s sent by ClientHandler.sendMessage
//     */
//    fun listenForResponse(responseProcessor: ResponseProcessor<*>) {
//        Thread {
//            while (socket.isConnected) {
//                try {
//                    val message = _inn.readLine()
//                    try {
//
//                        val response = responseProcessor.process(message)
//
//                    } catch (e: Exception) {
//                        println("Uncaught: ${e.message}")
//                    }
//                } catch (e: IOException) {
//                    _out.close()
//                    _inn.close()
//                }
//            }
//        }.start()
//
//    }
//
//
//    private fun tearDownSocket() {
//        _out.close()
//        _inn.close()
//        socket.close()
//    }
//}
