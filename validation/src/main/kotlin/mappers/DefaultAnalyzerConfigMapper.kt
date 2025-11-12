package mappers

import checkers.IdentifierStyle
import config.AnalyzerConfig
import dtos.AnalyzerRuleDTO
import entities.AnalyzerEntity
import org.springframework.stereotype.Component

@Component
class DefaultAnalyzerConfigMapper : AnalyzerConfigMapper {

    override fun entityToRules(entity: AnalyzerEntity): List<AnalyzerRuleDTO> =
        listOf(
            AnalyzerRuleDTO(
                id = "identifierStyle",
                name = "Identifier Style",
                isActive = true,
                value = entity.identifierStyle.name,
            ),
            AnalyzerRuleDTO(
                id = "restrictPrintlnArgs",
                name = "Restrict Println Arguments",
                isActive = entity.restrictPrintlnArgs,
                value = null,
            ),
            AnalyzerRuleDTO(
                id = "restrictReadInputArgs",
                name = "Restrict Read Input Arguments",
                isActive = entity.restrictReadInputArgs,
                value = null,
            ),
            AnalyzerRuleDTO(
                id = "noReadInput",
                name = "No Read Input",
                isActive = entity.noReadInput,
                value = null,
            ),
        )

    override fun entityToConfig(entity: AnalyzerEntity): AnalyzerConfig =
        AnalyzerConfig(
            identifierStyle = entity.identifierStyle,
            restrictPrintlnArgs = entity.restrictPrintlnArgs,
            restrictReadInputArgs = entity.restrictReadInputArgs,
            noReadInput = entity.noReadInput,
        )

    override fun rulesToEntity(
        userId: String,
        rules: List<AnalyzerRuleDTO>,
    ): AnalyzerEntity =
        AnalyzerEntity(
            userId = userId,
            identifierStyle = extractIdentifierStyle(rules),
            restrictPrintlnArgs = extractBooleanRule(rules, "restrictPrintlnArgs", false),
            restrictReadInputArgs = extractBooleanRule(rules, "restrictReadInputArgs", false),
            noReadInput = extractBooleanRule(rules, "noReadInput", false),
        )

    override fun updateEntity(
        entity: AnalyzerEntity,
        rules: List<AnalyzerRuleDTO>,
    ): AnalyzerEntity =
        entity.copy(
            identifierStyle = extractIdentifierStyle(rules),
            restrictPrintlnArgs =
                extractBooleanRule(
                    rules,
                    "restrictPrintlnArgs",
                    entity.restrictPrintlnArgs,
                ),
            restrictReadInputArgs =
                extractBooleanRule(
                    rules,
                    "restrictReadInputArgs",
                    entity.restrictReadInputArgs,
                ),
            noReadInput = extractBooleanRule(rules, "noReadInput", entity.noReadInput),
        )

    private fun extractIdentifierStyle(rules: List<AnalyzerRuleDTO>): IdentifierStyle {
        val rule = rules.find { it.id == "identifierStyle" }
        return if (rule?.value != null) {
            try {
                IdentifierStyle.valueOf(rule.value)
            } catch (e: IllegalArgumentException) {
                IdentifierStyle.NO_STYLE
            }
        } else {
            IdentifierStyle.NO_STYLE
        }
    }

    private fun extractBooleanRule(
        rules: List<AnalyzerRuleDTO>,
        ruleId: String,
        default: Boolean,
    ): Boolean = rules.find { it.id == ruleId }?.isActive ?: default
}
