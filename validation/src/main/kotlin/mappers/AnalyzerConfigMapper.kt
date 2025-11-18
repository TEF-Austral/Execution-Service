package mappers

import config.AnalyzerConfig
import dtos.AnalyzerRuleDTO
import entities.AnalyzerEntity

interface AnalyzerConfigMapper {
    fun entityToRules(entity: AnalyzerEntity): List<AnalyzerRuleDTO>

    fun entityToConfig(entity: AnalyzerEntity): AnalyzerConfig

    fun rulesToEntity(
        userId: String,
        rules: List<AnalyzerRuleDTO>,
    ): AnalyzerEntity

    fun updateEntity(
        entity: AnalyzerEntity,
        rules: List<AnalyzerRuleDTO>,
    ): AnalyzerEntity
}
