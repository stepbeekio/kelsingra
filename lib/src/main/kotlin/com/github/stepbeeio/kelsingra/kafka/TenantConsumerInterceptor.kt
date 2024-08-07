package com.github.stepbeeio.kelsingra.kafka

import com.github.stepbeeio.kelsingra.servlet.NoOpInterceptorService
import com.github.stepbeeio.kelsingra.servlet.TenantId
import com.github.stepbeeio.kelsingra.servlet.TenantInterceptorService
import org.apache.kafka.clients.consumer.ConsumerInterceptor
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition

class TenantConsumerInterceptor : ConsumerInterceptor<Any, Any> {
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

    override fun onCommit(offsets: MutableMap<TopicPartition, OffsetAndMetadata>?) {
        // Do nothing
    }

    override fun onConsume(records: ConsumerRecords<Any, Any>): ConsumerRecords<Any, Any> {
        return records
            .filter { record ->
                val header = record.headers().find { it.key() == tenantHeader }
                header != null && interceptorService.shouldProcessKafkaRecord(TenantId(header.value().decodeToString()))
            }
            .groupBy { TopicPartition(it.topic(), it.partition()) }
            .let { ConsumerRecords(it) }
    }
}
