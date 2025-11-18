package controllers

import Language
import component.AssetServiceClient
import dtos.LintViolationDTO
import dtos.ValidationResultDTO
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import services.LanguagesAnalyzerService
import java.io.InputStream

class AnalyzerControllerTest {

    private lateinit var languagesAnalyzerService: LanguagesAnalyzerService
    private lateinit var assetServiceClient: AssetServiceClient
    private lateinit var controller: AnalyzerController

    @BeforeEach
    fun setup() {
        languagesAnalyzerService = mockk(relaxed = true)
        assetServiceClient = mockk(relaxed = true)
        controller = AnalyzerController(languagesAnalyzerService, assetServiceClient)
    }

    @Test
    fun `analyzeCode should return valid response when code is valid`() {
        val container = "test-container"
        val key = "test-key"
        val version = "1.0"
        val userId = "user-123"
        val language = Language.PRINTSCRIPT
        val assetContent = "println(42);"

        every { assetServiceClient.getAsset(container, key) } returns assetContent
        every {
            languagesAnalyzerService.analyze(any<InputStream>(), version, userId, language)
        } returns ValidationResultDTO.Valid

        val response = controller.analyzeCode(container, key, version, userId, language)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertTrue(response.body!!.isValid)
        assertTrue(response.body!!.violations.isEmpty())

        verify { assetServiceClient.getAsset(container, key) }
        verify { languagesAnalyzerService.analyze(any<InputStream>(), version, userId, language) }
    }

    @Test
    fun `analyzeCode should return invalid response with violations when code is invalid`() {
        val container = "test-container"
        val key = "test-key"
        val version = "1.0"
        val userId = "user-123"
        val language = Language.PRINTSCRIPT
        val assetContent = "println(42"
        val violations =
            listOf(
                LintViolationDTO("Missing closing parenthesis", 1, 10),
            )

        every { assetServiceClient.getAsset(container, key) } returns assetContent
        every {
            languagesAnalyzerService.analyze(any<InputStream>(), version, userId, language)
        } returns ValidationResultDTO.Invalid(violations)

        val response = controller.analyzeCode(container, key, version, userId, language)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertFalse(response.body!!.isValid)
        assertEquals(1, response.body!!.violations.size)
        assertEquals("Missing closing parenthesis", response.body!!.violations[0].message)

        verify { assetServiceClient.getAsset(container, key) }
        verify { languagesAnalyzerService.analyze(any<InputStream>(), version, userId, language) }
    }

    @Test
    fun `validateContent should return valid response when content is valid`() {
        val content = "println(42);"
        val version = "1.0"
        val language = Language.PRINTSCRIPT

        every {
            languagesAnalyzerService.compile(any<InputStream>(), version, language)
        } returns ValidationResultDTO.Valid

        val response = controller.validateContent(content, version, language)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertTrue(response.body!!.isValid)
        assertTrue(response.body!!.violations.isEmpty())

        verify { languagesAnalyzerService.compile(any<InputStream>(), version, language) }
    }

    @Test
    fun `validateContent should return invalid response with violations when content is invalid`() {
        val content = "println(42"
        val version = "1.0"
        val language = Language.PRINTSCRIPT
        val violations =
            listOf(
                LintViolationDTO("Syntax error", 1, 10),
            )

        every {
            languagesAnalyzerService.compile(any<InputStream>(), version, language)
        } returns ValidationResultDTO.Invalid(violations)

        val response = controller.validateContent(content, version, language)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertFalse(response.body!!.isValid)
        assertEquals(1, response.body!!.violations.size)

        verify { languagesAnalyzerService.compile(any<InputStream>(), version, language) }
    }

    @Test
    fun `compileCode should return valid response when code compiles successfully`() {
        val container = "test-container"
        val key = "test-key"
        val version = "1.0"
        val language = Language.PRINTSCRIPT
        val assetContent = "println(42);"

        every { assetServiceClient.getAsset(container, key) } returns assetContent
        every {
            languagesAnalyzerService.compile(any<InputStream>(), version, language)
        } returns ValidationResultDTO.Valid

        val response = controller.compileCode(container, key, version, language)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertTrue(response.body!!.isValid)

        verify { assetServiceClient.getAsset(container, key) }
        verify { languagesAnalyzerService.compile(any<InputStream>(), version, language) }
    }

    @Test
    fun `compileCode should return invalid response when code has compilation errors`() {
        val container = "test-container"
        val key = "test-key"
        val version = "1.0"
        val language = Language.PRINTSCRIPT
        val assetContent = "invalid code"
        val violations =
            listOf(
                LintViolationDTO("Compilation error", 1, 0),
            )

        every { assetServiceClient.getAsset(container, key) } returns assetContent
        every {
            languagesAnalyzerService.compile(any<InputStream>(), version, language)
        } returns ValidationResultDTO.Invalid(violations)

        val response = controller.compileCode(container, key, version, language)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertFalse(response.body!!.isValid)
        assertEquals(1, response.body!!.violations.size)

        verify { assetServiceClient.getAsset(container, key) }
        verify { languagesAnalyzerService.compile(any<InputStream>(), version, language) }
    }
}
