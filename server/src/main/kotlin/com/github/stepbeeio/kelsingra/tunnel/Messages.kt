package com.github.stepbeeio.kelsingra.tunnel

import org.springframework.http.HttpMethod
import java.util.*

data class Request(val id: UUID, val serviceKey: String, val sandboxKey: String, val path: String, val method: String, val body: String,)

data class Response(val statusCode: Int, val body: String,)
