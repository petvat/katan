package io.github.petvat.katan.client

import io.github.petvat.katan.shared.NioClient
import io.github.petvat.katan.shared.protocol.Request
import io.github.petvat.katan.shared.protocol.Response
import json.KatanJson


class NioKatanClient : NioClient<Request, Response>() {
    override fun processRequest(request: Request): String = KatanJson.toJson(request)
    override fun processResponse(response: String): Response = KatanJson.toResponse(response)
}

