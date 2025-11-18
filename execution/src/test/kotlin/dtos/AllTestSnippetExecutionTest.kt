package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AllTestSnippetExecutionTest {

    @Test
    fun `should create AllTestSnippetExecution with executions`() {
        val executions =
            listOf(
                TestExecutionResponseDTO(
                    testId = 1L,
                    passed = true,
                    outputs = listOf("output1"),
                    expectedOutputs = listOf("output1"),
                    errors = emptyList(),
                ),
                TestExecutionResponseDTO(
                    testId = 2L,
                    passed = false,
                    outputs = listOf("output2"),
                    expectedOutputs = listOf("expected2"),
                    errors = listOf("error1"),
                ),
            )

        val result = AllTestSnippetExecution(executions)

        assertEquals(2, result.executions.size)
        assertEquals(1L, result.executions[0].testId)
        assertEquals(2L, result.executions[1].testId)
    }

    @Test
    fun `should create AllTestSnippetExecution with empty executions`() {
        val result = AllTestSnippetExecution(emptyList())

        assertEquals(0, result.executions.size)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            AllTestSnippetExecution(
                listOf(
                    TestExecutionResponseDTO(
                        testId = 1L,
                        passed = true,
                        outputs = listOf("out"),
                        expectedOutputs = listOf("out"),
                        errors = emptyList(),
                    ),
                ),
            )

        val newExecutions = emptyList<TestExecutionResponseDTO>()
        val copied = original.copy(executions = newExecutions)

        assertEquals(0, copied.executions.size)
    }
}
