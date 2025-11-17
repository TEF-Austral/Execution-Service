package services

import entities.TestEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import repositories.TestRepository
import java.io.ByteArrayInputStream
import java.util.Optional

class PrintScriptExecutionServiceTest {

    @Mock
    private lateinit var testRepository: TestRepository

    private lateinit var executionService: PrintScriptExecutionService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        executionService = PrintScriptExecutionService(testRepository)
    }

    @Test
    fun `supportsLanguage should return PRINTSCRIPT`() {
        assertEquals("PRINTSCRIPT", executionService.supportsLanguage())
    }

    @Test
    fun `executeTest should pass with matching outputs`() {
        val testEntity =
            TestEntity(
                id = 1L,
                snippetId = 100L,
                name = "Pass Test",
                inputs = emptyList(),
                expectedOutputs = listOf("42"),
            )
        `when`(testRepository.findById(1L)).thenReturn(Optional.of(testEntity))

        val code = "let x: number = 42;\nprintln(x);"
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.0",
                1L,
            )

        assertTrue(result.passed)
        assertEquals(1L, result.testId)
        assertEquals(listOf("42"), result.expectedOutputs)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `executeTest should fail with mismatched outputs`() {
        val testEntity =
            TestEntity(
                id = 2L,
                snippetId = 100L,
                name = "Fail Test",
                inputs = emptyList(),
                expectedOutputs = listOf("100"),
            )
        `when`(testRepository.findById(2L)).thenReturn(Optional.of(testEntity))

        val code = "let x: number = 42;\nprintln(x);"
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.0",
                2L,
            )

        assertFalse(result.passed)
        assertEquals(2L, result.testId)
    }

    @Test
    fun `executeTest should fail with syntax errors`() {
        val testEntity =
            TestEntity(
                id = 3L,
                snippetId = 100L,
                name = "Error Test",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )
        `when`(testRepository.findById(3L)).thenReturn(Optional.of(testEntity))

        val code = "let x: invalid = 5;"
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.0",
                3L,
            )

        assertFalse(result.passed)
        assertFalse(result.errors.isEmpty())
    }

    @Test
    fun `executeTest should throw when test not found`() {
        `when`(testRepository.findById(999L)).thenReturn(Optional.empty())

        val code = "let x: number = 5;"

        assertThrows(NoSuchElementException::class.java) {
            executionService.executeTest(ByteArrayInputStream(code.toByteArray()), "1.0", 999L)
        }
    }

    @Test
    fun `executeTest should handle inputs correctly`() {
        val testEntity =
            TestEntity(
                id = 4L,
                snippetId = 100L,
                name = "Input Test",
                inputs = listOf("John"),
                expectedOutputs = listOf("Hello John"),
            )
        `when`(testRepository.findById(4L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let name: string = readInput("Enter name:");
            println("Hello " + name);
            """.trimIndent()
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.1",
                4L,
            )

        assertTrue(result.passed)
    }

    @Test
    fun `executeAllTests should execute multiple tests`() {
        val test1 =
            TestEntity(
                id = 1L,
                snippetId = 100L,
                name = "Test 1",
                inputs = emptyList(),
                expectedOutputs = listOf("42"),
            )
        val test2 =
            TestEntity(
                id = 2L,
                snippetId = 100L,
                name = "Test 2",
                inputs = emptyList(),
                expectedOutputs = listOf("42"),
            )

        `when`(testRepository.findBySnippetId(100L)).thenReturn(listOf(test1, test2))
        `when`(testRepository.findById(1L)).thenReturn(Optional.of(test1))
        `when`(testRepository.findById(2L)).thenReturn(Optional.of(test2))

        val code = "let x: number = 42;\nprintln(x);"
        val result =
            executionService.executeAllTests(
                ByteArrayInputStream(code.toByteArray()),
                100L,
                "1.0",
            )

        assertEquals(2, result.executions.size)
    }

    @Test
    fun `executeAllTests should handle empty test list`() {
        `when`(testRepository.findBySnippetId(200L)).thenReturn(emptyList())

        val code = "let x: number = 1;"
        val result =
            executionService.executeAllTests(
                ByteArrayInputStream(code.toByteArray()),
                200L,
                "1.0",
            )

        assertEquals(0, result.executions.size)
    }

    @Test
    fun `executeTest should trim outputs before comparison`() {
        val testEntity =
            TestEntity(
                id = 5L,
                snippetId = 100L,
                name = "Trim Test",
                inputs = emptyList(),
                expectedOutputs = listOf("  42  "),
            )
        `when`(testRepository.findById(5L)).thenReturn(Optional.of(testEntity))

        val code = "let x: number = 42;\nprintln(x);"
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.0",
                5L,
            )

        assertTrue(result.passed)
    }

    @Test
    fun `executeTest should work with version 1_1`() {
        val testEntity =
            TestEntity(
                id = 6L,
                snippetId = 100L,
                name = "Version Test",
                inputs = emptyList(),
                expectedOutputs = listOf("5"),
            )
        `when`(testRepository.findById(6L)).thenReturn(Optional.of(testEntity))

        val code = "const x: number = 5;\nprintln(x);"
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.1",
                6L,
            )

        assertTrue(result.passed)
    }
}
