package mappers

import dtos.FormatConfigDTO
import dtos.FormatterRuleDTO
import entities.FormatterConfigEntity
import formatter.config.FormatConfig
import org.springframework.stereotype.Component

@Component
class DefaultFormatterConfigMapper : FormatterConfigMapper {
    override fun entityToRules(entity: FormatterConfigEntity): List<FormatterRuleDTO> =
        listOf(
            FormatterRuleDTO(
                id = "spaceBeforeColon",
                name = "Space Before Colon",
                isActive = entity.spaceBeforeColon,
                value = entity.spaceBeforeColon,
            ),
            FormatterRuleDTO(
                id = "spaceAfterColon",
                name = "Space After Colon",
                isActive = entity.spaceAfterColon,
                value = entity.spaceAfterColon,
            ),
            FormatterRuleDTO(
                id = "spaceAroundAssignment",
                name = "Space Around Assignment",
                isActive = entity.spaceAroundAssignment,
                value = entity.spaceAroundAssignment,
            ),
            FormatterRuleDTO(
                id = "blankLinesAfterPrintln",
                name = "Blank Lines After Println",
                isActive = true,
                value = entity.blankLinesAfterPrintln,
            ),
            FormatterRuleDTO(
                id = "indentSize",
                name = "Indent Size",
                isActive = true,
                value = entity.indentSize,
            ),
            FormatterRuleDTO(
                id = "ifBraceOnSameLine",
                name = "If Brace On Same Line",
                isActive = entity.ifBraceOnSameLine,
                value = entity.ifBraceOnSameLine,
            ),
            FormatterRuleDTO(
                id = "enforceSingleSpace",
                name = "Enforce Single Space",
                isActive = entity.enforceSingleSpace,
                value = entity.enforceSingleSpace,
            ),
            FormatterRuleDTO(
                id = "spaceAroundOperators",
                name = "Space Around Operators",
                isActive = entity.spaceAroundOperators,
                value = entity.spaceAroundOperators,
            ),
        )

    override fun rulesToConfigDTO(rules: List<FormatterRuleDTO>): FormatConfigDTO =
        FormatConfigDTO(
            spaceBeforeColon = findRuleValue(rules, "spaceBeforeColon", false),
            spaceAfterColon = findRuleValue(rules, "spaceAfterColon", true),
            spaceAroundAssignment = findRuleValue(rules, "spaceAroundAssignment", true),
            blankLinesAfterPrintln = findRuleIntValue(rules, "blankLinesAfterPrintln", 1),
            indentSize = findRuleIntValue(rules, "indentSize", 4),
            ifBraceOnSameLine = findRuleValue(rules, "ifBraceOnSameLine", true),
            enforceSingleSpace = findRuleValue(rules, "enforceSingleSpace", true),
            spaceAroundOperators = findRuleValue(rules, "spaceAroundOperators", true),
        )

    override fun dtoToFormatConfig(configDTO: FormatConfigDTO): FormatConfig =
        FormatConfig(
            spaceBeforeColon = configDTO.spaceBeforeColon,
            spaceAfterColon = configDTO.spaceAfterColon,
            spaceAroundAssignment = configDTO.spaceAroundAssignment,
            blankLinesAfterPrintln = configDTO.blankLinesAfterPrintln,
            indentSize = configDTO.indentSize,
            ifBraceOnSameLine = configDTO.ifBraceOnSameLine,
            enforceSingleSpace = configDTO.enforceSingleSpace,
            spaceAroundOperators = configDTO.spaceAroundOperators,
        )

    override fun rulesToEntity(
        userId: String,
        rules: List<FormatterRuleDTO>,
    ): FormatterConfigEntity {
        val configDTO = rulesToConfigDTO(rules)
        return FormatterConfigEntity(
            userId = userId,
            spaceBeforeColon = configDTO.spaceBeforeColon,
            spaceAfterColon = configDTO.spaceAfterColon,
            spaceAroundAssignment = configDTO.spaceAroundAssignment,
            blankLinesAfterPrintln = configDTO.blankLinesAfterPrintln,
            indentSize = configDTO.indentSize,
            ifBraceOnSameLine = configDTO.ifBraceOnSameLine,
            enforceSingleSpace = configDTO.enforceSingleSpace,
            spaceAroundOperators = configDTO.spaceAroundOperators,
        )
    }

    override fun updateEntity(
        entity: FormatterConfigEntity,
        rules: List<FormatterRuleDTO>,
    ): FormatterConfigEntity {
        val configDTO = rulesToConfigDTO(rules)
        return entity.copy(
            spaceBeforeColon = configDTO.spaceBeforeColon,
            spaceAfterColon = configDTO.spaceAfterColon,
            spaceAroundAssignment = configDTO.spaceAroundAssignment,
            blankLinesAfterPrintln = configDTO.blankLinesAfterPrintln,
            indentSize = configDTO.indentSize,
            ifBraceOnSameLine = configDTO.ifBraceOnSameLine,
            enforceSingleSpace = configDTO.enforceSingleSpace,
            spaceAroundOperators = configDTO.spaceAroundOperators,
        )
    }

    private fun findRuleValue(
        rules: List<FormatterRuleDTO>,
        ruleId: String,
        default: Boolean,
    ): Boolean = rules.find { it.id == ruleId }?.isActive ?: default

    private fun findRuleIntValue(
        rules: List<FormatterRuleDTO>,
        ruleId: String,
        default: Int,
    ): Int =
        rules
            .find { it.id == ruleId }
            ?.value
            ?.toString()
            ?.toIntOrNull() ?: default
}
