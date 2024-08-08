package com.github.stepbeeio.kelsingra.tunnel

import org.springframework.data.repository.CrudRepository
import java.util.*

interface PersistableRequestRepository: CrudRepository<PersistableRequest, Long> {
    fun findByRequestId(requestId: UUID): PersistableRequest?
}
