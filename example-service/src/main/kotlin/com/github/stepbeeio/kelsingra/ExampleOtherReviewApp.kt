package com.github.stepbeeio.kelsingra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExampleOtherReviewApp

fun main(args: Array<String>) {
    System.setProperty("server.port", "8083")
    System.setProperty("interceptor.client.sandbox", "review")
    System.setProperty("spring.application.name", "example-other-review-app")
    runApplication<ExampleOtherReviewApp>(*args)
}
