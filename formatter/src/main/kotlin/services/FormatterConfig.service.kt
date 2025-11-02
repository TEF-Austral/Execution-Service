package services

import dtos.FormatConfigDTO
import dtos.RuleDTO
import entities.FormatterConfigEntity
import org.springframework.stereotype.Service
import repositories.FormatterConfigRepository

@Service
class FormatterConfigService(
    private val formatterConfigRepository: FormatterConfigRepository,
) {

    fun getConfig(userId: String): List<RuleDTO> {
        val config =
            formatterConfigRepository
                .findByUserId(userId)
                .orElseGet {
                    val defaultConfig = FormatterConfigEntity(userId = userId)
                    formatterConfigRepository.save(defaultConfig)
                }

        return config.toRules()
    }

    fun updateConfig(
        userId: String,
        rules: List<RuleDTO>,
    ): List<RuleDTO> {
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
        return saved.toRules()
    }

    private fun FormatterConfigEntity.toRules(): List<RuleDTO> =
        listOf(
            RuleDTO(
                id = "spaceBeforeColon",
                name = "Space Before Colon",
                isActive = spaceBeforeColon,
                value = null,
            ),
            RuleDTO(
                id = "spaceAfterColon",
                name = "Space After Colon",
                isActive = spaceAfterColon,
                value = null,
            ),
            RuleDTO(
                id = "spaceAroundAssignment",
                name = "Space Around Assignment",
                isActive = spaceAroundAssignment,
                value = null,
            ),
            RuleDTO(
                id = "blankLinesAfterPrintln",
                name = "Blank Lines After Println",
                isActive = true,
                value = blankLinesAfterPrintln,
            ),
            RuleDTO(
                id = "indentSize",
                name = "Indent Size",
                isActive = true,
                value = indentSize,
            ),
            RuleDTO(
                id = "ifBraceOnSameLine",
                name = "If Brace On Same Line",
                isActive = ifBraceOnSameLine,
                value = null,
            ),
            RuleDTO(
                id = "enforceSingleSpace",
                name = "Enforce Single Space",
                isActive = enforceSingleSpace,
                value = null,
            ),
            RuleDTO(
                id = "spaceAroundOperators",
                name = "Space Around Operators",
                isActive = spaceAroundOperators,
                value = null,
            ),
        )

    private fun rulesToConfigDTO(rules: List<RuleDTO>): FormatConfigDTO =
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
