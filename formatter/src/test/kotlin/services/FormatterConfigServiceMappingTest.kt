package services

import dtos.FormatConfigDTO
import dtos.FormatterRuleDTO
import mappers.DefaultFormatterConfigMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FormatterConfigServiceMappingTest {

    @Test
    fun `rulesToConfigDTO should map mixed boolean and numeric rules`() {
        val rules =
            listOf(
                FormatterRuleDTO("spaceBeforeColon", "Space Before Colon", true, true),
                FormatterRuleDTO("spaceAfterColon", "Space After Colon", false, false),
                FormatterRuleDTO("spaceAroundAssignment", "Space Around Assignment", true, true),
                FormatterRuleDTO("blankLinesAfterPrintln", "Blank Lines", true, 2),
                FormatterRuleDTO("indentSize", "Indent Size", true, 6),
                FormatterRuleDTO("ifBraceOnSameLine", "Brace Position", false, false),
                FormatterRuleDTO("enforceSingleSpace", "Single Space", true, true),
                FormatterRuleDTO("spaceAroundOperators", "Operator Space", false, false),
            )

        val mapper = DefaultFormatterConfigMapper()
        val dto: FormatConfigDTO = mapper.rulesToConfigDTO(rules)

        assertEquals(true, dto.spaceBeforeColon)
        assertEquals(false, dto.spaceAfterColon)
        assertEquals(true, dto.spaceAroundAssignment)
        assertEquals(2, dto.blankLinesAfterPrintln)
        assertEquals(6, dto.indentSize)
        assertEquals(false, dto.ifBraceOnSameLine)
        assertEquals(true, dto.enforceSingleSpace)
        assertEquals(false, dto.spaceAroundOperators)
    }
}
