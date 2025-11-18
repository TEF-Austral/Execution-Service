package dtos

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FormatterRuleDTOTest {

    @Test
    fun `should create FormatterRuleDTO with boolean value`() {
        val rule =
            FormatterRuleDTO(
                id = "spaceBeforeColon",
                name = "Space Before Colon",
                isActive = true,
                value = true,
            )

        assertEquals("spaceBeforeColon", rule.id)
        assertEquals("Space Before Colon", rule.name)
        assertTrue(rule.isActive)
        assertEquals(true, rule.value)
    }

    @Test
    fun `should create FormatterRuleDTO with integer value`() {
        val rule =
            FormatterRuleDTO(
                id = "indentSize",
                name = "Indent Size",
                isActive = true,
                value = 4,
            )

        assertEquals("indentSize", rule.id)
        assertEquals("Indent Size", rule.name)
        assertTrue(rule.isActive)
        assertEquals(4, rule.value)
    }

    @Test
    fun `should create FormatterRuleDTO with null value`() {
        val rule =
            FormatterRuleDTO(
                id = "testRule",
                name = "Test Rule",
                isActive = false,
                value = null,
            )

        assertEquals("testRule", rule.id)
        assertEquals("Test Rule", rule.name)
        assertFalse(rule.isActive)
        assertNull(rule.value)
    }

    @Test
    fun `should create FormatterRuleDTO without value parameter`() {
        val rule =
            FormatterRuleDTO(
                id = "rule1",
                name = "Rule 1",
                isActive = true,
            )

        assertEquals("rule1", rule.id)
        assertEquals("Rule 1", rule.name)
        assertTrue(rule.isActive)
        assertNull(rule.value)
    }

    @Test
    fun `should support copy functionality`() {
        val original =
            FormatterRuleDTO(
                id = "rule1",
                name = "Rule 1",
                isActive = true,
                value = 5,
            )

        val copied = original.copy(isActive = false, value = 10)

        assertEquals("rule1", copied.id)
        assertEquals("Rule 1", copied.name)
        assertFalse(copied.isActive)
        assertEquals(10, copied.value)
    }
}
