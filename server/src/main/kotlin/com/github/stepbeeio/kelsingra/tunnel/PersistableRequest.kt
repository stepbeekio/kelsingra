package com.github.stepbeeio.kelsingra.tunnel

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.time.Instant
import java.util.*

enum class RequestStatus {
    RECEIVED,
    PUBLISHED,
    WAITING,
    RESPONSE_RECEIVED,
    COMPLETED,
    ;
}

@RedisHash("InterceptionDetails")
data class PersistableRequest(
    @Id
    var id: Long?,
    @Indexed
    val requestId: UUID,
    val request: Request,
    var status: RequestStatus,
    var response: Response?,
    val createdAt: Instant,
    @LastModifiedDate
    var updatedAt: Instant,
) {
    companion object {
        fun create(request: Request): PersistableRequest = PersistableRequest(
            null,
            request.id,
            request,
            RequestStatus.RECEIVED,
            null,
            Instant.now(),
            Instant.now()
        )
    }

}
