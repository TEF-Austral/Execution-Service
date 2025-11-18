package handlers

import Language
import component.AssetServiceClient
import dtos.FormatConfigDTO
import dtos.FormatterRuleDTO
import dtos.responses.FormattingResultEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import producers.FormattingResultProducer
import requests.FormattingRequestEvent
import services.FormatterConfigService
import services.LanguagesFormatterService
import java.io.InputStream

class FormattingRequestHandlerTest {

    private lateinit var languagesFormatterService: LanguagesFormatterService
    private lateinit var formatterConfigService: FormatterConfigService
    private lateinit var assetServiceClient: AssetServiceClient
    private lateinit var resultProducer: FormattingResultProducer
    private lateinit var handler: FormattingRequestHandler

    @BeforeEach
    fun setup() {
        languagesFormatterService = mockk(relaxed = true)
        formatterConfigService = mockk(relaxed = true)
        assetServiceClient = mockk(relaxed = true)
        resultProducer = mockk(relaxed = true)
        handler =
            FormattingRequestHandler(
                languagesFormatterService,
                formatterConfigService,
                assetServiceClient,
                resultProducer,
            )
    }

    @Test
    fun `handle should format code and emit success result`() {
        val request =
            FormattingRequestEvent(
                requestId = "req-123",
                snippetId = 1L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
                userId = "user-123",
                languageId = "PRINTSCRIPT",
            )
        val content = "println(42);"
        val formattedContent = "println(42);\n"
        val rules = listOf(FormatterRuleDTO("id1", "rule1", true))
        val config = FormatConfigDTO()
        val resultSlot = slot<FormattingResultEvent>()

        every { assetServiceClient.getAsset(request.bucketContainer, request.bucketKey) } returns
            content
        every { formatterConfigService.getConfig(request.userId) } returns rules
        every { formatterConfigService.rulesToConfigDTO(rules) } returns config
        every {
            languagesFormatterService.format(
                any<InputStream>(),
                request.version,
                config,
                Language.PRINTSCRIPT,
            )
        } returns formattedContent

        handler.handle(request)

        verify { assetServiceClient.getAsset(request.bucketContainer, request.bucketKey) }
        verify { formatterConfigService.getConfig(request.userId) }
        verify { formatterConfigService.rulesToConfigDTO(rules) }
        verify {
            languagesFormatterService.format(
                any<InputStream>(),
                request.version,
                config,
                Language.PRINTSCRIPT,
            )
        }
        verify { resultProducer.emit(any()) }

        val emittedResult = resultSlot.captured
        assertEquals(request.requestId, emittedResult.requestId)
        assertTrue(emittedResult.success)
        assertEquals(formattedContent, emittedResult.formattedContent)
        assertNull(emittedResult.error)
        assertEquals(request.snippetId, emittedResult.snippetId)
    }

    @Test
    fun `handle should emit error result when formatting fails`() {
        val request =
            FormattingRequestEvent(
                requestId = "req-456",
                snippetId = 2L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
                userId = "user-456",
                languageId = "PRINTSCRIPT",
            )
        val errorMessage = "Formatting error occurred"
        val resultSlot = slot<FormattingResultEvent>()

        every { assetServiceClient.getAsset(request.bucketContainer, request.bucketKey) } throws
            RuntimeException(errorMessage)

        handler.handle(request)

        verify { assetServiceClient.getAsset(request.bucketContainer, request.bucketKey) }
        verify { resultProducer.emit(any()) }

        val emittedResult = resultSlot.captured
        assertEquals(request.requestId, emittedResult.requestId)
        assertFalse(emittedResult.success)
        assertNull(emittedResult.formattedContent)
        assertNotNull(emittedResult.error)
        assertEquals(request.snippetId, emittedResult.snippetId)
    }

    @Test
    fun `handle should handle empty content`() {
        val request =
            FormattingRequestEvent(
                requestId = "req-303",
                snippetId = 6L,
                bucketContainer = "container",
                bucketKey = "key",
                version = "1.0",
                userId = "user-303",
                languageId = "PRINTSCRIPT",
            )
        val content = ""
        val formattedContent = ""
        val rules = emptyList<FormatterRuleDTO>()
        val config = FormatConfigDTO()
        val resultSlot = slot<FormattingResultEvent>()

        every { assetServiceClient.getAsset(request.bucketContainer, request.bucketKey) } returns
            content
        every { formatterConfigService.getConfig(request.userId) } returns rules
        every { formatterConfigService.rulesToConfigDTO(rules) } returns config
        every {
            languagesFormatterService.format(
                any<InputStream>(),
                request.version,
                config,
                Language.PRINTSCRIPT,
            )
        } returns formattedContent

        handler.handle(request)

        val emittedResult = resultSlot.captured
        assertTrue(emittedResult.success)
        assertEquals("", emittedResult.formattedContent)
    }
}
