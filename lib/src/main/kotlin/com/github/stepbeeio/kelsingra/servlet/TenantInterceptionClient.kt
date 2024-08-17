package com.github.stepbeeio.kelsingra.servlet

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.stepbeeio.kelsingra.model.IInterceptionDetailsController
import com.github.stepbeeio.kelsingra.model.TenantInterceptionResponse
import com.github.stepbeeio.kelsingra.tunnel.Response
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URI


interface TenantInterceptionClient : IInterceptionDetailsController {
    fun forward(request: com.github.stepbeeio.kelsingra.tunnel.Request): Response
}

class HttpTenantInterceptionClient(
    private val baseUrl: String,
    private val objectMapper: ObjectMapper,
) : TenantInterceptionClient {
    private val client = OkHttpClient()
    override fun forward(request: com.github.stepbeeio.kelsingra.tunnel.Request): Response {
        val uri = URI.create("$baseUrl/send-request").toHttpUrlOrNull()
            ?: throw IllegalStateException("Unexpectedly could not create http url")

        val body = objectMapper.writeValueAsString(request).toRequestBody("application/json".toMediaType())

        val req = Request.Builder().url(uri).post(body).build()

        return client.newCall(req).execute().use { r ->
            objectMapper.readValue(r.body?.string()!!)
        }
    }

    override fun getByService(serviceKey: String): TenantInterceptionResponse {
        val uri = URI.create(baseUrl + "/interception-details/services/${serviceKey}.json").toHttpUrlOrNull()
            ?: throw IllegalStateException("Unexpectedly could not create http url")

        val request = Request.Builder().url(uri).get().build();

        return client.newCall(request).execute().use { response ->
            objectMapper.readValue(response.body?.string()!!)
        }
    }
}
