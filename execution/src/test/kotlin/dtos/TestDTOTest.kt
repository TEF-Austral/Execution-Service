package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestDTOTest {

    @Test
    fun `should create TestDTO with all fields`() {
        val dto =
            TestDTO(
                id = 1L,
                snippetId = 100L,
                name = "Test DTO",
                inputs = listOf("input1", "input2"),
                expectedOutputs = listOf("output1", "output2"),
            )

        assertEquals(1L, dto.id)
        assertEquals(100L, dto.snippetId)
        assertEquals("Test DTO", dto.name)
        assertEquals(2, dto.inputs.size)
        assertEquals(2, dto.expectedOutputs.size)
    }

    @Test
    fun `should create TestDTO with empty lists`() {
        val dto =
            TestDTO(
                id = 2L,
                snippetId = 200L,
                name = "Empty DTO",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )

        assertEquals(2L, dto.id)
        assertEquals(200L, dto.snippetId)
        assertEquals("Empty DTO", dto.name)
        assertEquals(0, dto.inputs.size)
        assertEquals(0, dto.expectedOutputs.size)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            TestDTO(
                id = 1L,
                snippetId = 100L,
                name = "Original",
                inputs = listOf("in"),
                expectedOutputs = listOf("out"),
            )

        val copied = original.copy(name = "Modified")

        assertEquals(1L, copied.id)
        assertEquals("Modified", copied.name)
        assertEquals(original.inputs, copied.inputs)
    }
}
