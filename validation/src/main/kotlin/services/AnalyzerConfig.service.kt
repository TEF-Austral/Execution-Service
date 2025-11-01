package services

import checkers.IdentifierStyle
import dtos.RuleDTO
import entities.AnalyzerEntity
import repositories.AnalyzerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnalyzerConfigService(
    private val analyzerRepository: AnalyzerRepository,
) {

    fun getConfig(userId: String): List<RuleDTO> {
        val entity =
            analyzerRepository.findById(userId).orElse(null)
                ?: AnalyzerEntity(userId = userId)

        return entityToRules(entity)
    }

    @Transactional
    fun updateConfig(
        userId: String,
        rules: List<RuleDTO>,
    ): List<RuleDTO> {
        val currentEntity =
            analyzerRepository.findById(userId).orElse(null)
                ?: AnalyzerEntity(userId = userId)

        val updatedEntity = applyRulesToEntity(currentEntity, rules)
        val savedEntity = analyzerRepository.save(updatedEntity)

        return entityToRules(savedEntity)
    }

    private fun entityToRules(entity: AnalyzerEntity): List<RuleDTO> =
        listOf(
            RuleDTO(
                id = "identifierStyle",
                name = "Identifier Style",
                isActive = entity.identifierStyle != IdentifierStyle.NO_STYLE,
                value = entity.identifierStyle.name,
            ),
            RuleDTO(
                id = "restrictPrintlnArgs",
                name = "Restrict Println Arguments",
                isActive = entity.restrictPrintlnArgs,
                value = null,
            ),
            RuleDTO(
                id = "restrictReadInputArgs",
                name = "Restrict Read Input Arguments",
                isActive = entity.restrictReadInputArgs,
                value = null,
            ),
            RuleDTO(
                id = "noReadInput",
                name = "No Read Input",
                isActive = entity.noReadInput,
                value = null,
            ),
        )

    private fun applyRulesToEntity(
        entity: AnalyzerEntity,
        rules: List<RuleDTO>,
    ): AnalyzerEntity {
        var identifierStyle = entity.identifierStyle
        var restrictPrintlnArgs = entity.restrictPrintlnArgs
        var restrictReadInputArgs = entity.restrictReadInputArgs
        var noReadInput = entity.noReadInput

        rules.forEach { rule ->
            when (rule.id) {
                "identifierStyle" -> {
                    identifierStyle =
                        if (rule.isActive && rule.value != null) {
                            try {
                                IdentifierStyle.valueOf(rule.value)
                            } catch (e: IllegalArgumentException) {
                                IdentifierStyle.NO_STYLE
                            }
                        } else {
                            IdentifierStyle.NO_STYLE
                        }
                }
                "restrictPrintlnArgs" -> restrictPrintlnArgs = rule.isActive
                "restrictReadInputArgs" -> restrictReadInputArgs = rule.isActive
                "noReadInput" -> noReadInput = rule.isActive
            }
        }

        return entity.copy(
            identifierStyle = identifierStyle,
            restrictPrintlnArgs = restrictPrintlnArgs,
            restrictReadInputArgs = restrictReadInputArgs,
            noReadInput = noReadInput,
        )
    }
}
