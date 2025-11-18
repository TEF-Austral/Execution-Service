package consumers.handlers

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import requests.FormattingRequestEvent
import kotlin.test.assertNotNull

class IFormattingRequestHandlerTest {

    @Test
    fun `handler interface should be implementable`() {
        val handler =
            object : IFormattingRequestHandler {
                override fun handle(request: FormattingRequestEvent) {
                    // Test implementation
                }
            }
        assertNotNull(handler)
    }

    @Test
    fun `handler should call handle method`() {
        val handler = mockk<IFormattingRequestHandler>(relaxed = true)
        val request =
            FormattingRequestEvent(
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
