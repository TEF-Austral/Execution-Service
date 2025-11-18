package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CreateTestRequestDTOExtraTest {

    @Test
    fun `should create CreateTestRequestDTO with all fields`() {
        val dto =
            CreateTestRequestDTO(
                snippetId = 123L,
                name = "Test Case",
                inputs = listOf("input1", "input2"),
                expectedOutputs = listOf("output1", "output2"),
            )

        assertEquals(123L, dto.snippetId)
        assertEquals("Test Case", dto.name)
        assertEquals(2, dto.inputs?.size)
        assertEquals(2, dto.expectedOutputs?.size)
    }

    @Test
    fun `should create CreateTestRequestDTO with null inputs`() {
        val dto =
            CreateTestRequestDTO(
                snippetId = 456L,
                name = "Null Inputs Test",
                inputs = null,
                expectedOutputs = listOf("output1"),
            )

        assertEquals(456L, dto.snippetId)
        assertEquals("Null Inputs Test", dto.name)
        assertNull(dto.inputs)
        assertEquals(1, dto.expectedOutputs?.size)
    }

    @Test
    fun `should create CreateTestRequestDTO with null outputs`() {
        val dto =
            CreateTestRequestDTO(
                snippetId = 789L,
                name = "Null Outputs Test",
                inputs = listOf("input1"),
                expectedOutputs = null,
            )

        assertEquals(789L, dto.snippetId)
        assertEquals("Null Outputs Test", dto.name)
        assertEquals(1, dto.inputs?.size)
        assertNull(dto.expectedOutputs)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            CreateTestRequestDTO(
                snippetId = 100L,
                name = "Original",
                inputs = listOf("in"),
                expectedOutputs = listOf("out"),
            )

        val copied = original.copy(name = "Modified")

        assertEquals(100L, copied.snippetId)
        assertEquals("Modified", copied.name)
        assertEquals(original.inputs, copied.inputs)
    }
}
