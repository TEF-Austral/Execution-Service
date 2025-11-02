package services

import entities.TestEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import repositories.TestRepository
import java.io.ByteArrayInputStream
import java.util.NoSuchElementException
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ExecutionServiceTest {

    private lateinit var executionService: ExecutionService

    @Mock
    private lateinit var testRepository: TestRepository

    @BeforeEach
    fun setup() {
        executionService = ExecutionService(testRepository)
    }

    @Test
    fun `executeTest should return passed when outputs match expected outputs`() {
        val testEntity =
            TestEntity(
                id = 1L,
                snippetId = 100L,
                name = "Sample Test",
                inputs = listOf("input1", "input2"),
                expectedOutputs = listOf("5"),
            )
        `when`(testRepository.findById(1L)).thenReturn(Optional.of(testEntity))

        val code = "let x: number = 5;\nprintln(x);"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 1L)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(1L, result.testId)
        Assertions.assertEquals(listOf("5"), result.expectedOutputs)
        Assertions.assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `executeTest should return not passed when outputs do not match`() {
        val testEntity =
            TestEntity(
                id = 2L,
                snippetId = 100L,
                name = "Failing Test",
                inputs = listOf("input1"),
                expectedOutputs = listOf("10"),
            )
        `when`(testRepository.findById(2L)).thenReturn(Optional.of(testEntity))

        val code = "let x: number = 5;\nprintln(x);"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 2L)

        Assertions.assertFalse(result.passed)
        Assertions.assertEquals(2L, result.testId)
        Assertions.assertEquals(listOf("10"), result.expectedOutputs)
    }

    @Test
    fun `executeTest should return not passed when there are errors`() {
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
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 3L)

        Assertions.assertFalse(result.passed)
        Assertions.assertFalse(result.errors.isEmpty())
    }

    @Test
    fun `executeTest should throw exception when test not found`() {
        `when`(testRepository.findById(999L)).thenReturn(Optional.empty())

        val code = "let x: number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val exception =
            Assertions.assertThrows(NoSuchElementException::class.java) {
                executionService.executeTest(inputStream, "1.0", 999L)
            }

        Assertions.assertTrue(exception.message?.contains("Test not found") == true)
    }

    @Test
    fun `executeTest should consume inputs in order`() {
        val testEntity =
            TestEntity(
                id = 5L,
                snippetId = 100L,
                name = "Input Test",
                inputs = listOf("Alice", "Bob"),
                expectedOutputs = listOf("name", "Hello, Alice", "name", "Hello, Bob"),
            )
        `when`(testRepository.findById(5L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let name: string = readInput("name");
            println("Hello, " + name);
            let name2: string = readInput("name");
            println("Hello, " + name2);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.1", 5L)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(
            listOf("name", "Hello, Alice", "name", "Hello, Bob"),
            result.outputs,
        )
    }

    @Test
    fun `executeTest should return null for input when inputs exhausted`() {
        val testEntity =
            TestEntity(
                id = 6L,
                snippetId = 100L,
                name = "Exhausted Inputs Test",
                inputs = listOf("First"),
                expectedOutputs = listOf("name", "Hello, First", "name", "Hello, null"),
            )
        `when`(testRepository.findById(6L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let name1: string = readInput("name");
            println("Hello, " + name1);
            let name2: string = readInput("name");
            println("Hello, " + name2);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.1", 6L)

        Assertions.assertTrue(result.passed)
    }

    @Test
    fun `executeInteractive should return success when no errors`() {
        val code = "let x: number = 5;\nprintln(x);"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val inputs = mapOf("name" to "value")

        val result = executionService.execute(inputStream, "1.0", inputs)

        Assertions.assertTrue(result.success)
        Assertions.assertTrue(result.errors.isEmpty())
        Assertions.assertEquals(listOf("5"), result.outputs)
    }

    @Test
    fun `executeInteractive should return errors when execution fails`() {
        val code = "let x: invalid = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val inputs = emptyMap<String, String>()

        val result = executionService.execute(inputStream, "1.0", inputs)

        Assertions.assertFalse(result.success)
        Assertions.assertFalse(result.errors.isEmpty())
    }

    @Test
    fun `executeInteractive should use provided inputs from map`() {
        val code =
            """
            let name: string = readInput("username");
            println("Hello, " + name);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val inputs = mapOf("username" to "Alice")

        val result = executionService.execute(inputStream, "1.1", inputs)

        Assertions.assertTrue(result.success)
        Assertions.assertEquals(listOf("username", "Hello, Alice"), result.outputs)
    }

    @Test
    fun `executeInteractive should handle missing input keys`() {
        val code =
            """
            let name: string = readInput("username");
            println("Value: " + name);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val inputs = mapOf("other" to "value")

        val result = executionService.execute(inputStream, "1.1", inputs)

        Assertions.assertTrue(result.success)
        Assertions.assertEquals(listOf("username", "Value: null"), result.outputs)
    }

    @Test
    fun `executeTest should handle empty inputs list`() {
        val testEntity =
            TestEntity(
                id = 7L,
                snippetId = 100L,
                name = "Empty Inputs Test",
                inputs = emptyList(),
                expectedOutputs = listOf("10"),
            )
        `when`(testRepository.findById(7L)).thenReturn(Optional.of(testEntity))

        val code = "let x: number = 10;\nprintln(x);"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 7L)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(7L, result.testId)
    }

    @Test
    fun `executeTest should handle empty expected outputs`() {
        val testEntity =
            TestEntity(
                id = 8L,
                snippetId = 100L,
                name = "Empty Expected Test",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )
        `when`(testRepository.findById(8L)).thenReturn(Optional.of(testEntity))

        val code = "let x: number = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 8L)

        Assertions.assertTrue(result.passed)
        Assertions.assertTrue(result.outputs.isEmpty())
    }

    @Test
    fun `executeInteractive should handle empty inputs map`() {
        val code = "let x: number = 42;\nprintln(x);"
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val inputs = emptyMap<String, String>()

        val result = executionService.execute(inputStream, "1.0", inputs)

        Assertions.assertTrue(result.success)
        Assertions.assertEquals(listOf("42"), result.outputs)
    }

    @Test
    fun `executeTest should collect multiple outputs`() {
        val testEntity =
            TestEntity(
                id = 9L,
                snippetId = 100L,
                name = "Multiple Outputs Test",
                inputs = emptyList(),
                expectedOutputs = listOf("1", "2", "3"),
            )
        `when`(testRepository.findById(9L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            println(1);
            println(2);
            println(3);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 9L)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(listOf("1", "2", "3"), result.outputs)
    }

    @Test
    fun `executeInteractive should collect multiple outputs`() {
        val code =
            """
            println("a");
            println("b");
            println("c");
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())
        val inputs = emptyMap<String, String>()

        val result = executionService.execute(inputStream, "1.0", inputs)

        Assertions.assertTrue(result.success)
        Assertions.assertEquals(listOf("a", "b", "c"), result.outputs)
    }

    @Test
    fun `executeTest should handle multiple errors`() {
        val testEntity =
            TestEntity(
                id = 10L,
                snippetId = 100L,
                name = "Multiple Errors Test",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )
        `when`(testRepository.findById(10L)).thenReturn(Optional.of(testEntity))

        val code = "let x: invalid = 5;"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 10L)

        Assertions.assertFalse(result.passed)
        Assertions.assertFalse(result.errors.isEmpty())
    }

    @Test
    fun `executeTest should pass when outputs match exactly`() {
        val testEntity =
            TestEntity(
                id = 11L,
                snippetId = 100L,
                name = "Exact Match Test",
                inputs = emptyList(),
                expectedOutputs = listOf("Hello", "World"),
            )
        `when`(testRepository.findById(11L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            println("Hello");
            println("World");
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 11L)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(testEntity.expectedOutputs, result.outputs)
    }

    @Test
    fun `executeTest should fail when output count differs`() {
        val testEntity =
            TestEntity(
                id = 12L,
                snippetId = 100L,
                name = "Count Mismatch Test",
                inputs = emptyList(),
                expectedOutputs = listOf("1", "2"),
            )
        `when`(testRepository.findById(12L)).thenReturn(Optional.of(testEntity))

        val code = "println(1);"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 12L)

        Assertions.assertFalse(result.passed)
        Assertions.assertEquals(1, result.outputs.size)
        Assertions.assertEquals(2, result.expectedOutputs.size)
    }
}
