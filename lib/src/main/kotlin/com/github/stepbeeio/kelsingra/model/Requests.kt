package com.github.stepbeeio.kelsingra.model


interface IPersistedInterceptionDetails {
    val serviceKey: String
    val sandboxKey: String

    fun addTenant(tenantId: String)
    fun removeTenant(tenantId: String)
}

interface InterceptionRequest {
    val tenantId: String
    val sandboxKey: String
    val serviceKey: String

    fun execute(details: IPersistedInterceptionDetails)
}

data class CreateInterceptionRequest(
    val tenantId: String,
    val sandboxKey: String,
    val serviceKey: String,
    val redirectHost: String,
)

data class AddTenantRequest(
    override val tenantId: String,
    override val sandboxKey: String,
    override val serviceKey: String,
): InterceptionRequest {
    override fun execute(details: IPersistedInterceptionDetails) {
        require(details.sandboxKey == sandboxKey && details.serviceKey == serviceKey) {
            "Attempting to apply request $this to incorrect details with key $details"
        }
        details.addTenant(tenantId)
    }
}

data class RemoveTenantRequest(
    override val tenantId: String,
    override val sandboxKey: String,
    override val serviceKey: String,
): InterceptionRequest {
    override fun execute(details: IPersistedInterceptionDetails) {
        require(details.sandboxKey == sandboxKey && details.serviceKey == serviceKey) {
            "Attempting to apply request $this to incorrect details with key $details"
        }
        details.removeTenant(tenantId)
    }
}
