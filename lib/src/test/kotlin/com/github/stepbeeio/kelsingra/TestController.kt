package com.github.stepbeeio.kelsingra

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

data class TestResult(val data: String)

@RestController
class TestController {
    @GetMapping("/hello")
    fun hello(): TestResult = TestResult(data = "Hello World!")
}
