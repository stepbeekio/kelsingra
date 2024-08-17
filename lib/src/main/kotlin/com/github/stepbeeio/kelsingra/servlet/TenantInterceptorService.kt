package com.github.stepbeeio.kelsingra.servlet

import com.github.stepbeeio.kelsingra.tunnel.Request
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

data class InterceptionDetails(val tenantId: TenantId, val sandboxKey: String, val redirectHost: String, val isLocalHostInterception: Boolean) {
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
    data class LocalhostInterception(val details: InterceptionDetails) : InterceptionResult()
}

interface TenantInterceptorService {
    fun shouldIntercept(tenantId: TenantId): InterceptionResult
    fun shouldProcessKafkaRecord(tenantId: TenantId): Boolean
    fun isMainline(): Boolean

    fun forwardLocalhost(details: InterceptionDetails, request: HttpServletRequest, response: HttpServletResponse)
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
        } else if (details.isLocalHostInterception) {
            InterceptionResult.LocalhostInterception(details)
        } else {
            InterceptionResult.Intercept(details)
        }
    }

    override fun shouldProcessKafkaRecord(tenantId: TenantId): Boolean =
        tenantResults[tenantId] == null

    override fun isMainline(): Boolean = mainline.get()

    override fun forwardLocalhost(
        details: InterceptionDetails,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val req = Request.from(serviceKey, details.sandboxKey, request)

        val res = client.forward(req)

        res.writeTo(response)
    }

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
    override fun forwardLocalhost(
        details: InterceptionDetails,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {}
}

