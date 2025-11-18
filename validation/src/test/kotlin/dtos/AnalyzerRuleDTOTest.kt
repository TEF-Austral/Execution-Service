package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AnalyzerRuleDTOTest {

    @Test
    fun `should create AnalyzerRuleDTO with all fields`() {
        val rule =
            AnalyzerRuleDTO(
                id = "rule1",
                name = "Rule 1",
                isActive = true,
                value = "value1",
            )

        assertEquals("rule1", rule.id)
        assertEquals("Rule 1", rule.name)
        assertTrue(rule.isActive)
        assertEquals("value1", rule.value)
    }

    @Test
    fun `should create AnalyzerRuleDTO with null value`() {
        val rule =
            AnalyzerRuleDTO(
                id = "rule2",
                name = "Rule 2",
                isActive = false,
                value = null,
            )

        assertEquals("rule2", rule.id)
        assertEquals("Rule 2", rule.name)
        assertFalse(rule.isActive)
        assertNull(rule.value)
    }

    @Test
    fun `should create AnalyzerRuleDTO without value parameter`() {
        val rule =
            AnalyzerRuleDTO(
                id = "rule3",
                name = "Rule 3",
                isActive = true,
            )

        assertEquals("rule3", rule.id)
        assertEquals("Rule 3", rule.name)
        assertTrue(rule.isActive)
        assertNull(rule.value)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            AnalyzerRuleDTO(
                id = "original",
                name = "Original",
                isActive = true,
                value = "originalValue",
            )

        val copied = original.copy(isActive = false, value = "newValue")

        assertEquals("original", copied.id)
        assertEquals("Original", copied.name)
        assertFalse(copied.isActive)
        assertEquals("newValue", copied.value)
    }

    @Test
    fun `should support equality comparison`() {
        val rule1 = AnalyzerRuleDTO("id1", "Name 1", true, "value")
        val rule2 = AnalyzerRuleDTO("id1", "Name 1", true, "value")
        val rule3 = AnalyzerRuleDTO("id2", "Name 2", false, null)

        assertEquals(rule1, rule2)
        assertTrue(rule1 != rule3)
    }
}
