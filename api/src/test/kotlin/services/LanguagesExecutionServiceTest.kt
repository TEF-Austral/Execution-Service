package services

import Language
import dtos.AllTestSnippetExecution
import dtos.TestExecutionResponseDTO
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class LanguagesExecutionServiceTest {

    private lateinit var mockExecutionService: LanguageExecutionService
    private lateinit var languagesExecutionService: LanguagesExecutionService

    @BeforeEach
    fun setup() {
        mockExecutionService = mockk(relaxed = true)
        languagesExecutionService = LanguagesExecutionService(listOf(mockExecutionService))
    }

    @Test
    fun `executeAllTests should delegate to correct language service`() {
        val inputStream = ByteArrayInputStream("println(42);".toByteArray())
        val snippetId = 1L
        val version = "1.0"
        val language = Language.PRINTSCRIPT
        val expectedResult =
            AllTestSnippetExecution(
                executions =
                    listOf(
                        TestExecutionResponseDTO(1L, true, listOf("42"), listOf("42"), emptyList()),
                    ),
            )

        every { mockExecutionService.supportsLanguage() } returns "PRINTSCRIPT"
        every {
            mockExecutionService.executeAllTests(any<InputStream>(), snippetId, version)
        } returns expectedResult

        val result =
            languagesExecutionService.executeAllTests(
                inputStream,
                snippetId,
                version,
                language,
            )

        assertEquals(expectedResult, result)
        assertEquals(1, result.executions.size)
        verify { mockExecutionService.supportsLanguage() }
        verify { mockExecutionService.executeAllTests(any<InputStream>(), snippetId, version) }
    }

    @Test
    fun `executeAllTests should return failed result when tests fail`() {
        val inputStream = ByteArrayInputStream("code".toByteArray())
        val snippetId = 2L
        val version = "1.0"
        val language = Language.PRINTSCRIPT
        val expectedResult =
            AllTestSnippetExecution(
                executions =
                    listOf(
                        TestExecutionResponseDTO(
                            1L,
                            true,
                            listOf("1"),
                            listOf("1"),
                            emptyList(),
                        ),
                        TestExecutionResponseDTO(
                            2L,
                            false,
                            listOf("2"),
                            listOf("3"),
                            listOf("Mismatch"),
                        ),
                    ),
            )

        every { mockExecutionService.supportsLanguage() } returns "PRINTSCRIPT"
        every {
            mockExecutionService.executeAllTests(any<InputStream>(), snippetId, version)
        } returns expectedResult

        val result =
            languagesExecutionService.executeAllTests(
                inputStream,
                snippetId,
                version,
                language,
            )

        assertEquals(2, result.executions.size)
        assertFalse(result.executions[1].passed)
    }

    @Test
    fun `executeAllTests should throw exception for unsupported language`() {
        val inputStream = ByteArrayInputStream("code".toByteArray())
        val snippetId = 1L
        val version = "1.0"
        val language = Language.PRINTSCRIPT

        every { mockExecutionService.supportsLanguage() } returns "OTHER_LANGUAGE"

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                languagesExecutionService.executeAllTests(inputStream, snippetId, version, language)
            }

        assertEquals("Unsupported language: PRINTSCRIPT", exception.message)
    }

    @Test
    fun `executeTest should delegate to correct language service`() {
        val inputStream = ByteArrayInputStream("println(42);".toByteArray())
        val version = "1.0"
        val testId = 1L
        val language = Language.PRINTSCRIPT
        val expectedResult =
            TestExecutionResponseDTO(
                testId = testId,
                passed = true,
                outputs = listOf("42"),
                expectedOutputs = listOf("42"),
                errors = emptyList(),
            )

        every { mockExecutionService.supportsLanguage() } returns "PRINTSCRIPT"
        every {
            mockExecutionService.executeTest(any<InputStream>(), version, testId)
        } returns expectedResult

        val result = languagesExecutionService.executeTest(inputStream, version, testId, language)

        assertEquals(expectedResult, result)
        assertTrue(result.passed)
        assertEquals(testId, result.testId)
        verify { mockExecutionService.supportsLanguage() }
        verify { mockExecutionService.executeTest(any<InputStream>(), version, testId) }
    }

    @Test
    fun `executeTest should return failed result when test fails`() {
        val inputStream = ByteArrayInputStream("code".toByteArray())
        val version = "1.0"
        val testId = 5L
        val language = Language.PRINTSCRIPT
        val expectedResult =
            TestExecutionResponseDTO(
                testId = testId,
                passed = false,
                outputs = listOf("wrong output"),
                expectedOutputs = listOf("correct output"),
                errors = listOf("Output mismatch"),
            )

        every { mockExecutionService.supportsLanguage() } returns "PRINTSCRIPT"
        every {
            mockExecutionService.executeTest(any<InputStream>(), version, testId)
        } returns expectedResult

        val result = languagesExecutionService.executeTest(inputStream, version, testId, language)

        assertFalse(result.passed)
        assertEquals(listOf("Output mismatch"), result.errors)
    }

    @Test
    fun `executeTest should throw exception for unsupported language`() {
        val inputStream = ByteArrayInputStream("code".toByteArray())
        val version = "1.0"
        val testId = 1L
        val language = Language.PRINTSCRIPT

        every { mockExecutionService.supportsLanguage() } returns "OTHER_LANGUAGE"

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                languagesExecutionService.executeTest(inputStream, version, testId, language)
            }

        assertEquals("Unsupported language: PRINTSCRIPT", exception.message)
    }

    @Test
    fun `service should work with multiple execution services`() {
        val mockService1 = mockk<LanguageExecutionService>(relaxed = true)
        val mockService2 = mockk<LanguageExecutionService>(relaxed = true)
        val service = LanguagesExecutionService(listOf(mockService1, mockService2))

        val inputStream = ByteArrayInputStream("code".toByteArray())
        val version = "1.0"
        val testId = 1L
        val language = Language.PRINTSCRIPT
        val expectedResult =
            TestExecutionResponseDTO(testId, true, emptyList(), emptyList(), emptyList())

        every { mockService1.supportsLanguage() } returns "OTHER"
        every { mockService2.supportsLanguage() } returns "PRINTSCRIPT"
        every { mockService2.executeTest(any<InputStream>(), version, testId) } returns
            expectedResult

        val result = service.executeTest(inputStream, version, testId, language)

        assertEquals(expectedResult, result)
        verify(exactly = 0) { mockService1.executeTest(any(), any(), any()) }
        verify { mockService2.executeTest(any<InputStream>(), version, testId) }
    }

    @Test
    fun `executeAllTests should handle empty test results`() {
        val inputStream = ByteArrayInputStream("code".toByteArray())
        val snippetId = 1L
        val version = "1.0"
        val language = Language.PRINTSCRIPT
        val expectedResult =
            AllTestSnippetExecution(
                executions = emptyList(),
            )

        every { mockExecutionService.supportsLanguage() } returns "PRINTSCRIPT"
        every {
            mockExecutionService.executeAllTests(any<InputStream>(), snippetId, version)
        } returns expectedResult

        val result =
            languagesExecutionService.executeAllTests(
                inputStream,
                snippetId,
                version,
                language,
            )

        assertEquals(0, result.executions.size)
    }
}
