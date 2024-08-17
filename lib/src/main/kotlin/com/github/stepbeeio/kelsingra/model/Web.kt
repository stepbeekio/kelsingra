package com.github.stepbeeio.kelsingra.model

import com.github.stepbeeio.kelsingra.servlet.InterceptionDetails
import com.github.stepbeeio.kelsingra.servlet.TenantId

data class TenantInterceptionResponse(val data: List<InterceptionDetailResponse>, val mainlineKeys: List<String>)
data class InterceptionDetailResponse(
    val tenantId: TenantId,
    val sandboxKey: String,
    val redirectHost: String,
    val isLocalhostRedirect: Boolean,
) {
    fun toDetails(): InterceptionDetails = InterceptionDetails(tenantId, sandboxKey = sandboxKey, redirectHost = redirectHost, isLocalhostRedirect)
}
