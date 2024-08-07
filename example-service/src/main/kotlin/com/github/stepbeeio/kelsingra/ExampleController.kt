package com.github.stepbeeio.kelsingra

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.stepbeeio.kelsingra.kafka.InterceptorConfig
import com.github.stepbeeio.kelsingra.servlet.InMemoryTenantInterceptor
import jakarta.servlet.http.HttpServletRequest
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

data class ExampleResponse(val message: String)

@RestController
class ExampleController(
    @Value("\${spring.application.name}") val applicationName: String,
    private val inMemoryTenantInterceptor: InMemoryTenantInterceptor,
    @Value("\${interceptor.client.tenant-header}") val tenantHeader: String,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val topic: NewTopic,
    private val objectMapper: ObjectMapper,
) {
    @GetMapping("/example")
    fun get(@RequestParam name: String): ExampleResponse =
        ExampleResponse("GET from $name! I'm $applicationName!")

    @PostMapping("/example")
    fun post(@RequestParam name: String, request: HttpServletRequest): ExampleResponse {
        val result = ExampleResponse("POST from $name! I'm $applicationName!")
        val tenantId = request.getHeader(tenantHeader)
        val recordHeaders = if (tenantId != null) {
            listOf(RecordHeader(InterceptorConfig.defaultHeader, tenantId.encodeToByteArray()))
        } else {
            emptyList()
        }
        val record: ProducerRecord<String, String> = ProducerRecord(
            topic.name(),
            null,
            System.currentTimeMillis(),
            UUID.randomUUID().toString(),
            objectMapper.writeValueAsString(result),
            recordHeaders
        )
        kafkaTemplate.send(record)
        return result
    }

    @PostMapping("/refresh")
    fun refresh(): ExampleResponse {
        inMemoryTenantInterceptor.refresh()
        return ExampleResponse("Refreshed $applicationName")
    }
}
