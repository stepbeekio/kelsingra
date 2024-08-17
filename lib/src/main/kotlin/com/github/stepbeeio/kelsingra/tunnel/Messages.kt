package com.github.stepbeeio.kelsingra.tunnel

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.*

data class Request(
    val id: UUID,
    val serviceKey: String,
    val sandboxKey: String,
    val path: String,
    val method: String,
    val body: String,
    val headers: Map<String, String>
) {
    companion object {
        fun from(serviceKey: String, sandboxKey: String, request: HttpServletRequest): Request = Request(
            id = UUID.randomUUID(),
            serviceKey = serviceKey,
            sandboxKey = sandboxKey,
            path = "${request.servletPath}?${request.queryString}",
            method = request.method,
            body = request.inputStream.readAllBytes().decodeToString(),
            headers = request.headerNames.toList().associateWith { request.getHeaders(it).toList().joinToString("") }
        )
    }
}

data class Response(val statusCode: Int, val headers: Map<String, String>, val body: String) {
    fun writeTo(response: HttpServletResponse) {
        response.status = statusCode
        headers.forEach { (key, value) ->
            response.addHeader(key, value)
        }
        response.outputStream.bufferedWriter().use {
            it.write(body)
        }
    }
}
