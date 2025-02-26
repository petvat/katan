package io.github.petvat.katan.client

import io.github.petvat.katan.shared.NioClient
import io.github.petvat.katan.shared.protocol.Request
import json.KatanJson


class NioKatanClient : NioClient<Request>() {
    override fun processRequest(request: Request): String = KatanJson.toJson(request)
}

