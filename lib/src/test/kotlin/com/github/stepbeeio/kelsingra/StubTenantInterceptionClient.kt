package com.github.stepbeeio.kelsingra

import com.github.stepbeeio.kelsingra.model.TenantInterceptionResponse
import com.github.stepbeeio.kelsingra.servlet.TenantInterceptionClient
import com.github.stepbeeio.kelsingra.tunnel.Request
import com.github.stepbeeio.kelsingra.tunnel.Response

class StubTenantInterceptionClient : TenantInterceptionClient {
    private var interceptionResponse: TenantInterceptionResponse =
        TenantInterceptionResponse(emptyList(), listOf("dev", "prod"))
    private var response: Response? = null

    fun set(response: TenantInterceptionResponse) {
        this.interceptionResponse = response
    }

    fun set(response: Response) {
        this.response = response
    }

    override fun forward(request: Request): Response {
        return response ?: throw IllegalStateException("Cannot use stub interception client when response is null")
    }

    override fun getByService(serviceKey: String): TenantInterceptionResponse {
        return interceptionResponse
    }
}
