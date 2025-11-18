package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExecutionResponseDTOTest {

    @Test
    fun `should create ExecutionResponseDTO with success`() {
        val dto =
            ExecutionResponseDTO(
                outputs = listOf("output1", "output2"),
                errors = emptyList(),
                success = true,
            )

        assertEquals(2, dto.outputs.size)
        assertEquals(0, dto.errors.size)
        assertTrue(dto.success)
    }

    @Test
    fun `should create ExecutionResponseDTO with errors`() {
        val dto =
            ExecutionResponseDTO(
                outputs = emptyList(),
                errors = listOf("error1", "error2"),
                success = false,
            )

        assertEquals(0, dto.outputs.size)
        assertEquals(2, dto.errors.size)
        assertFalse(dto.success)
    }

    @Test
    fun `should create ExecutionResponseDTO with both outputs and errors`() {
        val dto =
            ExecutionResponseDTO(
                outputs = listOf("output1"),
                errors = listOf("error1"),
                success = false,
            )

        assertEquals(1, dto.outputs.size)
        assertEquals(1, dto.errors.size)
        assertFalse(dto.success)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            ExecutionResponseDTO(
                outputs = listOf("output1"),
                errors = emptyList(),
                success = true,
            )

        val copied = original.copy(success = false)

        assertEquals(original.outputs, copied.outputs)
        assertFalse(copied.success)
    }
}
