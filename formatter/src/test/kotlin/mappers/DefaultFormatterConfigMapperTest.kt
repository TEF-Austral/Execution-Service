package mappers

import dtos.FormatterRuleDTO
import entities.FormatterConfigEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DefaultFormatterConfigMapperTest {

    private val mapper = DefaultFormatterConfigMapper()

    @Test
    fun `entityToRules should convert entity to rules list`() {
        val entity =
            FormatterConfigEntity(
                id = 1L,
                userId = "user123",
                spaceBeforeColon = true,
                spaceAfterColon = false,
                spaceAroundAssignment = true,
                blankLinesAfterPrintln = 2,
                indentSize = 4,
                ifBraceOnSameLine = true,
                enforceSingleSpace = false,
                spaceAroundOperators = true,
            )

        val rules = mapper.entityToRules(entity)

        assertEquals(8, rules.size)
        val spaceBeforeColonRule = rules.find { it.id == "spaceBeforeColon" }
        assertTrue(spaceBeforeColonRule?.isActive == true)
        val blankLinesRule = rules.find { it.id == "blankLinesAfterPrintln" }
        assertEquals(2, blankLinesRule?.value)
    }

    @Test
    fun `rulesToConfigDTO should convert rules to config DTO`() {
        val rules =
            listOf(
                FormatterRuleDTO("spaceBeforeColon", "Space Before Colon", true, true),
                FormatterRuleDTO("spaceAfterColon", "Space After Colon", false, false),
                FormatterRuleDTO("spaceAroundAssignment", "Space Around Assignment", true, true),
                FormatterRuleDTO("blankLinesAfterPrintln", "Blank Lines", true, 3),
                FormatterRuleDTO("indentSize", "Indent Size", true, 2),
                FormatterRuleDTO("ifBraceOnSameLine", "Brace Position", true, false),
                FormatterRuleDTO("enforceSingleSpace", "Single Space", true, true),
                FormatterRuleDTO("spaceAroundOperators", "Operator Space", true, false),
            )

        val configDTO = mapper.rulesToConfigDTO(rules)

        assertTrue(configDTO.spaceBeforeColon)
        assertFalse(configDTO.spaceAfterColon)
        assertTrue(configDTO.spaceAroundAssignment)
        assertEquals(3, configDTO.blankLinesAfterPrintln)
        assertEquals(2, configDTO.indentSize)
        assertTrue(configDTO.enforceSingleSpace)
    }

    @Test
    fun `rulesToConfigDTO should use defaults when rules are missing`() {
        val rules = emptyList<FormatterRuleDTO>()

        val configDTO = mapper.rulesToConfigDTO(rules)

        assertFalse(configDTO.spaceBeforeColon)
        assertTrue(configDTO.spaceAfterColon)
        assertTrue(configDTO.spaceAroundAssignment)
        assertEquals(1, configDTO.blankLinesAfterPrintln)
        assertEquals(4, configDTO.indentSize)
        assertTrue(configDTO.ifBraceOnSameLine)
        assertTrue(configDTO.enforceSingleSpace)
        assertTrue(configDTO.spaceAroundOperators)
    }

    @Test
    fun `rulesToEntity should create entity from rules`() {
        val rules =
            listOf(
                FormatterRuleDTO("spaceBeforeColon", "Space Before Colon", false, false),
                FormatterRuleDTO("spaceAfterColon", "Space After Colon", true, true),
                FormatterRuleDTO("spaceAroundAssignment", "Space Around Assignment", true, true),
                FormatterRuleDTO("blankLinesAfterPrintln", "Blank Lines", true, 1),
                FormatterRuleDTO("indentSize", "Indent Size", true, 4),
                FormatterRuleDTO("ifBraceOnSameLine", "Brace Position", true, true),
                FormatterRuleDTO("enforceSingleSpace", "Single Space", true, true),
                FormatterRuleDTO("spaceAroundOperators", "Operator Space", true, true),
            )

        val entity = mapper.rulesToEntity("user456", rules)

        assertEquals("user456", entity.userId)
        assertFalse(entity.spaceBeforeColon)
        assertTrue(entity.spaceAfterColon)
        assertTrue(entity.spaceAroundAssignment)
        assertEquals(1, entity.blankLinesAfterPrintln)
        assertEquals(4, entity.indentSize)
        assertTrue(entity.ifBraceOnSameLine)
        assertTrue(entity.enforceSingleSpace)
        assertTrue(entity.spaceAroundOperators)
    }

    @Test
    fun `updateEntity should update entity with rules`() {
        val originalEntity =
            FormatterConfigEntity(
                id = 1L,
                userId = "user789",
                spaceBeforeColon = true,
                spaceAfterColon = true,
                spaceAroundAssignment = true,
                blankLinesAfterPrintln = 1,
                indentSize = 4,
                ifBraceOnSameLine = true,
                enforceSingleSpace = true,
                spaceAroundOperators = true,
            )

        val rules =
            listOf(
                FormatterRuleDTO("spaceBeforeColon", "Space Before Colon", false, false),
                FormatterRuleDTO("indentSize", "Indent Size", true, 2),
            )

        val updatedEntity = mapper.updateEntity(originalEntity, rules)

        assertEquals(1L, updatedEntity.id)
        assertEquals("user789", updatedEntity.userId)
        assertFalse(updatedEntity.spaceBeforeColon)
        assertEquals(2, updatedEntity.indentSize)
    }

    @Test
    fun `rulesToConfigDTO should handle invalid integer values`() {
        val rules =
            listOf(
                FormatterRuleDTO("indentSize", "Indent Size", true, "invalid"),
                FormatterRuleDTO("blankLinesAfterPrintln", "Blank Lines", true, null),
            )

        val configDTO = mapper.rulesToConfigDTO(rules)

        assertEquals(4, configDTO.indentSize)
        assertEquals(1, configDTO.blankLinesAfterPrintln)
    }
}
