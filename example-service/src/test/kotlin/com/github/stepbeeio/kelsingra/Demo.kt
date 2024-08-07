package com.github.stepbeeio.kelsingra

import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.util.*

class Demo {
    private val serverClient = RestClient.create("http://localhost:8080")
    private val devClient = RestClient.create("http://localhost:8081")
    private val reviewClient = RestClient.create("http://localhost:8082")
    private val otherReviewClient = RestClient.create("http://localhost:8083")


    @Test
    fun `run demo`() {
        println("Calling dev example service without tenant id")
        devClient.get().uri("/example?name=Stephen").exchange { _, clientResponse ->
            val body = clientResponse.body.readAllBytes().decodeToString()
            println(body)
        }

        val reviewTenant = UUID.randomUUID().toString()

        println("Creating interception for $reviewTenant pointing to http://localhost:8082")
        serverClient.put().uri("/interception-details").body(creationRequest(reviewTenant)).contentType(MediaType.APPLICATION_JSON).exchange { _, clientResponse ->
            val body = clientResponse.body.readAllBytes().decodeToString()
            println(body)
        }

        println("Refreshing the clients")
        refresh()

        println("Calling dev example service without tenant id (again)")
        devClient.get().uri("/example?name=Stephen").exchange { _, clientResponse ->
            val body = clientResponse.body.readAllBytes().decodeToString()
            println(body)
        }

        println("Calling dev example service with review tenant id")
        devClient.get().uri("/example?name=Stephen").header("x-tenant-id", reviewTenant).exchange { _, clientResponse ->
            val body = clientResponse.body.readAllBytes().decodeToString()
            println(body)
        }

        println("Posting example service without review tenant id")
        devClient.post().uri("/example?name=Stephen").exchange { _, clientResponse ->
            val body = clientResponse.body.readAllBytes().decodeToString()
            println(body)
        }

        println("Posting example service with review tenant id")
        devClient.post().uri("/example?name=Stephen").header("x-tenant-id", reviewTenant).exchange { _, clientResponse ->
            val body = clientResponse.body.readAllBytes().decodeToString()
            println(body)
        }
    }

    private fun refresh() {
        listOf(devClient, reviewClient, otherReviewClient).forEach { client ->
            client.post().uri("/refresh").exchange { _, response ->
                val body = response.body.readAllBytes().decodeToString()
                println(body)
            }
        }
    }


    private fun creationRequest(reviewTenant: String) = """
                {
                        "tenantId": "$reviewTenant",
                        "sandboxKey": "review",
                        "serviceKey": "example",
                        "redirectHost": "http://localhost:8082"
                }
            """.trimIndent()
}
