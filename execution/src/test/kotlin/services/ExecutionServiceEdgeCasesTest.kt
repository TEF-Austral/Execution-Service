package services

import entities.TestEntity
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import repositories.TestRepository
import java.io.ByteArrayInputStream
import java.util.Optional

class ExecutionServiceEdgeCasesTest {

    @Mock
    private lateinit var testRepository: TestRepository

    private lateinit var executionService: PrintScriptExecutionService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        executionService = PrintScriptExecutionService(testRepository)
    }

    @Test
    fun `executeTest should fail when output count differs`() {
        val testEntity =
            TestEntity(
                id = 1L,
                snippetId = 100L,
                name = "Count Mismatch",
                inputs = emptyList(),
                expectedOutputs = listOf("1", "2", "3"),
            )
        `when`(testRepository.findById(1L)).thenReturn(Optional.of(testEntity))

        val code = "println(1);\nprintln(2);"
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.0",
                1L,
            )

        assertFalse(result.passed)
    }

    @Test
    fun `executeTest should pass when outputs match exactly`() {
        val testEntity =
            TestEntity(
                id = 2L,
                snippetId = 100L,
                name = "Exact Match",
                inputs = emptyList(),
                expectedOutputs = listOf("hello", "world"),
            )
        `when`(testRepository.findById(2L)).thenReturn(Optional.of(testEntity))

        val code = "println(\"hello\");\nprintln(\"world\");"
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.0",
                2L,
            )

        assertTrue(result.passed)
    }

    @Test
    fun `executeTest should handle multiple inputs`() {
        val testEntity =
            TestEntity(
                id = 3L,
                snippetId = 100L,
                name = "Multiple Inputs",
                inputs = listOf("Alice", "Bob", "Charlie"),
                expectedOutputs = listOf("Hello Alice", "Hello Bob", "Hello Charlie"),
            )
        `when`(testRepository.findById(3L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let name1: string = readInput("Name 1:");
            println("Hello " + name1);
            let name2: string = readInput("Name 2:");
            println("Hello " + name2);
            let name3: string = readInput("Name 3:");
            println("Hello " + name3);
            """.trimIndent()
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.1",
                3L,
            )

        assertTrue(result.passed)
    }

    @Test
    fun `executeTest should handle missing input gracefully`() {
        val testEntity =
            TestEntity(
                id = 4L,
                snippetId = 100L,
                name = "Missing Input",
                inputs = listOf("first"),
                expectedOutputs = listOf("Hello first", "Hello null"),
            )
        `when`(testRepository.findById(4L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let name1: string = readInput("Name 1:");
            println("Hello " + name1);
            let name2: string = readInput("Name 2:");
            println("Hello " + name2);
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
    fun `executeTest should handle string concatenation`() {
        val testEntity =
            TestEntity(
                id = 5L,
                snippetId = 100L,
                name = "String Concat",
                inputs = emptyList(),
                expectedOutputs = listOf("Hello World"),
            )
        `when`(testRepository.findById(5L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let greeting: string = "Hello";
            let target: string = "World";
            println(greeting + " " + target);
            """.trimIndent()
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.1",
                5L,
            )

        assertTrue(result.passed)
    }

    @Test
    fun `executeTest should handle number operations`() {
        val testEntity =
            TestEntity(
                id = 6L,
                snippetId = 100L,
                name = "Number Operations",
                inputs = emptyList(),
                expectedOutputs = listOf("15", "5", "50", "2"),
            )
        `when`(testRepository.findById(6L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let a: number = 10;
            let b: number = 5;
            println(a + b);
            println(a - b);
            println(a * b);
            println(a / b);
            """.trimIndent()
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.0",
                6L,
            )

        assertTrue(result.passed)
    }

    @Test
    fun `executeTest should catch runtime errors`() {
        val testEntity =
            TestEntity(
                id = 7L,
                snippetId = 100L,
                name = "Runtime Error",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )
        `when`(testRepository.findById(7L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let x: number = 10 / 0;
            """.trimIndent()
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.0",
                7L,
            )

        assertFalse(result.passed)
    }

    @Test
    fun `executeTest should handle empty code`() {
        val testEntity =
            TestEntity(
                id = 8L,
                snippetId = 100L,
                name = "Empty Code",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )
        `when`(testRepository.findById(8L)).thenReturn(Optional.of(testEntity))

        val code = ""
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.0",
                8L,
            )

        assertTrue(result.passed)
    }

    @Test
    fun `executeTest should handle if statement`() {
        val testEntity =
            TestEntity(
                id = 9L,
                snippetId = 100L,
                name = "If Statement",
                inputs = emptyList(),
                expectedOutputs = listOf("Greater"),
            )
        `when`(testRepository.findById(9L)).thenReturn(Optional.of(testEntity))

        val code =
            """
            let x: number = 10;
            if (x > 5) {
                println("Greater");
            }
            """.trimIndent()
        val result =
            executionService.executeTest(
                ByteArrayInputStream(code.toByteArray()),
                "1.1",
                9L,
            )

        assertTrue(result.passed)
    }

    @Test
    fun `executeAllTests should handle mixed pass and fail`() {
        val test1 =
            TestEntity(
                id = 1L,
                snippetId = 100L,
                name = "Pass Test",
                inputs = emptyList(),
                expectedOutputs = listOf("5"),
            )
        val test2 =
            TestEntity(
                id = 2L,
                snippetId = 100L,
                name = "Fail Test",
                inputs = emptyList(),
                expectedOutputs = listOf("10"),
            )

        `when`(testRepository.findBySnippetId(100L)).thenReturn(listOf(test1, test2))
        `when`(testRepository.findById(1L)).thenReturn(Optional.of(test1))
        `when`(testRepository.findById(2L)).thenReturn(Optional.of(test2))

        val code = "let x: number = 5;\nprintln(x);"
        val result =
            executionService.executeAllTests(
                ByteArrayInputStream(code.toByteArray()),
                100L,
                "1.0",
            )

        assertTrue(result.executions[0].passed)
        assertFalse(result.executions[1].passed)
    }
}
