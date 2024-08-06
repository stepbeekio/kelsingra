package com.github.stepbeeio.kelsingra.tenant

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class InterceptionDetailsRepositoryTest {
    @Autowired
    lateinit var interceptionDetailsRepository: InterceptionDetailsRepository

    @Test
    fun `can save and retrieve details`() {
        val sandbox = UUID.randomUUID().toString()
        val service = UUID.randomUUID().toString()
        val interceptionDetails = PersistedInterceptionDetails.create(sandbox, service, "http://localhost:9000")
        interceptionDetails.addTenant("TENANT")

        val expected = interceptionDetailsRepository.save(interceptionDetails)

        assertThat(interceptionDetailsRepository.findByServiceKey(service)).isEqualTo(expected)
    }
}
