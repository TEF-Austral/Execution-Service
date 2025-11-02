package services

import dtos.FormatConfigDTO
import entities.FormatterConfigEntity
import org.springframework.stereotype.Service
import repositories.FormatterConfigRepository

@Service
class FormatterConfigService(
    private val formatterConfigRepository: FormatterConfigRepository,
) {

    fun getConfig(userId: String): FormatConfigDTO {
        val config =
            formatterConfigRepository
                .findByUserId(userId)
                .orElseGet {
                    // Create default config if not exists
                    val defaultConfig = FormatterConfigEntity(userId = userId)
                    formatterConfigRepository.save(defaultConfig)
                }

        return config.toDTO()
    }

    fun updateConfig(
        userId: String,
        configDTO: FormatConfigDTO,
    ): FormatConfigDTO {
        val existing = formatterConfigRepository.findByUserId(userId)

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
        return saved.toDTO()
    }

    private fun FormatterConfigEntity.toDTO() =
        FormatConfigDTO(
            spaceBeforeColon = spaceBeforeColon,
            spaceAfterColon = spaceAfterColon,
            spaceAroundAssignment = spaceAroundAssignment,
            blankLinesAfterPrintln = blankLinesAfterPrintln,
            indentSize = indentSize,
            ifBraceOnSameLine = ifBraceOnSameLine,
            enforceSingleSpace = enforceSingleSpace,
            spaceAroundOperators = spaceAroundOperators,
        )
}
