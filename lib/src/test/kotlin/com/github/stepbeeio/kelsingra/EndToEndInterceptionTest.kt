package com.github.stepbeeio.kelsingra

import com.github.stepbeeio.kelsingra.model.InterceptionDetailResponse
import com.github.stepbeeio.kelsingra.model.TenantInterceptionResponse
import com.github.stepbeeio.kelsingra.servlet.TenantId
import org.apache.catalina.valves.PersistentValve
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class EndToEndInterceptionTest {
    @Autowired
    lateinit var stubTenantInterceptionClient: StubTenantInterceptionClient

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `requests without header aren't intercepted`() {
        mvc.get("/hello")
            .andExpect {
                content {
                    json("""{"data": "Hello World!"}""")
                }
                status {
                    isOk()
                }
            }
    }

    @Test
    fun `requests with header but not configured aren't intercepted`() {
        mvc.get("/hello") {
            header("tenant", "abc")
        }
            .andExpect {
                content {
                    json("""{"data": "Hello World!"}""")
                }
                status {
                    isOk()
                }
            }
    }

    @Test
    fun `requests with header that matches configuration are redirected`() {
        val tenantId = "abc"
        stubTenantInterceptionClient.set(
            TenantInterceptionResponse(
                listOf(
                    InterceptionDetailResponse(TenantId(tenantId), "pr-123", "example.com",)
                ),
                mainlineKeys = listOf("dev", "prod")
            )
        )

        mvc.get("/hello") {
            header("tenant", tenantId)
        }
            .andExpect {
                content {
                    json("""{"data": "Hello World!"}""")
                }
                status {
                    isOk()
                }
            }
    }
}
