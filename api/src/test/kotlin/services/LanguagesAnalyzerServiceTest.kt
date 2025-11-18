package services

import Language
import dtos.LintViolationDTO
import dtos.ValidationResultDTO
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class LanguagesAnalyzerServiceTest {

    private lateinit var mockAnalyzerService: LanguageAnalyzerService
    private lateinit var languagesAnalyzerService: LanguagesAnalyzerService

    @BeforeEach
    fun setup() {
        mockAnalyzerService = mockk(relaxed = true)
        languagesAnalyzerService = LanguagesAnalyzerService(listOf(mockAnalyzerService))
    }

    @Test
    fun `compile should delegate to correct language service`() {
        val src = ByteArrayInputStream("println(42);".toByteArray())
        val version = "1.0"
        val language = Language.PRINTSCRIPT
        val expectedResult = ValidationResultDTO.Valid

        every { mockAnalyzerService.supportsLanguage() } returns "PRINTSCRIPT"
        every { mockAnalyzerService.compile(any<InputStream>(), version) } returns expectedResult

        val result = languagesAnalyzerService.compile(src, version, language)

        assertEquals(expectedResult, result)
        verify { mockAnalyzerService.supportsLanguage() }
        verify { mockAnalyzerService.compile(any<InputStream>(), version) }
    }

    @Test
    fun `compile should return invalid result when compilation fails`() {
        val src = ByteArrayInputStream("invalid code".toByteArray())
        val version = "1.0"
        val language = Language.PRINTSCRIPT
        val violations = listOf(LintViolationDTO("Syntax error", 1, 0))
        val expectedResult = ValidationResultDTO.Invalid(violations)

        every { mockAnalyzerService.supportsLanguage() } returns "PRINTSCRIPT"
        every { mockAnalyzerService.compile(any<InputStream>(), version) } returns expectedResult

        val result = languagesAnalyzerService.compile(src, version, language)

        assertEquals(expectedResult, result)
        assertEquals(1, (result as ValidationResultDTO.Invalid).violations.size)
    }

    @Test
    fun `compile should throw exception for unsupported language`() {
        val src = ByteArrayInputStream("code".toByteArray())
        val version = "1.0"
        val language = Language.PRINTSCRIPT

        every { mockAnalyzerService.supportsLanguage() } returns "OTHER_LANGUAGE"

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                languagesAnalyzerService.compile(src, version, language)
            }

        assertEquals("Unsupported language: PRINTSCRIPT", exception.message)
    }

    @Test
    fun `analyze should delegate to correct language service`() {
        val src = ByteArrayInputStream("println(42);".toByteArray())
        val version = "1.0"
        val userId = "user-123"
        val language = Language.PRINTSCRIPT
        val expectedResult = ValidationResultDTO.Valid

        every { mockAnalyzerService.supportsLanguage() } returns "PRINTSCRIPT"
        every {
            mockAnalyzerService.analyze(any<InputStream>(), version, userId)
        } returns expectedResult

        val result = languagesAnalyzerService.analyze(src, version, userId, language)

        assertEquals(expectedResult, result)
        verify { mockAnalyzerService.supportsLanguage() }
        verify { mockAnalyzerService.analyze(any<InputStream>(), version, userId) }
    }

    @Test
    fun `analyze should return invalid result with violations`() {
        val src = ByteArrayInputStream("code with issues".toByteArray())
        val version = "1.0"
        val userId = "user-123"
        val language = Language.PRINTSCRIPT
        val violations =
            listOf(
                LintViolationDTO("Linting error 1", 1, 5),
                LintViolationDTO("Linting error 2", 2, 10),
            )
        val expectedResult = ValidationResultDTO.Invalid(violations)

        every { mockAnalyzerService.supportsLanguage() } returns "PRINTSCRIPT"
        every {
            mockAnalyzerService.analyze(any<InputStream>(), version, userId)
        } returns expectedResult

        val result = languagesAnalyzerService.analyze(src, version, userId, language)

        assertEquals(expectedResult, result)
        assertEquals(2, (result as ValidationResultDTO.Invalid).violations.size)
    }

    @Test
    fun `analyze should throw exception for unsupported language`() {
        val src = ByteArrayInputStream("code".toByteArray())
        val version = "1.0"
        val userId = "user-123"
        val language = Language.PRINTSCRIPT

        every { mockAnalyzerService.supportsLanguage() } returns "OTHER_LANGUAGE"

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                languagesAnalyzerService.analyze(src, version, userId, language)
            }

        assertEquals("Unsupported language: PRINTSCRIPT", exception.message)
    }

    @Test
    fun `service should work with multiple language services`() {
        val mockService1 = mockk<LanguageAnalyzerService>(relaxed = true)
        val mockService2 = mockk<LanguageAnalyzerService>(relaxed = true)
        val service = LanguagesAnalyzerService(listOf(mockService1, mockService2))

        val src = ByteArrayInputStream("code".toByteArray())
        val version = "1.0"
        val language = Language.PRINTSCRIPT

        every { mockService1.supportsLanguage() } returns "OTHER"
        every { mockService2.supportsLanguage() } returns "PRINTSCRIPT"
        every { mockService2.compile(any<InputStream>(), version) } returns
            ValidationResultDTO.Valid

        val result = service.compile(src, version, language)

        assertEquals(ValidationResultDTO.Valid, result)
        verify(exactly = 0) { mockService1.compile(any(), any()) }
        verify { mockService2.compile(any<InputStream>(), version) }
    }
}
