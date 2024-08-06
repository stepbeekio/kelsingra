package com.github.stepbeeio.kelsingra.tenant

import com.github.stepbeeio.kelsingra.model.CreateInterceptionRequest
import com.github.stepbeeio.kelsingra.model.InterceptionRequest
import com.github.stepbeeio.kelsingra.model.TenantInterceptionResponse
import org.springframework.stereotype.Service


fun CreateInterceptionRequest.toDetails(): PersistedInterceptionDetails = PersistedInterceptionDetails.create(
    sandboxKey,
    serviceKey,
    redirectHost = redirectHost
).also {
    it.addTenant(tenantId)
}


interface InterceptionService {
    fun create(request: CreateInterceptionRequest): TenantInterceptionResponse

    fun update(request: InterceptionRequest): TenantInterceptionResponse?

    fun detailsForService(serviceKey: String): TenantInterceptionResponse
}

@Service
class RedisBackedInterceptionService(private val repository: InterceptionDetailsRepository) : InterceptionService {
    override fun create(request: CreateInterceptionRequest): TenantInterceptionResponse {
        val toCreate = request.toDetails()
        return repository.save(toCreate).toDetailResponse()
    }

    override fun update(request: InterceptionRequest): TenantInterceptionResponse? {
        return repository.findByServiceKeyAndSandboxKey(request.serviceKey, request.sandboxKey)?.let {
            request.execute(it)
            repository.save(it).toDetailResponse()
        }
    }

    override fun detailsForService(serviceKey: String): TenantInterceptionResponse {
        return repository.findByServiceKey(serviceKey)?.toDetailResponse()
            ?: TenantInterceptionResponse(data = emptyList())
    }
}
