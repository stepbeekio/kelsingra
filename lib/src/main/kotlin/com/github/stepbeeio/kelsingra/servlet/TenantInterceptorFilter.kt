package com.github.stepbeeio.kelsingra.servlet

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestClient
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.UriComponentsBuilder

class TenantInterceptorFilter(
    private val headerName: String,
    private val tenantInterceptorService: TenantInterceptorService,
) : OncePerRequestFilter() {
    private val client = RestClient.create()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val tenantId = request.getHeader(headerName)

        if (tenantId != null) {
            when (val result = tenantInterceptorService.shouldIntercept(TenantId(tenantId))) {
                is InterceptionResult.Intercept -> {
                    // NOTE: if this doesn't work as expected then maybe a 307 status is required.
                    intercept(result.details, request, response)
                }

                is InterceptionResult.LocalhostInterception -> tenantInterceptorService.forwardLocalhost(result.details, request, response)
                InterceptionResult.NoOp -> filterChain.doFilter(request, response)
            }
        } else {
            filterChain.doFilter(request, response)
        }
    }


    private fun intercept(details: InterceptionDetails, request: HttpServletRequest, response: HttpServletResponse) {
        val redirectUrl = UriComponentsBuilder.fromHttpUrl(details.uriFromOriginal(request.requestURI)).query(request.queryString)
            .build().toUriString()

        client.method(HttpMethod.valueOf(request.method))
            .uri(redirectUrl)
            .headers { consumer ->
                request.headerNames.asIterator().forEach { headerName ->
                    consumer.set(headerName, request.getHeader(headerName))
                }
            }
            .body(request.reader.readLines().joinToString("\n"))
            .exchange { _, clientResponse ->
                response.status = clientResponse.statusCode.value()
                clientResponse.headers.toList().forEach { (name, values) ->
                    values.forEach { value ->
                        response.addHeader(name, value)
                    }
                }
                response.writer.use { writer ->
                    writer.write(clientResponse.body.readAllBytes().decodeToString())
                }
            }
    }
}
