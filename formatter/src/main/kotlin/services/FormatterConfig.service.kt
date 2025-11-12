package services

import dtos.FormatConfigDTO
import dtos.FormatterRuleDTO
import entities.FormatterConfigEntity
import events.FormattingRulesUpdatedEvent
import mappers.FormatterConfigMapper
import org.springframework.stereotype.Service
import producers.FormattingRulesUpdatedProducer
import repositories.FormatterConfigRepository

@Service
class FormatterConfigService(
    private val formatterConfigRepository: FormatterConfigRepository,
    private val rulesUpdatedProducer: FormattingRulesUpdatedProducer,
    private val formatterConfigMapper: FormatterConfigMapper,
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

        val result = formatterConfigMapper.entityToRules(config)
        log.warn("Retrieved ${result.size} formatter rules for user $userId")
        return result
    }

    fun updateConfig(
        userId: String,
        rules: List<FormatterRuleDTO>,
    ): List<FormatterRuleDTO> {
        log.info("Updating formatter config for user $userId")
        val existing = formatterConfigRepository.findByUserId(userId)

        val config =
            if (existing.isPresent) {
                formatterConfigMapper.updateEntity(existing.get(), rules)
            } else {
                formatterConfigMapper.rulesToEntity(userId, rules)
            }

        val saved = formatterConfigRepository.save(config)
        rulesUpdatedProducer.emit(FormattingRulesUpdatedEvent(userId = userId))
        val result = formatterConfigMapper.entityToRules(saved)
        log.warn("Formatter config updated for user $userId, ${result.size} rules")
        return result
    }

    fun rulesToConfigDTO(rules: List<FormatterRuleDTO>): FormatConfigDTO =
        formatterConfigMapper.rulesToConfigDTO(rules)
}
