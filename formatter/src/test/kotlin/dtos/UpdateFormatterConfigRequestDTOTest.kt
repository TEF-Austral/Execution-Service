package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateFormatterConfigRequestDTOTest {

    @Test
    fun `should create UpdateFormatterConfigRequestDTO with rules`() {
        val rules =
            listOf(
                FormatterRuleDTO("rule1", "Rule 1", true, true),
                FormatterRuleDTO("rule2", "Rule 2", false, 5),
            )

        val request = UpdateFormatterConfigRequestDTO(rules)

        assertEquals(2, request.rules.size)
        assertEquals("rule1", request.rules[0].id)
        assertEquals("rule2", request.rules[1].id)
    }

    @Test
    fun `should create UpdateFormatterConfigRequestDTO with empty rules`() {
        val request = UpdateFormatterConfigRequestDTO(emptyList())

        assertEquals(0, request.rules.size)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            UpdateFormatterConfigRequestDTO(
                listOf(FormatterRuleDTO("rule1", "Rule 1", true, true)),
            )

        val newRules =
            listOf(
                FormatterRuleDTO("rule2", "Rule 2", false, false),
            )
        val copied = original.copy(rules = newRules)

        assertEquals(1, copied.rules.size)
        assertEquals("rule2", copied.rules[0].id)
    }
}
