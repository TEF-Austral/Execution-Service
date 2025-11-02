package dtos

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ExecutionResponseDTOTest {

    @Test
    fun `should create DTO with all properties`() {
        val dto =
            ExecutionResponseDTO(
                outputs = listOf("output1", "output2"),
                errors = listOf("error1"),
                success = true,
            )

        assertEquals(listOf("output1", "output2"), dto.outputs)
        assertEquals(listOf("error1"), dto.errors)
        assertTrue(dto.success)
    }

    @Test
    fun `should create successful DTO with no errors`() {
        val dto =
            ExecutionResponseDTO(
                outputs = listOf("result"),
                errors = emptyList(),
                success = true,
            )

        assertTrue(dto.success)
        assertTrue(dto.errors.isEmpty())
        assertFalse(dto.outputs.isEmpty())
    }

    @Test
    fun `should create failed DTO with errors`() {
        val dto =
            ExecutionResponseDTO(
                outputs = emptyList(),
                errors = listOf("Syntax error", "Type error"),
                success = false,
            )

        assertFalse(dto.success)
        assertEquals(2, dto.errors.size)
        assertTrue(dto.outputs.isEmpty())
    }

    @Test
    fun `should create DTO with empty outputs and errors`() {
        val dto =
            ExecutionResponseDTO(
                outputs = emptyList(),
                errors = emptyList(),
                success = true,
            )

        assertTrue(dto.outputs.isEmpty())
        assertTrue(dto.errors.isEmpty())
        assertTrue(dto.success)
    }

    @Test
    fun `should support data class copy`() {
        val original =
            ExecutionResponseDTO(
                outputs = listOf("output"),
                errors = emptyList(),
                success = true,
            )

        val copied = original.copy(success = false)

        assertFalse(copied.success)
        assertEquals(original.outputs, copied.outputs)
        assertEquals(original.errors, copied.errors)
    }

    @Test
    fun `should support equality comparison`() {
        val dto1 =
            ExecutionResponseDTO(
                outputs = listOf("out"),
                errors = listOf("err"),
                success = false,
            )

        val dto2 =
            ExecutionResponseDTO(
                outputs = listOf("out"),
                errors = listOf("err"),
                success = false,
            )

        assertEquals(dto1, dto2)
    }

    @Test
    fun `should have different hash codes for different success values`() {
        val dto1 =
            ExecutionResponseDTO(
                outputs = listOf("out"),
                errors = emptyList(),
                success = true,
            )

        val dto2 =
            ExecutionResponseDTO(
                outputs = listOf("out"),
                errors = emptyList(),
                success = false,
            )

        assertNotEquals(dto1, dto2)
    }

    @Test
    fun `should generate meaningful toString`() {
        val dto =
            ExecutionResponseDTO(
                outputs = listOf("output"),
                errors = listOf("error"),
                success = false,
            )

        val toString = dto.toString()

        assertTrue(toString.contains("outputs"))
        assertTrue(toString.contains("errors"))
        assertTrue(toString.contains("success"))
    }

    @Test
    fun `should handle multiple outputs`() {
        val outputs = listOf("out1", "out2", "out3", "out4", "out5")
        val dto =
            ExecutionResponseDTO(
                outputs = outputs,
                errors = emptyList(),
                success = true,
            )

        assertEquals(5, dto.outputs.size)
        assertEquals(outputs, dto.outputs)
    }

    @Test
    fun `should handle multiple errors`() {
        val errors =
            listOf(
                "Syntax error on line 1",
                "Undefined variable x",
                "Type mismatch",
            )
        val dto =
            ExecutionResponseDTO(
                outputs = emptyList(),
                errors = errors,
                success = false,
            )

        assertEquals(3, dto.errors.size)
        assertEquals(errors, dto.errors)
    }

    @Test
    fun `should handle outputs with special characters`() {
        val dto =
            ExecutionResponseDTO(
                outputs = listOf("Hello\nWorld", "Tab\there", "Quote\"inside"),
                errors = emptyList(),
                success = true,
            )

        assertEquals(3, dto.outputs.size)
        assertTrue(dto.outputs[0].contains("\n"))
        assertTrue(dto.outputs[1].contains("\t"))
        assertTrue(dto.outputs[2].contains("\""))
    }

    @Test
    fun `should handle long error messages`() {
        val longError =
            "This is a very long error message " +
                "that spans multiple lines and contains detailed information about what went wrong during execution"
        val dto =
            ExecutionResponseDTO(
                outputs = emptyList(),
                errors = listOf(longError),
                success = false,
            )

        assertEquals(longError, dto.errors[0])
    }

    @Test
    fun `should handle empty string outputs`() {
        val dto =
            ExecutionResponseDTO(
                outputs = listOf("", "value", ""),
                errors = emptyList(),
                success = true,
            )

        assertEquals(3, dto.outputs.size)
        assertEquals("", dto.outputs[0])
        assertEquals("", dto.outputs[2])
    }

    @Test
    fun `should handle numeric string outputs`() {
        val dto =
            ExecutionResponseDTO(
                outputs = listOf("42", "3.14", "-10"),
                errors = emptyList(),
                success = true,
            )

        assertEquals(listOf("42", "3.14", "-10"), dto.outputs)
    }

    @Test
    fun `should handle boolean string outputs`() {
        val dto =
            ExecutionResponseDTO(
                outputs = listOf("true", "false"),
                errors = emptyList(),
                success = true,
            )

        assertEquals(listOf("true", "false"), dto.outputs)
    }
}
