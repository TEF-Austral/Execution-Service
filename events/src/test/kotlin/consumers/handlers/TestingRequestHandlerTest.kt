package consumers.handlers

import component.AssetService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import producers.TestingResultProducer
import repositories.TestRepository
import requests.TestingRequestEvent
import services.PrintScriptExecutionService
import kotlin.test.assertNotNull

class TestingRequestHandlerTest {

    private lateinit var assetService: AssetService
    private lateinit var testRepository: TestRepository
    private lateinit var executionService: PrintScriptExecutionService
    private lateinit var testingResultProducer: TestingResultProducer
    private lateinit var handler: TestingRequestHandler

    @BeforeEach
    fun setup() {
        assetService = mockk(relaxed = true)
        testRepository = mockk(relaxed = true)
        executionService = mockk(relaxed = true)
        testingResultProducer = mockk(relaxed = true)
        handler =
            TestingRequestHandler(
                assetService = assetService,
                testRepository = testRepository,
                executionService = executionService,
                testingResultProducer = testingResultProducer,
            )
    }

    @Test
    fun `handler should be instantiated`() {
        assertNotNull(handler)
    }

    @Test
    fun `handle should process request with no tests found`() {
        val request =
            TestingRequestEvent(
                requestId = "req-1",
                snippetId = 1L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
            )
        every { assetService.getAsset(any(), any()) } returns "code content"
        every { testRepository.findBySnippetId(1L) } returns emptyList()

        assertDoesNotThrow {
            handler.handle(request)
        }

        verify { assetService.getAsset("container", "key") }
        verify { testRepository.findBySnippetId(1L) }
        verify(exactly = 0) { executionService.executeTest(any(), any(), any()) }
    }

    @Test
    fun `handle should handle fatal error gracefully`() {
        val request =
            TestingRequestEvent(
                requestId = "req-6",
                snippetId = 6L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
            )
        every { assetService.getAsset(any(), any()) } throws
            RuntimeException("Asset service failed")

        assertDoesNotThrow {
            handler.handle(request)
        }

        verify { assetService.getAsset("container", "key") }
        verify(exactly = 0) { testRepository.findBySnippetId(any()) }
        verify(exactly = 0) { executionService.executeTest(any(), any(), any()) }
    }
}
