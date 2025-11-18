package consumers.handlers

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import requests.LintingRequestEvent
import kotlin.test.assertNotNull

class ILintingRequestHandlerTest {

    @Test
    fun `handler interface should be implementable`() {
        val handler =
            object : ILintingRequestHandler {
                override fun handle(request: LintingRequestEvent) {
                }
            }
        assertNotNull(handler)
    }

    @Test
    fun `handler should call handle method`() {
        val handler = mockk<ILintingRequestHandler>(relaxed = true)
        val request =
            LintingRequestEvent(
                requestId = "req-1",
                snippetId = 1L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
                userId = "user-123",
                languageId = "PRINTSCRIPT",
            )

        assertDoesNotThrow {
            handler.handle(request)
        }

        verify { handler.handle(request) }
    }
}
