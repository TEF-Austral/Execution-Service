package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class UpdateTestRequestDTOTest {

    @Test
    fun `should create UpdateTestRequestDTO with all fields`() {
        val dto =
            UpdateTestRequestDTO(
                name = "Updated Test",
                inputs = listOf("new input"),
                expectedOutputs = listOf("new output"),
            )

        assertEquals("Updated Test", dto.name)
        assertEquals(1, dto.inputs?.size)
        assertEquals(1, dto.expectedOutputs?.size)
    }

    @Test
    fun `should create UpdateTestRequestDTO with null name`() {
        val dto =
            UpdateTestRequestDTO(
                name = null,
                inputs = listOf("input"),
                expectedOutputs = listOf("output"),
            )

        assertNull(dto.name)
        assertEquals(1, dto.inputs?.size)
        assertEquals(1, dto.expectedOutputs?.size)
    }

    @Test
    fun `should create UpdateTestRequestDTO with all null fields`() {
        val dto =
            UpdateTestRequestDTO(
                name = null,
                inputs = null,
                expectedOutputs = null,
            )

        assertNull(dto.name)
        assertNull(dto.inputs)
        assertNull(dto.expectedOutputs)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            UpdateTestRequestDTO(
                name = "Original",
                inputs = listOf("in1"),
                expectedOutputs = listOf("out1"),
            )

        val copied = original.copy(name = "Modified")

        assertEquals("Modified", copied.name)
        assertEquals(original.inputs, copied.inputs)
        assertEquals(original.expectedOutputs, copied.expectedOutputs)
    }
}
