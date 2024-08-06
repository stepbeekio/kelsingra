package com.github.stepbeeio.kelsingra

import com.github.stepbeeio.kelsingra.servlet.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfiguration {
    @Bean
    fun client(): StubTenantInterceptionClient = StubTenantInterceptionClient()

    @Bean
    fun service(): TenantInterceptorService = InMemoryTenantInterceptor("test-service", "test-sandbox", client())

    @Bean
    fun filter(): TenantInterceptorFilter = TenantInterceptorFilter("X-tentant", service())
}
