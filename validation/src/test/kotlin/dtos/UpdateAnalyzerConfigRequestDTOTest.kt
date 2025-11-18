package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateAnalyzerConfigRequestDTOTest {

    @Test
    fun `should create UpdateAnalyzerConfigRequestDTO with rules`() {
        val rules =
            listOf(
                AnalyzerRuleDTO("rule1", "Rule 1", true, "value1"),
                AnalyzerRuleDTO("rule2", "Rule 2", false, null),
            )

        val request = UpdateAnalyzerConfigRequestDTO(rules)

        assertEquals(2, request.rules.size)
        assertEquals("rule1", request.rules[0].id)
        assertEquals("rule2", request.rules[1].id)
    }

    @Test
    fun `should create UpdateAnalyzerConfigRequestDTO with empty rules`() {
        val request = UpdateAnalyzerConfigRequestDTO(emptyList())

        assertEquals(0, request.rules.size)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            UpdateAnalyzerConfigRequestDTO(
                listOf(AnalyzerRuleDTO("rule1", "Rule 1", true, "value1")),
            )

        val newRules =
            listOf(
                AnalyzerRuleDTO("rule2", "Rule 2", false, null),
            )
        val copied = original.copy(rules = newRules)

        assertEquals(1, copied.rules.size)
        assertEquals("rule2", copied.rules[0].id)
    }
}
