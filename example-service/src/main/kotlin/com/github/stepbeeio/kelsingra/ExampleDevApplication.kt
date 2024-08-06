package com.github.stepbeeio.kelsingra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExampleDevApplication

fun main(args: Array<String>) {
	System.setProperty("server.port", "8081")
	System.setProperty("spring.application.name", "example-dev-service")
	runApplication<ExampleDevApplication>(*args)
}
