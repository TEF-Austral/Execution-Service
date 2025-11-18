package services

import Language
import dtos.FormatConfigDTO
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class LanguagesFormatterServiceTest {

    private lateinit var mockFormatterService: LanguageFormatterService
    private lateinit var languagesFormatterService: LanguagesFormatterService

    @BeforeEach
    fun setup() {
        mockFormatterService = mockk(relaxed = true)
        languagesFormatterService = LanguagesFormatterService(listOf(mockFormatterService))
    }

    @Test
    fun `format should delegate to correct language service`() {
        val src = ByteArrayInputStream("println(42);".toByteArray())
        val version = "1.0"
        val config = FormatConfigDTO()
        val language = Language.PRINTSCRIPT
        val expectedResult = "println(42);\n"

        every { mockFormatterService.supportsLanguage() } returns "PRINTSCRIPT"
        every {
            mockFormatterService.format(any<InputStream>(), version, config)
        } returns expectedResult

        val result = languagesFormatterService.format(src, version, config, language)

        assertEquals(expectedResult, result)
        verify { mockFormatterService.supportsLanguage() }
        verify { mockFormatterService.format(any<InputStream>(), version, config) }
    }

    @Test
    fun `format should handle multiline code`() {
        val code = "let x = 5;\nprintln(x);"
        val src = ByteArrayInputStream(code.toByteArray())
        val version = "1.0"
        val config = FormatConfigDTO()
        val language = Language.PRINTSCRIPT
        val expectedResult = "let x = 5;\n\nprintln(x);\n"

        every { mockFormatterService.supportsLanguage() } returns "PRINTSCRIPT"
        every {
            mockFormatterService.format(any<InputStream>(), version, config)
        } returns expectedResult

        val result = languagesFormatterService.format(src, version, config, language)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `format should handle empty input`() {
        val src = ByteArrayInputStream("".toByteArray())
        val version = "1.0"
        val config = FormatConfigDTO()
        val language = Language.PRINTSCRIPT
        val expectedResult = ""

        every { mockFormatterService.supportsLanguage() } returns "PRINTSCRIPT"
        every {
            mockFormatterService.format(any<InputStream>(), version, config)
        } returns expectedResult

        val result = languagesFormatterService.format(src, version, config, language)

        assertEquals("", result)
    }

    @Test
    fun `format should throw exception for unsupported language`() {
        val src = ByteArrayInputStream("code".toByteArray())
        val version = "1.0"
        val config = FormatConfigDTO()
        val language = Language.PRINTSCRIPT

        every { mockFormatterService.supportsLanguage() } returns "OTHER_LANGUAGE"

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                languagesFormatterService.format(src, version, config, language)
            }

        assertEquals("Unsupported language: PRINTSCRIPT", exception.message)
    }

    @Test
    fun `format should apply different configs`() {
        val version = "1.0"
        val config1 = FormatConfigDTO()
        val config2 = FormatConfigDTO()
        val language = Language.PRINTSCRIPT

        every { mockFormatterService.supportsLanguage() } returns "PRINTSCRIPT"
        every {
            mockFormatterService.format(any<InputStream>(), version, any())
        } returns "let x=5;\n" andThen "let x = 5;\n"

        val result1 =
            languagesFormatterService.format(
                ByteArrayInputStream("let x=5;".toByteArray()),
                version,
                config1,
                language,
            )
        val result2 =
            languagesFormatterService.format(
                ByteArrayInputStream("let x=5;".toByteArray()),
                version,
                config2,
                language,
            )

        assertEquals("let x=5;\n", result1)
        assertEquals("let x = 5;\n", result2)
        verify(exactly = 2) { mockFormatterService.format(any<InputStream>(), version, any()) }
    }

    @Test
    fun `service should work with multiple formatter services`() {
        val mockService1 = mockk<LanguageFormatterService>(relaxed = true)
        val mockService2 = mockk<LanguageFormatterService>(relaxed = true)
        val service = LanguagesFormatterService(listOf(mockService1, mockService2))

        val src = ByteArrayInputStream("code".toByteArray())
        val version = "1.0"
        val config = FormatConfigDTO()
        val language = Language.PRINTSCRIPT
        val expectedResult = "formatted code"

        every { mockService1.supportsLanguage() } returns "OTHER"
        every { mockService2.supportsLanguage() } returns "PRINTSCRIPT"
        every { mockService2.format(any<InputStream>(), version, config) } returns expectedResult

        val result = service.format(src, version, config, language)

        assertEquals(expectedResult, result)
        verify(exactly = 0) { mockService1.format(any(), any(), any()) }
        verify { mockService2.format(any<InputStream>(), version, config) }
    }

    @Test
    fun `format should handle different versions`() {
        val src1 = ByteArrayInputStream("code".toByteArray())
        val src2 = ByteArrayInputStream("code".toByteArray())
        val version1 = "1.0"
        val version2 = "1.1"
        val config = FormatConfigDTO()
        val language = Language.PRINTSCRIPT

        every { mockFormatterService.supportsLanguage() } returns "PRINTSCRIPT"
        every {
            mockFormatterService.format(any<InputStream>(), version1, config)
        } returns "formatted v1.0"
        every {
            mockFormatterService.format(any<InputStream>(), version2, config)
        } returns "formatted v1.1"

        val result1 = languagesFormatterService.format(src1, version1, config, language)
        val result2 = languagesFormatterService.format(src2, version2, config, language)

        assertEquals("formatted v1.0", result1)
        assertEquals("formatted v1.1", result2)
    }
}
