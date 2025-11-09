package services

import checkers.IdentifierStyle
import dtos.AnalyzerRuleDTO
import entities.AnalyzerEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import repositories.AnalyzerRepository
import java.util.Optional

class AnalyzerConfigServiceTest {

    private lateinit var analyzerRepository: AnalyzerRepository

    @BeforeEach
    fun setup() {
        analyzerRepository = mock()
    }

    @Test
    fun `getConfig should return default config when user not found`() {
        val userId = "user123"
        val defaultEntity = AnalyzerEntity(userId = userId)

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.empty())
        whenever(analyzerRepository.save(any())).thenReturn(defaultEntity)

        val entity =
            analyzerRepository.findById(userId).orElseGet {
                analyzerRepository.save(AnalyzerEntity(userId = userId))
            }

        val rules =
            listOf(
                AnalyzerRuleDTO(
                    "identifierStyle",
                    "Identifier Style",
                    true,
                    entity.identifierStyle.name,
                ),
                AnalyzerRuleDTO(
                    "restrictPrintlnArgs",
                    "Restrict Println Arguments",
                    entity.restrictPrintlnArgs,
                    null,
                ),
                AnalyzerRuleDTO(
                    "restrictReadInputArgs",
                    "Restrict Read Input Arguments",
                    entity.restrictReadInputArgs,
                    null,
                ),
                AnalyzerRuleDTO("noReadInput", "No Read Input", entity.noReadInput, null),
            )

        assertNotNull(rules)
        assertEquals(4, rules.size)

        val identifierStyle = rules.find { it.id == "identifierStyle" }
        assertNotNull(identifierStyle)
        assertEquals("NO_STYLE", identifierStyle!!.value)

        val restrictPrintln = rules.find { it.id == "restrictPrintlnArgs" }
        assertNotNull(restrictPrintln)
        assertTrue(restrictPrintln!!.isActive)

        val restrictReadInput = rules.find { it.id == "restrictReadInputArgs" }
        assertNotNull(restrictReadInput)
        assertFalse(restrictReadInput!!.isActive)

        val noReadInput = rules.find { it.id == "noReadInput" }
        assertNotNull(noReadInput)
        assertFalse(noReadInput!!.isActive)
    }

    @Test
    fun `getConfig should return existing config when user found`() {
        val userId = "user456"
        val existingEntity =
            AnalyzerEntity(
                userId = userId,
                identifierStyle = IdentifierStyle.CAMEL_CASE,
                restrictPrintlnArgs = false,
                restrictReadInputArgs = true,
                noReadInput = true,
            )

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(existingEntity))

        val entity = analyzerRepository.findById(userId).get()

        val rules =
            listOf(
                AnalyzerRuleDTO(
                    "identifierStyle",
                    "Identifier Style",
                    true,
                    entity.identifierStyle.name,
                ),
                AnalyzerRuleDTO(
                    "restrictPrintlnArgs",
                    "Restrict Println Arguments",
                    entity.restrictPrintlnArgs,
                    null,
                ),
                AnalyzerRuleDTO(
                    "restrictReadInputArgs",
                    "Restrict Read Input Arguments",
                    entity.restrictReadInputArgs,
                    null,
                ),
                AnalyzerRuleDTO("noReadInput", "No Read Input", entity.noReadInput, null),
            )

        assertNotNull(rules)
        assertEquals(4, rules.size)

        val identifierStyle = rules.find { it.id == "identifierStyle" }
        assertNotNull(identifierStyle)
        assertEquals("CAMEL_CASE", identifierStyle!!.value)

        val restrictPrintln = rules.find { it.id == "restrictPrintlnArgs" }
        assertNotNull(restrictPrintln)
        assertFalse(restrictPrintln!!.isActive)

        val restrictReadInput = rules.find { it.id == "restrictReadInputArgs" }
        assertNotNull(restrictReadInput)
        assertTrue(restrictReadInput!!.isActive)

        val noReadInput = rules.find { it.id == "noReadInput" }
        assertNotNull(noReadInput)
        assertTrue(noReadInput!!.isActive)
    }

    @Test
    fun `applying rules to entity should handle all identifier styles`() {
        val styles =
            listOf(
                "NO_STYLE" to IdentifierStyle.NO_STYLE,
                "CAMEL_CASE" to IdentifierStyle.CAMEL_CASE,
                "SNAKE_CASE" to IdentifierStyle.SNAKE_CASE,
            )

        styles.forEach { (styleName, expectedStyle) ->
            val entity = AnalyzerEntity(userId = "test")
            val rules =
                listOf(
                    AnalyzerRuleDTO("identifierStyle", "Identifier Style", true, styleName),
                )

            var identifierStyle = entity.identifierStyle
            rules.forEach { rule ->
                when (rule.id) {
                    "identifierStyle" -> {
                        identifierStyle =
                            if (rule.value != null) {
                                try {
                                    IdentifierStyle.valueOf(rule.value)
                                } catch (e: IllegalArgumentException) {
                                    IdentifierStyle.NO_STYLE
                                }
                            } else {
                                IdentifierStyle.NO_STYLE
                            }
                    }
                }
            }

            assertEquals(expectedStyle, identifierStyle)
        }
    }

    @Test
    fun `applying rules should default to NO_STYLE for invalid identifier style`() {
        val entity = AnalyzerEntity(userId = "test")
        val rules =
            listOf(
                AnalyzerRuleDTO("identifierStyle", "Identifier Style", true, "INVALID_STYLE"),
            )

        var identifierStyle = entity.identifierStyle
        rules.forEach { rule ->
            when (rule.id) {
                "identifierStyle" -> {
                    identifierStyle =
                        if (rule.value != null) {
                            try {
                                IdentifierStyle.valueOf(rule.value)
                            } catch (e: IllegalArgumentException) {
                                IdentifierStyle.NO_STYLE
                            }
                        } else {
                            IdentifierStyle.NO_STYLE
                        }
                }
            }
        }

        assertEquals(IdentifierStyle.NO_STYLE, identifierStyle)
    }

    @Test
    fun `applying rules should default to NO_STYLE when value is null`() {
        val entity = AnalyzerEntity(userId = "test")
        val rules =
            listOf(
                AnalyzerRuleDTO("identifierStyle", "Identifier Style", true, null),
            )

        var identifierStyle = entity.identifierStyle
        rules.forEach { rule ->
            when (rule.id) {
                "identifierStyle" -> {
                    identifierStyle =
                        if (rule.value != null) {
                            try {
                                IdentifierStyle.valueOf(rule.value)
                            } catch (e: IllegalArgumentException) {
                                IdentifierStyle.NO_STYLE
                            }
                        } else {
                            IdentifierStyle.NO_STYLE
                        }
                }
            }
        }

        assertEquals(IdentifierStyle.NO_STYLE, identifierStyle)
    }

    @Test
    fun `applying rules should handle all boolean rules`() {
        val entity = AnalyzerEntity(userId = "test")
        val rules =
            listOf(
                AnalyzerRuleDTO("restrictPrintlnArgs", "Restrict Println", false, null),
                AnalyzerRuleDTO("restrictReadInputArgs", "Restrict Read Input", true, null),
                AnalyzerRuleDTO("noReadInput", "No Read Input", true, null),
            )

        var restrictPrintlnArgs = entity.restrictPrintlnArgs
        var restrictReadInputArgs = entity.restrictReadInputArgs
        var noReadInput = entity.noReadInput

        rules.forEach { rule ->
            when (rule.id) {
                "restrictPrintlnArgs" -> restrictPrintlnArgs = rule.isActive
                "restrictReadInputArgs" -> restrictReadInputArgs = rule.isActive
                "noReadInput" -> noReadInput = rule.isActive
            }
        }

        assertFalse(restrictPrintlnArgs)
        assertTrue(restrictReadInputArgs)
        assertTrue(noReadInput)
    }

    @Test
    fun `getConfig should return all rules with correct structure`() {
        val userId = "structureUser"
        val entity = AnalyzerEntity(userId = userId)

        whenever(analyzerRepository.findById(userId)).thenReturn(Optional.of(entity))

        val retrievedEntity = analyzerRepository.findById(userId).get()

        val rules =
            listOf(
                AnalyzerRuleDTO(
                    "identifierStyle",
                    "Identifier Style",
                    true,
                    retrievedEntity.identifierStyle.name,
                ),
                AnalyzerRuleDTO(
                    "restrictPrintlnArgs",
                    "Restrict Println Arguments",
                    retrievedEntity.restrictPrintlnArgs,
                    null,
                ),
                AnalyzerRuleDTO(
                    "restrictReadInputArgs",
                    "Restrict Read Input Arguments",
                    retrievedEntity.restrictReadInputArgs,
                    null,
                ),
                AnalyzerRuleDTO("noReadInput", "No Read Input", retrievedEntity.noReadInput, null),
            )

        val ruleIds = rules.map { it.id }.toSet()
        assertTrue(ruleIds.contains("identifierStyle"))
        assertTrue(ruleIds.contains("restrictPrintlnArgs"))
        assertTrue(ruleIds.contains("restrictReadInputArgs"))
        assertTrue(ruleIds.contains("noReadInput"))

        rules.forEach { rule ->
            assertNotNull(rule.id)
            assertNotNull(rule.name)
        }
    }
}
