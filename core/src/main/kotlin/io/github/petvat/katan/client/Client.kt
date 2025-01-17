package io.github.petvat.katan.client

import io.github.petvat.katan.shared.NioClient
import io.github.petvat.katan.shared.protocol.Message
import io.github.petvat.katan.shared.protocol.dto.Request
import io.github.petvat.katan.shared.protocol.json.KatanJson


class NioKatanClient : NioClient<Message<Request>>() {
    override fun processRequest(request: Message<Request>): String = KatanJson.messageToJson(request)
}

