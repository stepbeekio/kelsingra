package com.github.stepbeeio.kelsingra.servlet

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InterceptionDetailsTest {
    @Test
    fun `replaces host as expected`() {
        val details = InterceptionDetails(TenantId("ABC"), "sandbox", "https://somenewurl.com", false)

        val original = "http://example.com/my/path?query=param&other=otherparam"

        val result = details.uriFromOriginal(original)

        assertEquals("https://somenewurl.com/my/path?query=param&other=otherparam", result)
    }
}
