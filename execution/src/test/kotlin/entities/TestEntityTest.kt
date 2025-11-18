package entities

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class TestEntityTest {

    @Test
    fun `should create TestEntity with all fields`() {
        val entity =
            TestEntity(
                id = 1L,
                snippetId = 100L,
                name = "Test Case 1",
                inputs = listOf("input1", "input2"),
                expectedOutputs = listOf("output1", "output2"),
            )

        assertEquals(1L, entity.id)
        assertEquals(100L, entity.snippetId)
        assertEquals("Test Case 1", entity.name)
        assertEquals(2, entity.inputs.size)
        assertEquals(2, entity.expectedOutputs.size)
    }

    @Test
    fun `should create TestEntity with default values`() {
        val entity =
            TestEntity(
                snippetId = 200L,
                name = "Test Case 2",
            )

        assertEquals(0L, entity.id)
        assertEquals(200L, entity.snippetId)
        assertEquals("Test Case 2", entity.name)
        assertNotNull(entity.inputs)
        assertNotNull(entity.expectedOutputs)
        assertEquals(0, entity.inputs.size)
        assertEquals(0, entity.expectedOutputs.size)
    }

    @Test
    fun `should create TestEntity with empty inputs and outputs`() {
        val entity =
            TestEntity(
                id = 3L,
                snippetId = 300L,
                name = "Empty Test",
                inputs = emptyList(),
                expectedOutputs = emptyList(),
            )

        assertEquals(3L, entity.id)
        assertEquals(300L, entity.snippetId)
        assertEquals("Empty Test", entity.name)
        assertEquals(0, entity.inputs.size)
        assertEquals(0, entity.expectedOutputs.size)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            TestEntity(
                id = 1L,
                snippetId = 100L,
                name = "Original",
                inputs = listOf("in1"),
                expectedOutputs = listOf("out1"),
            )

        val copied = original.copy(name = "Modified")

        assertEquals(1L, copied.id)
        assertEquals(100L, copied.snippetId)
        assertEquals("Modified", copied.name)
        assertEquals(original.inputs, copied.inputs)
        assertEquals(original.expectedOutputs, copied.expectedOutputs)
    }
}
