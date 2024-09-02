package com.github.stepbeeio.kelsingra.kafka

import com.github.stepbeeio.kelsingra.servlet.NoOpInterceptorService
import com.github.stepbeeio.kelsingra.servlet.TenantInterceptorService
import org.apache.kafka.clients.producer.ProducerInterceptor
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.LoggerFactory

class TenantProducerInterceptor : ProducerInterceptor<Any, Any> {
    private lateinit var tenantHeader: String
    private lateinit var interceptorService: TenantInterceptorService

    override fun configure(configs: MutableMap<String, *>?) {
        tenantHeader = (configs?.get(InterceptorConfig.headerProperty) as? String) ?: InterceptorConfig.defaultHeader
        interceptorService =
            (configs?.get(InterceptorConfig.interceptorService) as? TenantInterceptorService) ?: NoOpInterceptorService
    }

    override fun close() {
        // Do nothing
    }

    override fun onAcknowledgement(metadata: RecordMetadata?, e: Exception?) {
        // Do nothing
    }

    override fun onSend(record: ProducerRecord<Any, Any>): ProducerRecord<Any, Any> {
        val header = interceptorService.getHeader()

        if (header == null) {
            logger.warn("Kafka message sent without tenant header set!")
        } else {
            record.headers().add(tenantHeader, header.encodeToByteArray())
        }

        return record
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TenantProducerInterceptor::class.java)
    }
}
