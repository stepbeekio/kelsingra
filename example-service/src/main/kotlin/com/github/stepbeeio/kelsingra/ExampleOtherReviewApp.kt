package com.github.stepbeeio.kelsingra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExampleOtherReviewApp

fun main(args: Array<String>) {
    System.setProperty("server.port", "8083")
    System.setProperty("interceptor.client.sandbox", "other-review")
    System.setProperty("spring.application.name", "example-other-review-app")
    System.setProperty("spring.kafka.consumer.group-id", "other-review-group")
    runApplication<ExampleOtherReviewApp>(*args)
}
