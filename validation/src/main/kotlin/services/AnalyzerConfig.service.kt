package services

import dtos.AnalyzerRuleDTO
import entities.AnalyzerEntity
import events.AnalyzerRulesUpdatedEvent
import mappers.AnalyzerConfigMapper
import producers.AnalyzerRulesUpdatedProducer
import repositories.AnalyzerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnalyzerConfigService(
    private val analyzerRepository: AnalyzerRepository,
    private val analyzerConfigMapper: AnalyzerConfigMapper,
    private val rulesUpdatedProducer: AnalyzerRulesUpdatedProducer,
) {
    private val log = LoggerFactory.getLogger(AnalyzerConfigService::class.java)

    fun getConfig(userId: String): List<AnalyzerRuleDTO> {
        log.info("Fetching analyzer config for user $userId")
        val entity = findOrCreateEntity(userId)
        val result = analyzerConfigMapper.entityToRules(entity)
        log.warn("Retrieved ${result.size} analyzer rules for user $userId")
        return result
    }

    @Transactional
    fun updateConfig(
        userId: String,
        rules: List<AnalyzerRuleDTO>,
    ): List<AnalyzerRuleDTO> {
        log.info("Updating analyzer config for user $userId")
        val existing = analyzerRepository.findById(userId)

        val entity =
            if (existing.isPresent) {
                analyzerConfigMapper.updateEntity(existing.get(), rules)
            } else {
                analyzerConfigMapper.rulesToEntity(userId, rules)
            }

        val savedEntity = analyzerRepository.save(entity)
        emitRulesUpdatedEvent(userId)

        val result = analyzerConfigMapper.entityToRules(savedEntity)
        log.warn("Analyzer config updated for user $userId, ${result.size} rules")
        return result
    }

    private fun findOrCreateEntity(userId: String): AnalyzerEntity =
        analyzerRepository.findById(userId).orElseGet {
            analyzerRepository.save(AnalyzerEntity(userId = userId))
        }

    private fun emitRulesUpdatedEvent(userId: String) {
        rulesUpdatedProducer.emit(AnalyzerRulesUpdatedEvent(userId = userId))
        println("📤 [PrintScript] Emitido AnalyzerRulesUpdatedEvent para usuario: $userId")
    }
}
