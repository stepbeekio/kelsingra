package com.github.stepbeeio.kelsingra.tunnel

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener

class PeristableRequestSubscriber(
    private val client: RequestSendingClient,
    private val objectMapper: ObjectMapper,
) : MessageListener {
    override fun onMessage(message: Message, pattern: ByteArray?) {
        val request: PersistableRequest = objectMapper.readValue(message.toString())

        client.sendRequest(request.request)
    }
}
