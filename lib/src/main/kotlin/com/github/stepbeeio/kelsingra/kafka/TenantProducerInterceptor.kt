package com.github.stepbeeio.kelsingra.kafka

import org.apache.kafka.clients.producer.ProducerInterceptor
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.LoggerFactory

class TenantProducerInterceptor : ProducerInterceptor<Any, Any> {
    private lateinit var tenantHeader: String

    override fun configure(configs: MutableMap<String, *>?) {
        tenantHeader = (configs?.get(InterceptorConfig.headerProperty) as? String) ?: InterceptorConfig.defaultHeader
    }

    override fun close() {
        // Do nothing
    }

    override fun onAcknowledgement(metadata: RecordMetadata?, e: Exception?) {
        // Do nothing
    }

    override fun onSend(record: ProducerRecord<Any, Any>): ProducerRecord<Any, Any> {
        val header = record.headers().find { it.key() == tenantHeader }
        if (header == null) {
            logger.warn("Kafka message sent without tenant header set!")
        }

        return record
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TenantProducerInterceptor::class.java)
    }
}
