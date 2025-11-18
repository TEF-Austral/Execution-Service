package consumers.handlers

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import requests.TestingRequestEvent
import kotlin.test.assertNotNull

class ITestingRequestHandlerTest {

    @Test
    fun `handler interface should be implementable`() {
        val handler =
            object : ITestingRequestHandler {
                override fun handle(request: TestingRequestEvent) {
                }
            }
        assertNotNull(handler)
    }

    @Test
    fun `handler should call handle method`() {
        val handler = mockk<ITestingRequestHandler>(relaxed = true)
        val request =
            TestingRequestEvent(
                requestId = "req-1",
                snippetId = 1L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
            )

        assertDoesNotThrow {
            handler.handle(request)
        }

        verify { handler.handle(request) }
    }
}
