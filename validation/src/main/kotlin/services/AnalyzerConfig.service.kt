package services

import checkers.IdentifierStyle
import dtos.AnalyzerRuleDTO
import entities.AnalyzerEntity
import events.AnalyzerRulesUpdatedEvent
import repositories.AnalyzerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import producers.AnalyzerRulesUpdatedProducer

@Service
class AnalyzerConfigService(
    private val analyzerRepository: AnalyzerRepository,
    private val rulesUpdatedProducer: AnalyzerRulesUpdatedProducer,
) {

    fun getConfig(userId: String): List<AnalyzerRuleDTO> {
        val entity =
            analyzerRepository.findById(userId).orElseGet {
                analyzerRepository.save(AnalyzerEntity(userId = userId))
            }

        return entityToRules(entity)
    }

    @Transactional
    fun updateConfig(
        userId: String,
        rules: List<AnalyzerRuleDTO>,
    ): List<AnalyzerRuleDTO> {
        val currentEntity =
            analyzerRepository.findById(userId).orElseGet {
                AnalyzerEntity(userId = userId)
            }

        val updatedEntity = applyRulesToEntity(currentEntity, rules)
        val savedEntity = analyzerRepository.save(updatedEntity)

        rulesUpdatedProducer.emit(AnalyzerRulesUpdatedEvent(userId = userId))
        println("📤 [PrintScript] Emitido AnalyzerRulesUpdatedEvent para usuario: $userId")

        return entityToRules(savedEntity)
    }

    private fun entityToRules(entity: AnalyzerEntity): List<AnalyzerRuleDTO> =
        listOf(
            AnalyzerRuleDTO(
                id = "identifierStyle",
                name = "Identifier Style",
                isActive = entity.identifierStyle != IdentifierStyle.NO_STYLE,
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

    private fun applyRulesToEntity(
        entity: AnalyzerEntity,
        rules: List<AnalyzerRuleDTO>,
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
