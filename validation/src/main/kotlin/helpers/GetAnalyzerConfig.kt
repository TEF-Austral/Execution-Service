package helpers

import config.AnalyzerConfig
import entities.AnalyzerEntity
import repositories.AnalyzerRepository
import org.springframework.stereotype.Component

@Component
class GetAnalyzerConfig(
    private val analyzerRepository: AnalyzerRepository,
) {
    fun getUserConfig(userId: String?): AnalyzerConfig {
        if (userId == null) {
            return getDefaultConfig()
        }

        val entity =
            analyzerRepository.findById(userId).orElse(null)
                ?: return getDefaultConfig()

        return entityToConfig(entity)
    }

    private fun entityToConfig(entity: AnalyzerEntity): AnalyzerConfig =
        AnalyzerConfig(
            identifierStyle = entity.identifierStyle,
            restrictPrintlnArgs = entity.restrictPrintlnArgs,
            restrictReadInputArgs = entity.restrictReadInputArgs,
            noReadInput = entity.noReadInput,
        )

    private fun getDefaultConfig(): AnalyzerConfig = AnalyzerConfig()
}
