package com.github.stepbeeio.kelsingra.tenant

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InterceptionDetailsRepository : CrudRepository<PersistedInterceptionDetails, Long> {
    fun findByServiceKey(serviceKey: String): PersistedInterceptionDetails?
    fun findByServiceKeyAndSandboxKey(serviceKey: String, sandboxKey: String): PersistedInterceptionDetails?
}
