package controllers

import Language
import component.AssetServiceClient
import dtos.FormatConfigDTO
import dtos.FormatterRuleDTO
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import services.FormatterConfigService
import services.LanguagesFormatterService
import java.io.InputStream

class FormatterControllerTest {

    private lateinit var languagesFormatterService: LanguagesFormatterService
    private lateinit var formatterConfigService: FormatterConfigService
    private lateinit var assetServiceClient: AssetServiceClient
    private lateinit var controller: FormatterController

    @BeforeEach
    fun setup() {
        languagesFormatterService = mockk(relaxed = true)
        formatterConfigService = mockk(relaxed = true)
        assetServiceClient = mockk(relaxed = true)
        controller =
            FormatterController(
                languagesFormatterService,
                formatterConfigService,
                assetServiceClient,
            )
    }

    @Test
    fun `formatCode should format code and save to asset service`() {
        val container = "test-container"
        val key = "test-key"
        val version = "1.0"
        val userId = "user-123"
        val language = Language.PRINTSCRIPT
        val assetContent = "println(42);"
        val formattedContent = "println(42);\n"
        val rules = listOf(FormatterRuleDTO("id1", "rule1", true))
        val config = FormatConfigDTO()

        every { assetServiceClient.getAsset(container, key) } returns assetContent
        every { formatterConfigService.getConfig(userId) } returns rules
        every { formatterConfigService.rulesToConfigDTO(rules) } returns config
        every {
            languagesFormatterService.format(any<InputStream>(), version, config, language)
        } returns formattedContent
        every {
            assetServiceClient.createOrUpdateAsset(container, key, formattedContent)
        } returns Unit

        val response = controller.formatCode(container, key, version, userId, language)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(formattedContent, response.body)

        verify { assetServiceClient.getAsset(container, key) }
        verify { formatterConfigService.getConfig(userId) }
        verify { formatterConfigService.rulesToConfigDTO(rules) }
        verify { languagesFormatterService.format(any<InputStream>(), version, config, language) }
        verify { assetServiceClient.createOrUpdateAsset(container, key, formattedContent) }
    }

    @Test
    fun `formatCode should handle empty content`() {
        val container = "test-container"
        val key = "test-key"
        val version = "1.0"
        val userId = "user-123"
        val language = Language.PRINTSCRIPT
        val assetContent = ""
        val formattedContent = ""
        val rules = listOf<FormatterRuleDTO>()
        val config = FormatConfigDTO()

        every { assetServiceClient.getAsset(container, key) } returns assetContent
        every { formatterConfigService.getConfig(userId) } returns rules
        every { formatterConfigService.rulesToConfigDTO(rules) } returns config
        every {
            languagesFormatterService.format(any<InputStream>(), version, config, language)
        } returns formattedContent

        val response = controller.formatCode(container, key, version, userId, language)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("", response.body)

        verify { assetServiceClient.getAsset(container, key) }
    }

    @Test
    fun `previewFormat should format code without saving`() {
        val container = "test-container"
        val key = "test-key"
        val version = "1.0"
        val userId = "user-123"
        val language = Language.PRINTSCRIPT
        val assetContent = "println(42);"
        val formattedContent = "println(42);\n"
        val rules = listOf(FormatterRuleDTO("id1", "rule1", true))
        val config = FormatConfigDTO()

        every { assetServiceClient.getAsset(container, key) } returns assetContent
        every { formatterConfigService.getConfig(userId) } returns rules
        every { formatterConfigService.rulesToConfigDTO(rules) } returns config
        every {
            languagesFormatterService.format(any<InputStream>(), version, config, language)
        } returns formattedContent

        val response = controller.previewFormat(container, key, version, userId, language)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(formattedContent, response.body)

        verify { assetServiceClient.getAsset(container, key) }
        verify { formatterConfigService.getConfig(userId) }
        verify { formatterConfigService.rulesToConfigDTO(rules) }
        verify { languagesFormatterService.format(any<InputStream>(), version, config, language) }
        verify(exactly = 0) { assetServiceClient.createOrUpdateAsset(any(), any(), any()) }
    }

    @Test
    fun `previewFormat should handle different languages`() {
        val container = "test-container"
        val key = "test-key"
        val version = "1.1"
        val userId = "user-456"
        val language = Language.PRINTSCRIPT
        val assetContent = "let x = 5;"
        val formattedContent = "let x = 5;\n"
        val rules = listOf(FormatterRuleDTO("id1", "rule1", true))
        val config = FormatConfigDTO()

        every { assetServiceClient.getAsset(container, key) } returns assetContent
        every { formatterConfigService.getConfig(userId) } returns rules
        every { formatterConfigService.rulesToConfigDTO(rules) } returns config
        every {
            languagesFormatterService.format(any<InputStream>(), version, config, language)
        } returns formattedContent

        val response = controller.previewFormat(container, key, version, userId, language)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(formattedContent, response.body)

        verify { languagesFormatterService.format(any<InputStream>(), version, config, language) }
    }
}
