package io.github.petvat.katan.nio

import io.github.petvat.katan.shared.NioClient

/**
 * Simple client for testing with no processing of request before forwarding.
 */
class TestClient() : NioClient<String>() {
    override fun processRequest(request: String): String {
        return request
    }
}


