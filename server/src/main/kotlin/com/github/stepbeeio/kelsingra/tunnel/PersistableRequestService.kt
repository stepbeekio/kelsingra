package com.github.stepbeeio.kelsingra.tunnel

import com.github.stepbeeio.kelsingra.tenant.InterceptionDetailsRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class PersistableRequestService(
    private val publisher: PeristableRequestPublisher,
    private val repository: PersistableRequestRepository,
) {
    private val attempts = 30

    fun send(request: Request): Response {
        val persistable = PersistableRequest.create(request)
        val persisted = repository.save(persistable)
        publisher.send(persisted)

        repeat(attempts) { // Try for 120 seconds
            logger.info("Waiting for response for request with id ${persisted.requestId}")
            Thread.sleep(1000)
            val updated = repository.findByIdOrNull(persisted.id!!)
            val response = updated?.response
            if (response != null) {
                updated.status = RequestStatus.COMPLETED
                repository.save(updated)
                return response
            }
        }

        return Response(503, mapOf(), "NO RESPONSE FROM SANDBOX")
    }

    fun respond(requestId: UUID, payload: Response) {
        val request = repository.findByRequestId(requestId)
            ?: throw IllegalStateException("No request found for identifier $requestId")
        request.response = payload
        repository.save(request)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PersistableRequestService::class.java)
    }
}
