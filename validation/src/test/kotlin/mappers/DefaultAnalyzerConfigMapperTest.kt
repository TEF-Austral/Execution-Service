package mappers

import checkers.IdentifierStyle
import dtos.AnalyzerRuleDTO
import entities.AnalyzerEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DefaultAnalyzerConfigMapperTest {

    private val mapper = DefaultAnalyzerConfigMapper()

    @Test
    fun `entityToRules should convert entity to rules correctly`() {
        val entity =
            AnalyzerEntity(
                userId = "user123",
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = false,
                noReadInput = true,
            )

        val rules = mapper.entityToRules(entity)

        assertEquals(4, rules.size)

        val identifierRule = rules.find { it.id == "identifierStyle" }
        assertEquals("CAMEL_CASE", identifierRule?.value)

        val printlnRule = rules.find { it.id == "restrictPrintlnArgs" }
        assertTrue(printlnRule?.isActive == true)

        val readInputRule = rules.find { it.id == "restrictReadInputArgs" }
        assertFalse(readInputRule?.isActive == true)

        val noReadInputRule = rules.find { it.id == "noReadInput" }
        assertTrue(noReadInputRule?.isActive == true)
    }

    @Test
    fun `entityToConfig should convert entity to config correctly`() {
        val entity =
            AnalyzerEntity(
                userId = "user456",
                identifierStyle = IdentifierStyle.SNAKE_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = true,
                noReadInput = false,
            )

        val config = mapper.entityToConfig(entity)

        assertEquals(IdentifierStyle.SNAKE_CASE, config.identifierStyle)
        assertFalse(config.restrictPrintlnArgs)
        assertTrue(config.restrictReadInputArgs)
        assertFalse(config.noReadInput)
    }

    @Test
    fun `rulesToEntity should create entity from rules correctly`() {
        val rules =
            listOf(
                AnalyzerRuleDTO("identifierStyle", "Identifier Style", true, "CAMEL_CASE"),
                AnalyzerRuleDTO("restrictPrintlnArgs", "Restrict Println", true, null),
                AnalyzerRuleDTO("restrictReadInputArgs", "Restrict Read Input", false, null),
                AnalyzerRuleDTO("noReadInput", "No Read Input", true, null),
            )

        val entity = mapper.rulesToEntity("user789", rules)

        assertEquals("user789", entity.userId)
        assertEquals(IdentifierStyle.CAMEL_CASE, entity.identifierStyle)
        assertTrue(entity.restrictPrintlnArgs)
        assertFalse(entity.restrictReadInputArgs)
        assertTrue(entity.noReadInput)
    }

    @Test
    fun `rulesToEntity should handle invalid identifier style`() {
        val rules =
            listOf(
                AnalyzerRuleDTO("identifierStyle", "Identifier Style", true, "INVALID_STYLE"),
            )

        val entity = mapper.rulesToEntity("user", rules)

        assertEquals(IdentifierStyle.NO_STYLE, entity.identifierStyle)
    }

    @Test
    fun `rulesToEntity should handle null identifier style value`() {
        val rules =
            listOf(
                AnalyzerRuleDTO("identifierStyle", "Identifier Style", true, null),
            )

        val entity = mapper.rulesToEntity("user", rules)

        assertEquals(IdentifierStyle.NO_STYLE, entity.identifierStyle)
    }

    @Test
    fun `rulesToEntity should use defaults when rules are missing`() {
        val rules = emptyList<AnalyzerRuleDTO>()

        val entity = mapper.rulesToEntity("user", rules)

        assertEquals(IdentifierStyle.NO_STYLE, entity.identifierStyle)
        assertFalse(entity.restrictPrintlnArgs)
        assertFalse(entity.restrictReadInputArgs)
        assertFalse(entity.noReadInput)
    }

    @Test
    fun `updateEntity should update entity correctly`() {
        val original =
            AnalyzerEntity(
                userId = "user",
                identifierStyle = IdentifierStyle.NO_STYLE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = false,
                noReadInput = false,
            )

        val rules =
            listOf(
                AnalyzerRuleDTO("identifierStyle", "Identifier Style", true, "SNAKE_CASE"),
                AnalyzerRuleDTO("restrictPrintlnArgs", "Restrict Println", true, null),
                AnalyzerRuleDTO("restrictReadInputArgs", "Restrict Read Input", true, null),
                AnalyzerRuleDTO("noReadInput", "No Read Input", true, null),
            )

        val updated = mapper.updateEntity(original, rules)

        assertEquals("user", updated.userId)
        assertEquals(IdentifierStyle.SNAKE_CASE, updated.identifierStyle)
        assertTrue(updated.restrictPrintlnArgs)
        assertTrue(updated.restrictReadInputArgs)
        assertTrue(updated.noReadInput)
    }

    @Test
    fun `updateEntity should preserve userId`() {
        val original =
            AnalyzerEntity(
                userId = "originalUser",
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = true,
                restrictReadInputArgs = true,
                noReadInput = true,
            )

        val rules =
            listOf(
                AnalyzerRuleDTO("identifierStyle", "Identifier Style", true, "SNAKE_CASE"),
            )

        val updated = mapper.updateEntity(original, rules)

        assertEquals("originalUser", updated.userId)
        assertEquals(IdentifierStyle.SNAKE_CASE, updated.identifierStyle)
    }

    @Test
    fun `entityToRules should handle all identifier styles`() {
        val styles =
            listOf(
                IdentifierStyle.NO_STYLE,
                IdentifierStyle.CAMEL_CASE,
                IdentifierStyle.SNAKE_CASE,
            )

        styles.forEach { style ->
            val entity =
                AnalyzerEntity(
                    userId = "user",
                    identifierStyle = style,
                    restrictPrintlnArgs = false,
                    restrictReadInputArgs = false,
                    noReadInput = false,
                )

            val rules = mapper.entityToRules(entity)
            val identifierRule = rules.find { it.id == "identifierStyle" }

            assertEquals(style.name, identifierRule?.value)
        }
    }

    @Test
    fun `rulesToEntity should handle all boolean combinations`() {
        val testCases =
            listOf(
                Triple(true, true, true),
                Triple(true, true, false),
                Triple(true, false, true),
                Triple(true, false, false),
                Triple(false, true, true),
                Triple(false, true, false),
                Triple(false, false, true),
                Triple(false, false, false),
            )

        testCases.forEach { (restrictPrintln, restrictReadInput, noReadInput) ->
            val rules =
                listOf(
                    AnalyzerRuleDTO(
                        "restrictPrintlnArgs",
                        "Restrict Println",
                        restrictPrintln,
                        null,
                    ),
                    AnalyzerRuleDTO(
                        "restrictReadInputArgs",
                        "Restrict Read Input",
                        restrictReadInput,
                        null,
                    ),
                    AnalyzerRuleDTO("noReadInput", "No Read Input", noReadInput, null),
                )

            val entity = mapper.rulesToEntity("user", rules)

            assertEquals(restrictPrintln, entity.restrictPrintlnArgs)
            assertEquals(restrictReadInput, entity.restrictReadInputArgs)
            assertEquals(noReadInput, entity.noReadInput)
        }
    }
}
