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
    private val log = org.slf4j.LoggerFactory.getLogger(AnalyzerConfigService::class.java)

    fun getConfig(userId: String): List<AnalyzerRuleDTO> {
        log.info("Fetching analyzer config for user $userId")
        val entity =
            analyzerRepository.findById(userId).orElseGet {
                analyzerRepository.save(AnalyzerEntity(userId = userId))
            }

        val result = entityToRules(entity)
        log.warn("Retrieved ${result.size} analyzer rules for user $userId")
        return result
    }

    @Transactional
    fun updateConfig(
        userId: String,
        rules: List<AnalyzerRuleDTO>,
    ): List<AnalyzerRuleDTO> {
        log.info("Updating analyzer config for user $userId")
        val currentEntity =
            analyzerRepository.findById(userId).orElseGet {
                AnalyzerEntity(userId = userId)
            }

        val updatedEntity = applyRulesToEntity(currentEntity, rules)
        val savedEntity = analyzerRepository.save(updatedEntity)

        rulesUpdatedProducer.emit(AnalyzerRulesUpdatedEvent(userId = userId))
        println("📤 [PrintScript] Emitido AnalyzerRulesUpdatedEvent para usuario: $userId")

        val result = entityToRules(savedEntity)
        log.warn("Analyzer config updated for user $userId, ${result.size} rules")
        return result
    }

    private fun entityToRules(entity: AnalyzerEntity): List<AnalyzerRuleDTO> =
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
