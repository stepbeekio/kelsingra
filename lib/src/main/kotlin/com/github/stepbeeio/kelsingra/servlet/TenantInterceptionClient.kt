package com.github.stepbeeio.kelsingra.servlet

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.stepbeeio.kelsingra.model.IInterceptionDetailsController
import com.github.stepbeeio.kelsingra.model.TenantInterceptionResponse
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI


interface TenantInterceptionClient : IInterceptionDetailsController

class HttpTenantInterceptionClient(
    private val baseUrl: String,
    private val objectMapper: ObjectMapper,
) : TenantInterceptionClient {
    private val client = OkHttpClient()

    override fun getByService(serviceKey: String): TenantInterceptionResponse {
        val uri = URI.create(baseUrl + "/interception-details/services/${serviceKey}.json").toHttpUrlOrNull()
            ?: throw IllegalStateException("Unexpectedly could not create http url")

        val request = Request.Builder().url(uri).get().build();

        return client.newCall(request).execute().use { response ->
            objectMapper.readValue(response.body?.string()!!)
        }
    }
}
