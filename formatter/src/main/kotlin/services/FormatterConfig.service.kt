package services

import dtos.FormatConfigDTO
import dtos.FormatterRuleDTO
import entities.FormatterConfigEntity
import events.FormattingRulesUpdatedEvent
import org.springframework.stereotype.Service
import producers.FormattingRulesUpdatedProducer
import repositories.FormatterConfigRepository

@Service
class FormatterConfigService(
    private val formatterConfigRepository: FormatterConfigRepository,
    private val rulesUpdatedProducer: FormattingRulesUpdatedProducer,
) {
    private val log = org.slf4j.LoggerFactory.getLogger(FormatterConfigService::class.java)

    fun getConfig(userId: String): List<FormatterRuleDTO> {
        log.info("Fetching formatter config for user $userId")
        val config =
            formatterConfigRepository
                .findByUserId(userId)
                .orElseGet {
                    formatterConfigRepository.save(FormatterConfigEntity(userId = userId))
                }

        val result = config.toRules()
        log.warn("Retrieved ${result.size} formatter rules for user $userId")
        return result
    }

    fun updateConfig(
        userId: String,
        rules: List<FormatterRuleDTO>,
    ): List<FormatterRuleDTO> {
        log.info("Updating formatter config for user $userId")
        val existing = formatterConfigRepository.findByUserId(userId)

        val configDTO = rulesToConfigDTO(rules)

        val config =
            if (existing.isPresent) {
                existing.get().copy(
                    spaceBeforeColon = configDTO.spaceBeforeColon,
                    spaceAfterColon = configDTO.spaceAfterColon,
                    spaceAroundAssignment = configDTO.spaceAroundAssignment,
                    blankLinesAfterPrintln = configDTO.blankLinesAfterPrintln,
                    indentSize = configDTO.indentSize,
                    ifBraceOnSameLine = configDTO.ifBraceOnSameLine,
                    enforceSingleSpace = configDTO.enforceSingleSpace,
                    spaceAroundOperators = configDTO.spaceAroundOperators,
                )
            } else {
                FormatterConfigEntity(
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

        val saved = formatterConfigRepository.save(config)
        rulesUpdatedProducer.emit(FormattingRulesUpdatedEvent(userId = userId))
        val result = saved.toRules()
        log.warn("Formatter config updated for user $userId, ${result.size} rules")
        return result
    }

    private fun FormatterConfigEntity.toRules(): List<FormatterRuleDTO> =
        listOf(
            FormatterRuleDTO(
                id = "spaceBeforeColon",
                name = "Space Before Colon",
                isActive = spaceBeforeColon,
                value = spaceBeforeColon,
            ),
            FormatterRuleDTO(
                id = "spaceAfterColon",
                name = "Space After Colon",
                isActive = spaceAfterColon,
                value = spaceAfterColon,
            ),
            FormatterRuleDTO(
                id = "spaceAroundAssignment",
                name = "Space Around Assignment",
                isActive = spaceAroundAssignment,
                value = spaceAroundAssignment,
            ),
            FormatterRuleDTO(
                id = "blankLinesAfterPrintln",
                name = "Blank Lines After Println",
                isActive = true,
                value = blankLinesAfterPrintln,
            ),
            FormatterRuleDTO(
                id = "indentSize",
                name = "Indent Size",
                isActive = true,
                value = indentSize,
            ),
            FormatterRuleDTO(
                id = "ifBraceOnSameLine",
                name = "If Brace On Same Line",
                isActive = ifBraceOnSameLine,
                value = ifBraceOnSameLine,
            ),
            FormatterRuleDTO(
                id = "enforceSingleSpace",
                name = "Enforce Single Space",
                isActive = enforceSingleSpace,
                value = enforceSingleSpace,
            ),
            FormatterRuleDTO(
                id = "spaceAroundOperators",
                name = "Space Around Operators",
                isActive = spaceAroundOperators,
                value = spaceAroundOperators,
            ),
        )

    fun rulesToConfigDTO(rules: List<FormatterRuleDTO>): FormatConfigDTO =
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
            ifBraceOnSameLine = rules.find { it.id == "ifBraceOnSameLine" }?.isActive ?: true,
            enforceSingleSpace = rules.find { it.id == "enforceSingleSpace" }?.isActive ?: true,
            spaceAroundOperators = rules.find { it.id == "spaceAroundOperators" }?.isActive ?: true,
        )
}
