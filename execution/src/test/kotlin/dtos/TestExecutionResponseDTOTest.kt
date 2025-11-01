package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertNotEquals

class TestExecutionResponseDTOTest {

    @Test
    fun `should create DTO with all properties`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = true,
                outputs = listOf("output1", "output2"),
                expectedOutputs = listOf("output1", "output2"),
                errors = emptyList(),
            )

        assertEquals(1L, dto.testId)
        assertTrue(dto.passed)
        assertEquals(listOf("output1", "output2"), dto.outputs)
        assertEquals(listOf("output1", "output2"), dto.expectedOutputs)
        assertTrue(dto.errors.isEmpty())
    }

    @Test
    fun `should create passing DTO with matching outputs`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 5L,
                passed = true,
                outputs = listOf("result"),
                expectedOutputs = listOf("result"),
                errors = emptyList(),
            )

        assertTrue(dto.passed)
        assertEquals(dto.outputs, dto.expectedOutputs)
        assertTrue(dto.errors.isEmpty())
    }

    @Test
    fun `should create failing DTO with non-matching outputs`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 10L,
                passed = false,
                outputs = listOf("actual"),
                expectedOutputs = listOf("expected"),
                errors = emptyList(),
            )

        assertFalse(dto.passed)
        assertNotEquals(dto.outputs, dto.expectedOutputs)
    }

    @Test
    fun `should create failing DTO with errors`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 15L,
                passed = false,
                outputs = emptyList(),
                expectedOutputs = listOf("output"),
                errors = listOf("Syntax error", "Type error"),
            )

        assertFalse(dto.passed)
        assertEquals(2, dto.errors.size)
        assertTrue(dto.outputs.isEmpty())
    }

    @Test
    fun `should create DTO with empty outputs and expected outputs`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 20L,
                passed = true,
                outputs = emptyList(),
                expectedOutputs = emptyList(),
                errors = emptyList(),
            )

        assertTrue(dto.outputs.isEmpty())
        assertTrue(dto.expectedOutputs.isEmpty())
        assertTrue(dto.errors.isEmpty())
    }

    @Test
    fun `should support data class copy`() {
        val original =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = true,
                outputs = listOf("out"),
                expectedOutputs = listOf("out"),
                errors = emptyList(),
            )

        val copied = original.copy(passed = false)

        assertFalse(copied.passed)
        assertEquals(original.testId, copied.testId)
        assertEquals(original.outputs, copied.outputs)
    }

    @Test
    fun `should support equality comparison`() {
        val dto1 =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = true,
                outputs = listOf("out"),
                expectedOutputs = listOf("out"),
                errors = emptyList(),
            )

        val dto2 =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = true,
                outputs = listOf("out"),
                expectedOutputs = listOf("out"),
                errors = emptyList(),
            )

        assertEquals(dto1, dto2)
    }

    @Test
    fun `should have different hash codes for different test ids`() {
        val dto1 =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = true,
                outputs = emptyList(),
                expectedOutputs = emptyList(),
                errors = emptyList(),
            )

        val dto2 =
            TestExecutionResponseDTO(
                testId = 2L,
                passed = true,
                outputs = emptyList(),
                expectedOutputs = emptyList(),
                errors = emptyList(),
            )

        assertNotEquals(dto1, dto2)
    }

    @Test
    fun `should generate meaningful toString`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = false,
                outputs = listOf("out"),
                expectedOutputs = listOf("exp"),
                errors = listOf("err"),
            )

        val toString = dto.toString()

        assertTrue(toString.contains("testId"))
        assertTrue(toString.contains("passed"))
        assertTrue(toString.contains("outputs"))
        assertTrue(toString.contains("expectedOutputs"))
        assertTrue(toString.contains("errors"))
    }

    @Test
    fun `should handle multiple outputs`() {
        val outputs = listOf("out1", "out2", "out3", "out4")
        val dto =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = true,
                outputs = outputs,
                expectedOutputs = outputs,
                errors = emptyList(),
            )

        assertEquals(4, dto.outputs.size)
        assertEquals(outputs, dto.outputs)
    }

    @Test
    fun `should handle multiple expected outputs`() {
        val expected = listOf("exp1", "exp2", "exp3")
        val dto =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = false,
                outputs = listOf("act1", "act2"),
                expectedOutputs = expected,
                errors = emptyList(),
            )

        assertEquals(3, dto.expectedOutputs.size)
        assertEquals(expected, dto.expectedOutputs)
    }

    @Test
    fun `should handle multiple errors`() {
        val errors =
            listOf(
                "Error 1: Syntax error",
                "Error 2: Undefined variable",
                "Error 3: Type mismatch",
            )
        val dto =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = false,
                outputs = emptyList(),
                expectedOutputs = emptyList(),
                errors = errors,
            )

        assertEquals(3, dto.errors.size)
        assertEquals(errors, dto.errors)
    }

    @Test
    fun `should handle outputs with special characters`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = true,
                outputs = listOf("Hello\nWorld", "Tab\there"),
                expectedOutputs = listOf("Hello\nWorld", "Tab\there"),
                errors = emptyList(),
            )

        assertTrue(dto.outputs[0].contains("\n"))
        assertTrue(dto.outputs[1].contains("\t"))
    }

    @Test
    fun `should handle large test id`() {
        val dto =
            TestExecutionResponseDTO(
                testId = Long.MAX_VALUE,
                passed = true,
                outputs = emptyList(),
                expectedOutputs = emptyList(),
                errors = emptyList(),
            )

        assertEquals(Long.MAX_VALUE, dto.testId)
    }

    @Test
    fun `should handle different output and expected output sizes`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = false,
                outputs = listOf("out1", "out2"),
                expectedOutputs = listOf("exp1", "exp2", "exp3"),
                errors = emptyList(),
            )

        assertEquals(2, dto.outputs.size)
        assertEquals(3, dto.expectedOutputs.size)
        assertFalse(dto.passed)
    }

    @Test
    fun `should handle empty string outputs`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = true,
                outputs = listOf("", "value", ""),
                expectedOutputs = listOf("", "value", ""),
                errors = emptyList(),
            )

        assertEquals(3, dto.outputs.size)
        assertEquals("", dto.outputs[0])
        assertEquals("", dto.outputs[2])
    }

    @Test
    fun `should handle numeric string outputs`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = true,
                outputs = listOf("42", "3.14", "-10"),
                expectedOutputs = listOf("42", "3.14", "-10"),
                errors = emptyList(),
            )

        assertEquals(listOf("42", "3.14", "-10"), dto.outputs)
        assertEquals(dto.outputs, dto.expectedOutputs)
    }

    @Test
    fun `should handle boolean string outputs`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = true,
                outputs = listOf("true", "false"),
                expectedOutputs = listOf("true", "false"),
                errors = emptyList(),
            )

        assertEquals(listOf("true", "false"), dto.outputs)
    }

    @Test
    fun `should handle long error messages`() {
        val longError =
            "This is a very long error message " +
                "with detailed information about the failure"
        val dto =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = false,
                outputs = emptyList(),
                expectedOutputs = emptyList(),
                errors = listOf(longError),
            )

        assertEquals(longError, dto.errors[0])
    }
}
