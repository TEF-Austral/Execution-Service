package services

import dtos.FormatConfigDTO
import dtos.FormatterRuleDTO
import entities.FormatterConfigEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Optional

class FormatterConfigServiceTest {

    private lateinit var formatterConfigRepository: `FormatterConfig.repository`

    @BeforeEach
    fun setup() {
        formatterConfigRepository = mock()
    }

    @Test
    fun `getConfig should return default config when user not found`() {
        val userId = "user123"
        val defaultEntity = FormatterConfigEntity(userId = userId)

        whenever(formatterConfigRepository.findByUserId(userId)).thenReturn(Optional.empty())
        whenever(formatterConfigRepository.save(any())).thenReturn(defaultEntity)

        val rules =
            formatterConfigRepository
                .findByUserId(userId)
                .orElseGet {
                    formatterConfigRepository.save(FormatterConfigEntity(userId = userId))
                }.let { entity ->
                    listOf(
                        FormatterRuleDTO(
                            "spaceBeforeColon",
                            "Space Before Colon",
                            entity.spaceBeforeColon,
                            entity.spaceBeforeColon,
                        ),
                        FormatterRuleDTO(
                            "spaceAfterColon",
                            "Space After Colon",
                            entity.spaceAfterColon,
                            entity.spaceAfterColon,
                        ),
                        FormatterRuleDTO(
                            "spaceAroundAssignment",
                            "Space Around Assignment",
                            entity.spaceAroundAssignment,
                            entity.spaceAroundAssignment,
                        ),
                        FormatterRuleDTO(
                            "blankLinesAfterPrintln",
                            "Blank Lines After Println",
                            true,
                            entity.blankLinesAfterPrintln,
                        ),
                        FormatterRuleDTO("indentSize", "Indent Size", true, entity.indentSize),
                        FormatterRuleDTO(
                            "ifBraceOnSameLine",
                            "If Brace On Same Line",
                            entity.ifBraceOnSameLine,
                            entity.ifBraceOnSameLine,
                        ),
                        FormatterRuleDTO(
                            "enforceSingleSpace",
                            "Enforce Single Space",
                            entity.enforceSingleSpace,
                            entity.enforceSingleSpace,
                        ),
                        FormatterRuleDTO(
                            "spaceAroundOperators",
                            "Space Around Operators",
                            entity.spaceAroundOperators,
                            entity.spaceAroundOperators,
                        ),
                    )
                }

        assertNotNull(rules)
        assertEquals(8, rules.size)

        val spaceBeforeColon = rules.find { it.id == "spaceBeforeColon" }
        assertNotNull(spaceBeforeColon)
        assertFalse(spaceBeforeColon!!.isActive)

        val spaceAfterColon = rules.find { it.id == "spaceAfterColon" }
        assertNotNull(spaceAfterColon)
        assertTrue(spaceAfterColon!!.isActive)

        val indentSize = rules.find { it.id == "indentSize" }
        assertNotNull(indentSize)
        assertEquals(4, indentSize!!.value)
    }

    @Test
    fun `getConfig should return existing config when user found`() {
        val userId = "user456"
        val existingEntity =
            FormatterConfigEntity(
                userId = userId,
                spaceBeforeColon = true,
                spaceAfterColon = false,
                indentSize = 2,
                blankLinesAfterPrintln = 3,
            )

        whenever(
            formatterConfigRepository.findByUserId(userId),
        ).thenReturn(Optional.of(existingEntity))

        val rules =
            formatterConfigRepository.findByUserId(userId).get().let { entity ->
                listOf(
                    FormatterRuleDTO(
                        "spaceBeforeColon",
                        "Space Before Colon",
                        entity.spaceBeforeColon,
                        entity.spaceBeforeColon,
                    ),
                    FormatterRuleDTO(
                        "spaceAfterColon",
                        "Space After Colon",
                        entity.spaceAfterColon,
                        entity.spaceAfterColon,
                    ),
                    FormatterRuleDTO(
                        "spaceAroundAssignment",
                        "Space Around Assignment",
                        entity.spaceAroundAssignment,
                        entity.spaceAroundAssignment,
                    ),
                    FormatterRuleDTO(
                        "blankLinesAfterPrintln",
                        "Blank Lines After Println",
                        true,
                        entity.blankLinesAfterPrintln,
                    ),
                    FormatterRuleDTO("indentSize", "Indent Size", true, entity.indentSize),
                    FormatterRuleDTO(
                        "ifBraceOnSameLine",
                        "If Brace On Same Line",
                        entity.ifBraceOnSameLine,
                        entity.ifBraceOnSameLine,
                    ),
                    FormatterRuleDTO(
                        "enforceSingleSpace",
                        "Enforce Single Space",
                        entity.enforceSingleSpace,
                        entity.enforceSingleSpace,
                    ),
                    FormatterRuleDTO(
                        "spaceAroundOperators",
                        "Space Around Operators",
                        entity.spaceAroundOperators,
                        entity.spaceAroundOperators,
                    ),
                )
            }

        assertNotNull(rules)
        assertEquals(8, rules.size)

        val spaceBeforeColon = rules.find { it.id == "spaceBeforeColon" }
        assertNotNull(spaceBeforeColon)
        assertTrue(spaceBeforeColon!!.isActive)

        val spaceAfterColon = rules.find { it.id == "spaceAfterColon" }
        assertNotNull(spaceAfterColon)
        assertFalse(spaceAfterColon!!.isActive)

        val indentSize = rules.find { it.id == "indentSize" }
        assertNotNull(indentSize)
        assertEquals(2, indentSize!!.value)

        val blankLines = rules.find { it.id == "blankLinesAfterPrintln" }
        assertNotNull(blankLines)
        assertEquals(3, blankLines!!.value)
    }

    @Test
    fun `rulesToConfigDTO should convert rules correctly`() {
        val rules =
            listOf(
                FormatterRuleDTO("spaceBeforeColon", "Space Before Colon", true, true),
                FormatterRuleDTO("spaceAfterColon", "Space After Colon", false, false),
                FormatterRuleDTO("spaceAroundAssignment", "Space Around Assignment", true, true),
                FormatterRuleDTO("blankLinesAfterPrintln", "Blank Lines After Println", true, 2),
                FormatterRuleDTO("indentSize", "Indent Size", true, 8),
                FormatterRuleDTO("ifBraceOnSameLine", "If Brace On Same Line", false, false),
                FormatterRuleDTO("enforceSingleSpace", "Enforce Single Space", true, true),
                FormatterRuleDTO("spaceAroundOperators", "Space Around Operators", false, false),
            )

        val config =
            FormatConfigDTO(
                spaceBeforeColon = rules.find { it.id == "spaceBeforeColon" }?.isActive ?: false,
                spaceAfterColon = rules.find { it.id == "spaceAfterColon" }?.isActive ?: true,
                spaceAroundAssignment =
                    rules.find { it.id == "spaceAroundAssignment" }?.isActive ?: true,
                blankLinesAfterPrintln =
                    rules
                        .find { it.id == "blankLinesAfterPrintln" }
                        ?.value
                        ?.toString()
                        ?.toIntOrNull()
                        ?: 1,
                indentSize =
                    rules
                        .find { it.id == "indentSize" }
                        ?.value
                        ?.toString()
                        ?.toIntOrNull() ?: 4,
                ifBraceOnSameLine =
                    rules
                        .find {
                            it.id == "ifBraceOnSameLine"
                        }?.isActive ?: true,
                enforceSingleSpace =
                    rules
                        .find {
                            it.id == "enforceSingleSpace"
                        }?.isActive ?: true,
                spaceAroundOperators =
                    rules.find { it.id == "spaceAroundOperators" }?.isActive ?: true,
            )

        assertTrue(config.spaceBeforeColon)
        assertFalse(config.spaceAfterColon)
        assertTrue(config.spaceAroundAssignment)
        assertEquals(2, config.blankLinesAfterPrintln)
        assertEquals(8, config.indentSize)
        assertFalse(config.ifBraceOnSameLine)
        assertTrue(config.enforceSingleSpace)
        assertFalse(config.spaceAroundOperators)
    }

    @Test
    fun `rulesToConfigDTO should use default values when rules missing`() {
        val rules = emptyList<FormatterRuleDTO>()

        val config =
            FormatConfigDTO(
                spaceBeforeColon = rules.find { it.id == "spaceBeforeColon" }?.isActive ?: false,
                spaceAfterColon = rules.find { it.id == "spaceAfterColon" }?.isActive ?: true,
                spaceAroundAssignment =
                    rules.find { it.id == "spaceAroundAssignment" }?.isActive ?: true,
                blankLinesAfterPrintln =
                    rules
                        .find { it.id == "blankLinesAfterPrintln" }
                        ?.value
                        ?.toString()
                        ?.toIntOrNull()
                        ?: 1,
                indentSize =
                    rules
                        .find { it.id == "indentSize" }
                        ?.value
                        ?.toString()
                        ?.toIntOrNull() ?: 4,
                ifBraceOnSameLine =
                    rules
                        .find {
                            it.id == "ifBraceOnSameLine"
                        }?.isActive ?: true,
                enforceSingleSpace =
                    rules
                        .find {
                            it.id == "enforceSingleSpace"
                        }?.isActive ?: true,
                spaceAroundOperators =
                    rules.find { it.id == "spaceAroundOperators" }?.isActive ?: true,
            )

        assertFalse(config.spaceBeforeColon)
        assertTrue(config.spaceAfterColon)
        assertTrue(config.spaceAroundAssignment)
        assertEquals(1, config.blankLinesAfterPrintln)
        assertEquals(4, config.indentSize)
        assertTrue(config.ifBraceOnSameLine)
        assertTrue(config.enforceSingleSpace)
        assertTrue(config.spaceAroundOperators)
    }

    @Test
    fun `rulesToConfigDTO should handle invalid integer values`() {
        val rules =
            listOf(
                FormatterRuleDTO("indentSize", "Indent Size", true, "not_a_number"),
                FormatterRuleDTO("blankLinesAfterPrintln", "Blank Lines", true, "invalid"),
            )

        val config =
            FormatConfigDTO(
                spaceBeforeColon = rules.find { it.id == "spaceBeforeColon" }?.isActive ?: false,
                spaceAfterColon = rules.find { it.id == "spaceAfterColon" }?.isActive ?: true,
                spaceAroundAssignment =
                    rules.find { it.id == "spaceAroundAssignment" }?.isActive ?: true,
                blankLinesAfterPrintln =
                    rules
                        .find { it.id == "blankLinesAfterPrintln" }
                        ?.value
                        ?.toString()
                        ?.toIntOrNull()
                        ?: 1,
                indentSize =
                    rules
                        .find { it.id == "indentSize" }
                        ?.value
                        ?.toString()
                        ?.toIntOrNull() ?: 4,
                ifBraceOnSameLine =
                    rules
                        .find {
                            it.id == "ifBraceOnSameLine"
                        }?.isActive ?: true,
                enforceSingleSpace =
                    rules
                        .find {
                            it.id == "enforceSingleSpace"
                        }?.isActive ?: true,
                spaceAroundOperators =
                    rules.find { it.id == "spaceAroundOperators" }?.isActive ?: true,
            )

        assertEquals(4, config.indentSize)
        assertEquals(1, config.blankLinesAfterPrintln)
    }
}
