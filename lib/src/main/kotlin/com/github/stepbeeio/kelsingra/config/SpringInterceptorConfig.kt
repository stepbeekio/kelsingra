package com.github.stepbeeio.kelsingra.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.stepbeeio.kelsingra.servlet.HttpTenantInterceptionClient
import com.github.stepbeeio.kelsingra.servlet.InMemoryTenantInterceptor
import com.github.stepbeeio.kelsingra.servlet.TenantInterceptorFilter
import com.github.stepbeeio.kelsingra.servlet.TenantInterceptorService
import io.micrometer.tracing.Tracer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty("interceptor.server.base-url")
class SpringInterceptorConfig(
    private val objectMapper: ObjectMapper,
    @Value("\${interceptor.server.base-url}") private val baseUrl: String,
    @Value("\${interceptor.client.service}") private val serviceKey: String,
    @Value("\${interceptor.client.sandbox}") private val sandboxKey: String,
    @Value("\${interceptor.client.tenant-header}") private val tenantHeader: String,
    private val tracer: Tracer,
) {
    @Bean
    fun client(): HttpTenantInterceptionClient = HttpTenantInterceptionClient(baseUrl, objectMapper)

    @Bean
    fun service(): TenantInterceptorService = InMemoryTenantInterceptor(serviceKey, sandboxKey, client(), tracer)

    @Bean
    fun filter(): TenantInterceptorFilter = TenantInterceptorFilter(tenantHeader, service(), tracer)
}
