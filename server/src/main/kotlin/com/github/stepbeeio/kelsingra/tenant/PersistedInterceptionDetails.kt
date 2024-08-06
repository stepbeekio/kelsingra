package com.github.stepbeeio.kelsingra.tenant

import com.github.stepbeeio.kelsingra.model.IPersistedInterceptionDetails
import com.github.stepbeeio.kelsingra.model.InterceptionDetailResponse
import com.github.stepbeeio.kelsingra.model.TenantInterceptionResponse
import com.github.stepbeeio.kelsingra.servlet.TenantId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.time.Instant


@RedisHash("InterceptionDetails")
data class PersistedInterceptionDetails(
    @Id
    var id: Long?,
    @Indexed
    override val sandboxKey: String,
    @Indexed
    override val serviceKey: String,
    val redirectHost: String,
    val tenants: MutableSet<String> = mutableSetOf(),
    val createdAt: Instant,
    @LastModifiedDate
    val updatedAt: Instant,
    @Version
    val version: Int,
) : IPersistedInterceptionDetails {
    override fun addTenant(tenantId: String) {
        if (!tenants.contains(tenantId)) {
            tenants.add(tenantId)
        }
    }

    override fun removeTenant(tenantId: String) {
        tenants.remove(tenantId)
    }

    fun toDetailResponse(): TenantInterceptionResponse = TenantInterceptionResponse(
        data = tenants.map {
            InterceptionDetailResponse(
                TenantId(it),
                sandboxKey,
                redirectHost = redirectHost
            )
        }
    )

    companion object {
        fun create(sandboxKey: String, serviceKey: String, redirectHost: String): PersistedInterceptionDetails =
            PersistedInterceptionDetails(
                id = 0,
                sandboxKey = sandboxKey,
                serviceKey = serviceKey,
                redirectHost = redirectHost,
                tenants = mutableSetOf(),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                version = 1
            )

    }
}
