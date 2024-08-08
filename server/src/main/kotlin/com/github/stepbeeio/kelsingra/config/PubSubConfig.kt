package com.github.stepbeeio.kelsingra.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.stepbeeio.kelsingra.tunnel.PeristableRequestSubscriber
import com.github.stepbeeio.kelsingra.tunnel.RequestSendingClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter


@Configuration
class PubSubConfig(
    private val jedisConnectionFactory: JedisConnectionFactory,
    private val requestSendingClient: RequestSendingClient,
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun messageListener(): MessageListenerAdapter {
        return MessageListenerAdapter(PeristableRequestSubscriber(requestSendingClient, objectMapper))
    }

    @Bean
    fun redisContainer(): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(jedisConnectionFactory)
        container.addMessageListener(messageListener(), topic())
        return container
    }

    @Bean
    fun topic(): ChannelTopic = ChannelTopic("persistable-request")
}
