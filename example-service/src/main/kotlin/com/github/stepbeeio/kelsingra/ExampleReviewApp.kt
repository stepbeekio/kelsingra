package com.github.stepbeeio.kelsingra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExampleReviewApp

fun main(args: Array<String>) {
    System.setProperty("server.port", "8082")
    System.setProperty("interceptor.client.sandbox", "review")
    System.setProperty("spring.application.name", "example-review-app")
    runApplication<ExampleReviewApp>(*args)
}
