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
                inputs = emptyList(),
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
                inputs = emptyList(),
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
                expectedOutputs = listOf("Hello, Alice", "Hello, Bob"),
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

        Assertions.assertEquals(5L, result.testId)
        Assertions.assertEquals(2, result.outputs.size)
        Assertions.assertEquals(listOf("Hello, Alice", "Hello, Bob"), result.expectedOutputs)
    }

    @Test
    fun `executeTest should return null for input when inputs exhausted`() {
        val testEntity =
            TestEntity(
                id = 6L,
                snippetId = 100L,
                name = "Exhausted Inputs Test",
                inputs = listOf("First"),
                expectedOutputs = listOf("Hello, First", "Hello, null"),
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
    fun `executeTest should handle empty code`() {
        val testEntity =
            TestEntity(
                id = 7L,
                snippetId = 100L,
                name = "Empty Code Test",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )
        `when`(testRepository.findById(7L)).thenReturn(Optional.of(testEntity))

        val code = ""
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 7L)

        Assertions.assertTrue(result.passed)
        Assertions.assertTrue(result.outputs.isEmpty())
        Assertions.assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `executeTest should handle multiple println statements`() {
        val testEntity =
            TestEntity(
                id = 8L,
                snippetId = 100L,
                name = "Multiple Println Test",
                inputs = emptyList(),
                expectedOutputs = listOf("Line 1", "Line 2", "Line 3"),
            )
        `when`(testRepository.findById(8L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            println("Line 1");
            println("Line 2");
            println("Line 3");
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 8L)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(3, result.outputs.size)
        Assertions.assertEquals(listOf("Line 1", "Line 2", "Line 3"), result.outputs)
    }

    @Test
    fun `executeTest should handle arithmetic operations`() {
        val testEntity =
            TestEntity(
                id = 9L,
                snippetId = 100L,
                name = "Arithmetic Test",
                inputs = emptyList(),
                expectedOutputs = listOf("15", "5", "50", "2"),
            )
        `when`(testRepository.findById(9L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let a: number = 10;
            let b: number = 5;
            println(a + b);
            println(a - b);
            println(a * b);
            println(a / b);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 9L)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(4, result.outputs.size)
    }

    @Test
    fun `executeTest should handle string concatenation`() {
        val testEntity =
            TestEntity(
                id = 10L,
                snippetId = 100L,
                name = "String Concatenation Test",
                inputs = emptyList(),
                expectedOutputs = listOf("Hello World", "PrintScript 1.0"),
            )
        `when`(testRepository.findById(10L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let greeting: string = "Hello";
            let name: string = "World";
            println(greeting + " " + name);
            println("PrintScript" + " " + "1.0");
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 10L)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(2, result.outputs.size)
    }

    @Test
    fun `executeTest should handle boolean operations`() {
        val testEntity =
            TestEntity(
                id = 11L,
                snippetId = 100L,
                name = "Boolean Test",
                inputs = emptyList(),
                expectedOutputs = listOf("true", "false"),
            )
        `when`(testRepository.findById(11L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let isTrue: boolean = true;
            let isFalse: boolean = false;
            println(isTrue);
            println(isFalse);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.1", 11L)

        println(result.outputs)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(2, result.outputs.size)
    }

    @Test
    fun `executeTest should handle readInput with multiple inputs`() {
        val testEntity =
            TestEntity(
                id = 12L,
                snippetId = 100L,
                name = "Multiple ReadInput Test",
                inputs = listOf("John", "25", "Developer"),
                expectedOutputs = listOf("Name: John", "Age: 25", "Job: Developer"),
            )
        `when`(testRepository.findById(12L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let name: string = readInput("Enter name");
            println("Name: " + name);
            let age: string = readInput("Enter age");
            println("Age: " + age);
            let job: string = readInput("Enter job");
            println("Job: " + job);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.1", 12L)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(3, result.outputs.size)
    }

    @Test
    fun `executeTest should fail when outputs have different count`() {
        val testEntity =
            TestEntity(
                id = 13L,
                snippetId = 100L,
                name = "Different Count Test",
                inputs = emptyList(),
                expectedOutputs = listOf("Output 1", "Output 2"),
            )
        `when`(testRepository.findById(13L)).thenReturn(Optional.of(testEntity))

        val code = "println(\"Output 1\");"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 13L)

        Assertions.assertFalse(result.passed)
        Assertions.assertEquals(1, result.outputs.size)
        Assertions.assertEquals(2, result.expectedOutputs.size)
    }

    @Test
    fun `executeTest should handle syntax errors gracefully`() {
        val testEntity =
            TestEntity(
                id = 14L,
                snippetId = 100L,
                name = "Syntax Error Test",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )
        `when`(testRepository.findById(14L)).thenReturn(Optional.of(testEntity))

        val code = "let x: number = ;"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 14L)

        Assertions.assertFalse(result.passed)
        Assertions.assertFalse(result.errors.isEmpty())
    }

    @Test
    fun `executeTest should handle variable reassignment`() {
        val testEntity =
            TestEntity(
                id = 15L,
                snippetId = 100L,
                name = "Variable Reassignment Test",
                inputs = emptyList(),
                expectedOutputs = listOf("10", "20"),
            )
        `when`(testRepository.findById(15L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let x: number = 10;
            println(x);
            x = 20;
            println(x);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.1", 15L)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(2, result.outputs.size)
    }

    @Test
    fun `executeTest should handle const declarations with version 1_1`() {
        val testEntity =
            TestEntity(
                id = 16L,
                snippetId = 100L,
                name = "Const Declaration Test",
                inputs = emptyList(),
                expectedOutputs = listOf("42"),
            )
        `when`(testRepository.findById(16L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            const x: number = 42;
            println(x);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.1", 16L)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(1, result.outputs.size)
        Assertions.assertEquals(listOf("42"), result.outputs)
    }

    @Test
    fun `executeTest should handle whitespace in expected outputs`() {
        val testEntity =
            TestEntity(
                id = 17L,
                snippetId = 100L,
                name = "Whitespace Test",
                inputs = emptyList(),
                expectedOutputs = listOf("  Hello  "),
            )
        `when`(testRepository.findById(17L)).thenReturn(Optional.of(testEntity))

        val code = "println(\"  Hello  \");"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 17L)

        // This test checks if trimming is happening correctly
        Assertions.assertTrue(result.passed)
    }

    @Test
    fun `executeTest should handle complex expression`() {
        val testEntity =
            TestEntity(
                id = 18L,
                snippetId = 100L,
                name = "Complex Expression Test",
                inputs = emptyList(),
                expectedOutputs = listOf("21"),
            )
        `when`(testRepository.findById(18L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let a: number = 5;
            let b: number = 3;
            let c: number = 2;
            println((a + b) * c + a);
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 18L)

        println(result.outputs)

        Assertions.assertTrue(result.passed)
        Assertions.assertEquals(1, result.outputs.size)
    }

    @Test
    fun `executeTest should verify outputs list is populated correctly`() {
        val testEntity =
            TestEntity(
                id = 19L,
                snippetId = 100L,
                name = "Outputs Verification Test",
                inputs = emptyList(),
                expectedOutputs = listOf("Test Output"),
            )
        `when`(testRepository.findById(19L)).thenReturn(Optional.of(testEntity))

        val code = "println(\"Test Output\");"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 19L)

        // This test specifically checks if outputs are being captured
        Assertions.assertNotNull(result.outputs)
        Assertions.assertFalse(result.outputs.isEmpty(), "Outputs list should not be empty")
        Assertions.assertEquals(1, result.outputs.size, "Should have exactly one output")
        Assertions.assertEquals("Test Output", result.outputs[0])
    }

    @Test
    fun `executeTest should fail when using undefined variable`() {
        val testEntity =
            TestEntity(
                id = 20L,
                snippetId = 100L,
                name = "Undefined Variable Test",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )
        `when`(testRepository.findById(20L)).thenReturn(Optional.of(testEntity))

        val code = "println(undefinedVar);"
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 20L)

        Assertions.assertFalse(result.passed)
        Assertions.assertFalse(result.errors.isEmpty())
    }

    @Test
    fun `executeTest should fail on type mismatch error`() {
        val testEntity =
            TestEntity(
                id = 21L,
                snippetId = 100L,
                name = "Type Mismatch Test",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )
        `when`(testRepository.findById(21L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let x: number = "not a number";
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 21L)

        Assertions.assertFalse(result.passed)
        Assertions.assertFalse(result.errors.isEmpty())
    }

    @Test
    fun `executeTest should fail when trying to reassign const variable`() {
        val testEntity =
            TestEntity(
                id = 22L,
                snippetId = 100L,
                name = "Const Reassignment Test",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )
        `when`(testRepository.findById(22L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            const x: number = 10;
            x = 20;
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.1", 22L)

        Assertions.assertFalse(result.passed)
        Assertions.assertFalse(result.errors.isEmpty())
    }

    @Test
    fun `executeTest should fail with mismatched output content`() {
        val testEntity =
            TestEntity(
                id = 23L,
                snippetId = 100L,
                name = "Output Content Mismatch Test",
                inputs = emptyList(),
                expectedOutputs = listOf("Expected Output", "Another Expected"),
            )
        `when`(testRepository.findById(23L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            println("Wrong Output");
            println("Different Output");
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 23L)

        Assertions.assertFalse(result.passed)
        Assertions.assertEquals(2, result.outputs.size)
        Assertions.assertEquals(2, result.expectedOutputs.size)
        Assertions.assertNotEquals(result.expectedOutputs[0], result.outputs[0])
    }

    @Test
    fun `executeTest should fail when output is missing`() {
        val testEntity =
            TestEntity(
                id = 24L,
                snippetId = 100L,
                name = "Missing Output Test",
                inputs = emptyList(),
                expectedOutputs = listOf("Output 1", "Output 2", "Output 3"),
            )
        `when`(testRepository.findById(24L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            println("Output 1");
            println("Output 2");
            """.trimIndent()
        val inputStream = ByteArrayInputStream(code.toByteArray())

        val result = executionService.executeTest(inputStream, "1.0", 24L)

        Assertions.assertFalse(result.passed)
        Assertions.assertEquals(2, result.outputs.size)
        Assertions.assertEquals(3, result.expectedOutputs.size)
    }
}
