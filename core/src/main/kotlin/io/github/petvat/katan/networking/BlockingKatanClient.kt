package io.github.petvat.katan.networking

import io.github.petvat.katan.shared.BlockingClient
import io.github.petvat.katan.shared.protocol.InMessage
import io.github.petvat.katan.shared.protocol.OutMessage
import json.KatanJson


class KatanChannel : BlockingClient<InMessage, OutMessage>() {
    override fun processRequest(request: InMessage): String = KatanJson.toJson(request)
    override fun processResponse(response: String): OutMessage = KatanJson.toOutMessage(response)
}

