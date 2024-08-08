package com.github.stepbeeio.kelsingra.servlet

import jakarta.annotation.PostConstruct
import org.apache.kafka.common.protocol.types.Field.Bool
import org.slf4j.LoggerFactory
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

data class InterceptionDetails(val tenantId: TenantId, val redirectHost: String) {
    fun uriFromOriginal(requestURI: String): String {
        val original = URI.create(requestURI)
        val updated = UriComponentsBuilder.fromHttpUrl(redirectHost)
        return updated.path(original.path).query(original.query).toUriString()
    }
}

data class TenantId(val value: String)

sealed class InterceptionResult {
    data object NoOp : InterceptionResult()
    data class Intercept(val details: InterceptionDetails) : InterceptionResult()
}

interface TenantInterceptorService {
    fun shouldIntercept(tenantId: TenantId): InterceptionResult
    fun shouldProcessKafkaRecord(tenantId: TenantId): Boolean
    fun isMainline(): Boolean
}

interface Refreshable {
    fun refresh()
}

class InMemoryTenantInterceptor(
    private val serviceKey: String,
    private val sandboxKey: String,
    private val client: TenantInterceptionClient,
) : TenantInterceptorService, Refreshable {
    private val tenantResults: MutableMap<TenantId, InterceptionDetails> = ConcurrentHashMap()
    private var mainline: AtomicBoolean = AtomicBoolean(false)

    @PostConstruct
    fun init() {
        logger.info("Starting up the interceptor for service [$serviceKey] and sandbox [$sandboxKey]")
    }

    override fun shouldIntercept(tenantId: TenantId): InterceptionResult {
        val details = tenantResults[tenantId]
        return if (details == null) {
            InterceptionResult.NoOp
        } else {
            InterceptionResult.Intercept(details)
        }
    }

    override fun shouldProcessKafkaRecord(tenantId: TenantId): Boolean =
        tenantResults[tenantId] == null

    override fun isMainline(): Boolean = mainline.get()

    override fun refresh() {
        val results = client.getByService(serviceKey)
        tenantResults.clear()

        mainline.set(results.mainlineKeys.contains(sandboxKey))

        results.data
            .filter { it.sandboxKey != sandboxKey }
            .forEach { tenantResults[it.tenantId] = it.toDetails() }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(InMemoryTenantInterceptor::class.java)
    }
}

object NoOpInterceptorService : TenantInterceptorService {
    override fun shouldIntercept(tenantId: TenantId): InterceptionResult = InterceptionResult.NoOp

    override fun shouldProcessKafkaRecord(tenantId: TenantId): Boolean = true

    override fun isMainline(): Boolean = true
}

