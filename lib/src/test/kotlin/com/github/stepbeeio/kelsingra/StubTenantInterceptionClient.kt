package com.github.stepbeeio.kelsingra

import com.github.stepbeeio.kelsingra.model.TenantInterceptionResponse
import com.github.stepbeeio.kelsingra.servlet.TenantInterceptionClient

class StubTenantInterceptionClient : TenantInterceptionClient {
    private var response: TenantInterceptionResponse = TenantInterceptionResponse(emptyList())

    fun set(response: TenantInterceptionResponse) {
        this.response = response
    }

    override fun getByService(serviceKey: String): TenantInterceptionResponse {
        return response
    }
}
