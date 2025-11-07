package dtos

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CreateTestRequestDTOTest {

    @Test
    fun `should create DTO with all properties`() {
        val dto =
            CreateTestRequestDTO(
                snippetId = 100L,
                name = "Test Name",
                inputs = listOf("input1", "input2"),
                expectedOutputs = listOf("output1", "output2"),
            )

        Assertions.assertEquals(100L, dto.snippetId)
        Assertions.assertEquals("Test Name", dto.name)
        Assertions.assertEquals(listOf("input1", "input2"), dto.inputs)
        Assertions.assertEquals(listOf("output1", "output2"), dto.expectedOutputs)
    }

    @Test
    fun `should create DTO with empty lists`() {
        val dto =
            CreateTestRequestDTO(
                snippetId = 1L,
                name = "Empty Test",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )

        Assertions.assertTrue(dto.inputs!!.isEmpty())
        Assertions.assertTrue(dto.expectedOutputs!!.isEmpty())
    }

    @Test
    fun `should create DTO with single input and output`() {
        val dto =
            CreateTestRequestDTO(
                snippetId = 50L,
                name = "Single IO Test",
                inputs = listOf("single input"),
                expectedOutputs = listOf("single output"),
            )

        Assertions.assertEquals(1, dto.inputs!!.size)
        Assertions.assertEquals(1, dto.expectedOutputs!!.size)
        Assertions.assertEquals("single input", dto.inputs[0])
        Assertions.assertEquals("single output", dto.expectedOutputs[0])
    }

    @Test
    fun `should create DTO with multiple inputs and outputs`() {
        val dto =
            CreateTestRequestDTO(
                snippetId = 200L,
                name = "Multiple IO Test",
                inputs = listOf("input1", "input2", "input3"),
                expectedOutputs = listOf("output1", "output2", "output3", "output4"),
            )

        Assertions.assertEquals(3, dto.inputs!!.size)
        Assertions.assertEquals(4, dto.expectedOutputs!!.size)
    }

    @Test
    fun `should support data class copy`() {
        val original =
            CreateTestRequestDTO(
                snippetId = 1L,
                name = "Original",
                inputs = listOf("a"),
                expectedOutputs = listOf("b"),
            )

        val copied = original.copy(name = "Modified")

        Assertions.assertEquals("Modified", copied.name)
        Assertions.assertEquals(1L, copied.snippetId)
        Assertions.assertEquals(original.inputs, copied.inputs)
        Assertions.assertEquals(original.expectedOutputs, copied.expectedOutputs)
    }

    @Test
    fun `should support equality comparison`() {
        val dto1 =
            CreateTestRequestDTO(
                snippetId = 1L,
                name = "Test",
                inputs = listOf("input"),
                expectedOutputs = listOf("output"),
            )

        val dto2 =
            CreateTestRequestDTO(
                snippetId = 1L,
                name = "Test",
                inputs = listOf("input"),
                expectedOutputs = listOf("output"),
            )

        Assertions.assertEquals(dto1, dto2)
    }

    @Test
    fun `should have different hash codes for different objects`() {
        val dto1 =
            CreateTestRequestDTO(
                snippetId = 1L,
                name = "Test1",
                inputs = listOf("input1"),
                expectedOutputs = listOf("output1"),
            )

        val dto2 =
            CreateTestRequestDTO(
                snippetId = 2L,
                name = "Test2",
                inputs = listOf("input2"),
                expectedOutputs = listOf("output2"),
            )

        Assertions.assertNotEquals(dto1.hashCode(), dto2.hashCode())
    }

    @Test
    fun `should generate meaningful toString`() {
        val dto =
            CreateTestRequestDTO(
                snippetId = 1L,
                name = "Test",
                inputs = listOf("input"),
                expectedOutputs = listOf("output"),
            )

        val toString = dto.toString()

        Assertions.assertTrue(toString.contains("snippetId"))
        Assertions.assertTrue(toString.contains("name"))
        Assertions.assertTrue(toString.contains("inputs"))
        Assertions.assertTrue(toString.contains("expectedOutputs"))
    }

    @Test
    fun `should handle special characters in name`() {
        val dto =
            CreateTestRequestDTO(
                snippetId = 1L,
                name = "Test with special chars: !@#$%",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )

        Assertions.assertEquals("Test with special chars: !@#$%", dto.name)
    }

    @Test
    fun `should handle empty strings in inputs`() {
        val dto =
            CreateTestRequestDTO(
                snippetId = 1L,
                name = "Test",
                inputs = listOf("", "input2", ""),
                expectedOutputs = listOf("output"),
            )

        Assertions.assertEquals(3, dto.inputs!!.size)
        Assertions.assertEquals("", dto.inputs[0])
        Assertions.assertEquals("", dto.inputs[2])
    }

    @Test
    fun `should handle multiline strings in expected outputs`() {
        val dto =
            CreateTestRequestDTO(
                snippetId = 1L,
                name = "Test",
                inputs = emptyList(),
                expectedOutputs = listOf("line1\nline2\nline3"),
            )

        Assertions.assertTrue(dto.expectedOutputs!![0].contains("\n"))
    }
}
