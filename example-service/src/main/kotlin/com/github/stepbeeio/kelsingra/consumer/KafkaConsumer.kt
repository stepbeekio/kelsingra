package com.github.stepbeeio.kelsingra.consumer

import com.github.stepbeeio.kelsingra.config.KafkaConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumer(
    @Value("\${spring.application.name}")
    private val applicationName: String,
) {
    @KafkaListener(topics = [KafkaConfig.exampleTopic])
    fun listener(data: String?) {
        logger.info("Received message [{}]", data)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KafkaConsumer::class.java)
    }
}
