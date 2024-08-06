package com.github.stepbeeio.kelsingra

import com.github.stepbeeio.kelsingra.servlet.InMemoryTenantInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class ExampleResponse(val message: String)

@RestController
class ExampleController(
    @Value("\${spring.application.name}") val applicationName: String,
    private val inMemoryTenantInterceptor: InMemoryTenantInterceptor,
) {
    @GetMapping("/example")
    fun get(@RequestParam name: String): ExampleResponse =
        ExampleResponse("GET from $name! I'm $applicationName!")

    @PostMapping("/example")
    fun post(@RequestParam name: String): ExampleResponse =
        ExampleResponse("POST from $name! I'm $applicationName!")

    @PostMapping("/refresh")
    fun refresh(): ExampleResponse {
        inMemoryTenantInterceptor.refresh()
        return ExampleResponse("Refreshed $applicationName")
    }
}
