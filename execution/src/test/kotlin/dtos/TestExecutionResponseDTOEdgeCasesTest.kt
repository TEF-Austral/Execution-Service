package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TestExecutionResponseDTOEdgeCasesTest {

    @Test
    fun `should create DTO with empty outputs and expected outputs`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 1L,
                passed = true,
                outputs = emptyList(),
                expectedOutputs = emptyList(),
                errors = emptyList(),
            )

        assertEquals(1L, dto.testId)
        assertTrue(dto.passed)
        assertEquals(0, dto.outputs.size)
        assertEquals(0, dto.expectedOutputs.size)
        assertEquals(0, dto.errors.size)
    }

    @Test
    fun `should create DTO with multiple errors`() {
        val dto =
            TestExecutionResponseDTO(
                testId = 2L,
                passed = false,
                outputs = listOf("partial output"),
                expectedOutputs = listOf("expected output"),
                errors = listOf("Error 1", "Error 2", "Error 3"),
            )

        assertFalse(dto.passed)
        assertEquals(3, dto.errors.size)
        assertEquals("Error 1", dto.errors[0])
        assertEquals("Error 3", dto.errors[2])
    }

    @Test
    fun `should support copy functionality with passed change`() {
        val original =
            TestExecutionResponseDTO(
                testId = 3L,
                passed = true,
                outputs = listOf("out"),
                expectedOutputs = listOf("out"),
                errors = emptyList(),
            )

        val copied = original.copy(passed = false)

        assertEquals(3L, copied.testId)
        assertFalse(copied.passed)
        assertEquals(original.outputs, copied.outputs)
    }

    @Test
    fun `should support copy functionality with different outputs`() {
        val original =
            TestExecutionResponseDTO(
                testId = 4L,
                passed = true,
                outputs = listOf("original"),
                expectedOutputs = listOf("original"),
                errors = emptyList(),
            )

        val copied =
            original.copy(
                outputs = listOf("modified"),
                passed = false,
            )

        assertEquals(4L, copied.testId)
        assertFalse(copied.passed)
        assertEquals(listOf("modified"), copied.outputs)
        assertEquals(original.expectedOutputs, copied.expectedOutputs)
    }

    @Test
    fun `should create DTO with very long output list`() {
        val longOutputs = (1..100).map { "output$it" }
        val dto =
            TestExecutionResponseDTO(
                testId = 5L,
                passed = true,
                outputs = longOutputs,
                expectedOutputs = longOutputs,
                errors = emptyList(),
            )

        assertEquals(100, dto.outputs.size)
        assertEquals(100, dto.expectedOutputs.size)
        assertTrue(dto.passed)
    }
}
