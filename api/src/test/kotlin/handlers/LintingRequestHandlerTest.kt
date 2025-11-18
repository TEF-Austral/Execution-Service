package handlers

import component.AssetServiceClient
import dtos.LintViolationDTO
import dtos.ValidationResultDTO
import dtos.responses.LintingResultEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import producers.LintingResultProducer
import requests.LintingRequestEvent
import services.LanguagesAnalyzerService
import java.io.InputStream

class LintingRequestHandlerTest {

    private lateinit var languagesAnalyzerService: LanguagesAnalyzerService
    private lateinit var assetServiceClient: AssetServiceClient
    private lateinit var resultProducer: LintingResultProducer
    private lateinit var handler: LintingRequestHandler

    @BeforeEach
    fun setup() {
        languagesAnalyzerService = mockk(relaxed = true)
        assetServiceClient = mockk(relaxed = true)
        resultProducer = mockk(relaxed = true)
        handler =
            LintingRequestHandler(
                languagesAnalyzerService,
                assetServiceClient,
                resultProducer,
            )
    }

    @Test
    fun `handle should analyze code and emit valid result`() {
        val request =
            LintingRequestEvent(
                requestId = "req-123",
                snippetId = 1L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
                userId = "user-123",
                languageId = "PRINTSCRIPT",
            )
        val content = "println(42);"
        val validationResult = ValidationResultDTO.Valid
        val resultSlot = slot<LintingResultEvent>()

        every { assetServiceClient.getAsset(request.bucketContainer, request.bucketKey) } returns
            content
        every {
            languagesAnalyzerService.analyze(
                any<InputStream>(),
                request.version,
                request.userId,
                Language.PRINTSCRIPT,
            )
        } returns validationResult

        handler.handle(request)

        verify { assetServiceClient.getAsset(request.bucketContainer, request.bucketKey) }
        verify {
            languagesAnalyzerService.analyze(
                any<InputStream>(),
                request.version,
                request.userId,
                Language.PRINTSCRIPT,
            )
        }
        verify { resultProducer.emit(any()) }

        val emittedResult = resultSlot.captured
        assertEquals(request.requestId, emittedResult.requestId)
        assertTrue(emittedResult.isValid)
        assertEquals(0, emittedResult.violations.size)
        assertEquals(request.snippetId, emittedResult.snippetId)
    }

    @Test
    fun `handle should analyze code and emit invalid result with violations`() {
        val request =
            LintingRequestEvent(
                requestId = "req-456",
                snippetId = 2L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
                userId = "user-456",
                languageId = "PRINTSCRIPT",
            )
        val content = "invalid code"
        val violations =
            listOf(
                LintViolationDTO("Error 1", 1, 5),
                LintViolationDTO("Error 2", 2, 10),
            )
        val validationResult = ValidationResultDTO.Invalid(violations)
        val resultSlot = slot<LintingResultEvent>()

        every { assetServiceClient.getAsset(request.bucketContainer, request.bucketKey) } returns
            content
        every {
            languagesAnalyzerService.analyze(
                any<InputStream>(),
                request.version,
                request.userId,
                Language.PRINTSCRIPT,
            )
        } returns validationResult

        handler.handle(request)

        verify { resultProducer.emit(any()) }

        val emittedResult = resultSlot.captured
        assertEquals(request.requestId, emittedResult.requestId)
        assertFalse(emittedResult.isValid)
        assertEquals(2, emittedResult.violations.size)
        assertEquals("Error 1", emittedResult.violations[0].message)
        assertEquals(1, emittedResult.violations[0].line)
        assertEquals(5, emittedResult.violations[0].column)
        assertEquals(request.snippetId, emittedResult.snippetId)
    }

    @Test
    fun `handle should emit invalid result when asset service fails`() {
        val request =
            LintingRequestEvent(
                requestId = "req-789",
                snippetId = 3L,
                bucketContainer = "container",
                bucketKey = "invalid-key",
                version = "1.0",
                userId = "user-789",
                languageId = "PRINTSCRIPT",
            )
        val resultSlot = slot<LintingResultEvent>()

        every { assetServiceClient.getAsset(request.bucketContainer, request.bucketKey) } throws
            Exception("Asset not found")

        handler.handle(request)

        verify { resultProducer.emit(any()) }
        val emittedResult = resultSlot.captured
        assertFalse(emittedResult.isValid)
        assertEquals(0, emittedResult.violations.size)
    }

    @Test
    fun `handle should emit invalid result when analyzer service fails`() {
        val request =
            LintingRequestEvent(
                requestId = "req-101",
                snippetId = 4L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
                userId = "user-101",
                languageId = "PRINTSCRIPT",
            )
        val content = "code"
        val resultSlot = slot<LintingResultEvent>()

        every { assetServiceClient.getAsset(request.bucketContainer, request.bucketKey) } returns
            content
        every {
            languagesAnalyzerService.analyze(
                any<InputStream>(),
                request.version,
                request.userId,
                Language.PRINTSCRIPT,
            )
        } throws Exception("Analysis error")

        handler.handle(request)

        verify { resultProducer.emit(any()) }
        val emittedResult = resultSlot.captured
        assertFalse(emittedResult.isValid)
    }

    @Test
    fun `handle should handle empty content`() {
        val request =
            LintingRequestEvent(
                requestId = "req-202",
                snippetId = 5L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
                userId = "user-202",
                languageId = "PRINTSCRIPT",
            )
        val content = ""
        val validationResult = ValidationResultDTO.Valid
        val resultSlot = slot<LintingResultEvent>()

        every { assetServiceClient.getAsset(request.bucketContainer, request.bucketKey) } returns
            content
        every {
            languagesAnalyzerService.analyze(
                any<InputStream>(),
                request.version,
                request.userId,
                Language.PRINTSCRIPT,
            )
        } returns validationResult

        handler.handle(request)

        val emittedResult = resultSlot.captured
        assertTrue(emittedResult.isValid)
        assertEquals(0, emittedResult.violations.size)
    }

    @Test
    fun `handle should map all violation properties correctly`() {
        val request =
            LintingRequestEvent(
                requestId = "req-303",
                snippetId = 6L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
                userId = "user-303",
                languageId = "PRINTSCRIPT",
            )
        val content = "code"
        val violations =
            listOf(
                LintViolationDTO("Message 1", 1, 2),
                LintViolationDTO("Message 2", 3, 4),
                LintViolationDTO("Message 3", 5, 6),
            )
        val validationResult = ValidationResultDTO.Invalid(violations)
        val resultSlot = slot<LintingResultEvent>()

        every { assetServiceClient.getAsset(request.bucketContainer, request.bucketKey) } returns
            content
        every {
            languagesAnalyzerService.analyze(
                any<InputStream>(),
                request.version,
                request.userId,
                Language.PRINTSCRIPT,
            )
        } returns validationResult

        handler.handle(request)

        val emittedResult = resultSlot.captured
        assertEquals(3, emittedResult.violations.size)
        assertEquals("Message 1", emittedResult.violations[0].message)
        assertEquals(1, emittedResult.violations[0].line)
        assertEquals(2, emittedResult.violations[0].column)
        assertEquals("Message 3", emittedResult.violations[2].message)
        assertEquals(5, emittedResult.violations[2].line)
        assertEquals(6, emittedResult.violations[2].column)
    }
}
