package com.github.stepbeeio.kelsingra.tunnel


import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Component

@Component
class PeristableRequestPublisher(
    private val channel: ChannelTopic,
    private val operations: RedisOperations<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun send(request: PersistableRequest) {
        operations.convertAndSend(channel.topic, objectMapper.writeValueAsString(request))
    }
}
