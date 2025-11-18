package mappers

import dtos.FormatterRuleDTO
import entities.FormatterConfigEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FormatterConfigMapperEdgeCasesTest {

    private val mapper = DefaultFormatterConfigMapper()

    @Test
    fun `entityToRules should create rules for all entity fields`() {
        val entity =
            FormatterConfigEntity(
                id = 5L,
                userId = "edge-case-user",
                spaceBeforeColon = false,
                spaceAfterColon = false,
                spaceAroundAssignment = false,
                blankLinesAfterPrintln = 0,
                indentSize = 1,
                ifBraceOnSameLine = false,
                enforceSingleSpace = false,
                spaceAroundOperators = false,
            )

        val rules = mapper.entityToRules(entity)

        assertEquals(8, rules.size)
        rules.forEach { rule ->
            when (rule.id) {
                "blankLinesAfterPrintln" -> assertEquals(0, rule.value)
                "indentSize" -> assertEquals(1, rule.value)
                else -> {
                    if (rule.value is Boolean) {
                        assertFalse(rule.isActive)
                    }
                }
            }
        }
    }

    @Test
    fun `rulesToConfigDTO should handle partial rule list`() {
        val rules =
            listOf(
                FormatterRuleDTO("spaceBeforeColon", "Space Before Colon", true, true),
                FormatterRuleDTO("indentSize", "Indent Size", true, 8),
            )

        val configDTO = mapper.rulesToConfigDTO(rules)

        assertTrue(configDTO.spaceBeforeColon)
        assertEquals(8, configDTO.indentSize)
        assertTrue(configDTO.spaceAfterColon)
        assertTrue(configDTO.spaceAroundAssignment)
    }

    @Test
    fun `rulesToConfigDTO should handle string value for int field`() {
        val rules =
            listOf(
                FormatterRuleDTO("indentSize", "Indent Size", true, "8"),
                FormatterRuleDTO("blankLinesAfterPrintln", "Blank Lines", true, "2"),
            )

        val configDTO = mapper.rulesToConfigDTO(rules)

        assertEquals(8, configDTO.indentSize)
        assertEquals(2, configDTO.blankLinesAfterPrintln)
    }

    @Test
    fun `rulesToConfigDTO should use default for non-integer string`() {
        val rules =
            listOf(
                FormatterRuleDTO("indentSize", "Indent Size", true, "not-a-number"),
            )

        val configDTO = mapper.rulesToConfigDTO(rules)

        assertEquals(4, configDTO.indentSize)
    }

    @Test
    fun `rulesToEntity should create entity with extreme values`() {
        val rules =
            listOf(
                FormatterRuleDTO("spaceBeforeColon", "Space Before Colon", true, true),
                FormatterRuleDTO("spaceAfterColon", "Space After Colon", true, true),
                FormatterRuleDTO("spaceAroundAssignment", "Space Around Assignment", true, true),
                FormatterRuleDTO("blankLinesAfterPrintln", "Blank Lines", true, 10),
                FormatterRuleDTO("indentSize", "Indent Size", true, 16),
                FormatterRuleDTO("ifBraceOnSameLine", "Brace Position", true, true),
                FormatterRuleDTO("enforceSingleSpace", "Single Space", true, true),
                FormatterRuleDTO("spaceAroundOperators", "Operator Space", true, true),
            )

        val entity = mapper.rulesToEntity("extreme-user", rules)

        assertEquals("extreme-user", entity.userId)
        assertEquals(10, entity.blankLinesAfterPrintln)
        assertEquals(16, entity.indentSize)
        assertTrue(entity.spaceBeforeColon)
    }

    @Test
    fun `updateEntity should preserve id and userId`() {
        val originalEntity =
            FormatterConfigEntity(
                id = 999L,
                userId = "preserved-user",
                spaceBeforeColon = true,
                spaceAfterColon = true,
                spaceAroundAssignment = true,
                blankLinesAfterPrintln = 5,
                indentSize = 2,
                ifBraceOnSameLine = true,
                enforceSingleSpace = true,
                spaceAroundOperators = true,
            )

        val rules =
            listOf(
                FormatterRuleDTO("spaceBeforeColon", "Space Before Colon", false, false),
            )

        val updatedEntity = mapper.updateEntity(originalEntity, rules)

        assertEquals(999L, updatedEntity.id)
        assertEquals("preserved-user", updatedEntity.userId)
        assertFalse(updatedEntity.spaceBeforeColon)
    }

    @Test
    fun `entityToRules should handle zero and negative values properly`() {
        val entity =
            FormatterConfigEntity(
                id = 1L,
                userId = "zero-user",
                spaceBeforeColon = false,
                spaceAfterColon = true,
                spaceAroundAssignment = true,
                blankLinesAfterPrintln = 0,
                indentSize = 0,
                ifBraceOnSameLine = true,
                enforceSingleSpace = true,
                spaceAroundOperators = true,
            )

        val rules = mapper.entityToRules(entity)
        val blankLinesRule = rules.find { it.id == "blankLinesAfterPrintln" }
        val indentRule = rules.find { it.id == "indentSize" }

        assertEquals(0, blankLinesRule?.value)
        assertEquals(0, indentRule?.value)
    }

    @Test
    fun `rulesToConfigDTO should handle null values in rules`() {
        val rules =
            listOf(
                FormatterRuleDTO("indentSize", "Indent Size", true, null),
                FormatterRuleDTO("blankLinesAfterPrintln", "Blank Lines", true, null),
            )

        val configDTO = mapper.rulesToConfigDTO(rules)

        assertEquals(4, configDTO.indentSize)
        assertEquals(1, configDTO.blankLinesAfterPrintln)
    }
}
