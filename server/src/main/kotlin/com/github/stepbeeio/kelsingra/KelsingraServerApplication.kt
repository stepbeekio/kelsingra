package com.github.stepbeeio.kelsingra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KelsingraServerApplication

fun main(args: Array<String>) {
	runApplication<KelsingraServerApplication>(*args)
}
