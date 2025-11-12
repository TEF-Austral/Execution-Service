package services

import config.AnalyzerConfig
import dtos.ValidationResultDTO
import factories.ParserTokenStreamFactory
import factories.ValidationResultFactory
import factory.AnalyzerFactory
import mappers.AnalyzerConfigMapper
import repositories.AnalyzerRepository
import transformer.StringToPrintScriptVersion
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class AnalyzerService(
    private val parserFactory: ParserTokenStreamFactory,
    private val analyzerRepository: AnalyzerRepository,
    private val analyzerConfigMapper: AnalyzerConfigMapper,
    private val validationResultFactory: ValidationResultFactory,
) {
    private val log = LoggerFactory.getLogger(AnalyzerService::class.java)
    private val versionTransformer = StringToPrintScriptVersion()

    fun compile(
        src: InputStream,
        version: String,
    ): ValidationResultDTO {
        log.info("Compiling code, version $version")
        val parser = parserFactory.createParser(src, version)
        val result = parser.parse()

        if (!result.isSuccess()) {
            val position = result.getCoordinates()
            log.warn(
                "Compilation failed at line ${position?.getRow()}, column ${position?.getColumn()}",
            )
        } else {
            log.warn("Compilation successful")
        }

        return validationResultFactory.createFromParserResult(result)
    }

    fun analyze(
        src: InputStream,
        version: String,
        userId: String,
    ): ValidationResultDTO {
        log.info("Analyzing code for user $userId, version $version")
        val parser = parserFactory.createParser(src, version)
        val result = parser.parse()

        if (!result.isSuccess()) {
            val position = result.getCoordinates()
            log.warn(
                "Analysis failed at line ${position?.getRow()}, column ${position?.getColumn()}",
            )
            return validationResultFactory.createFromParserResult(result)
        }

        val analyzerConfig = getConfigForUser(userId)
        val analyzer =
            AnalyzerFactory.createAnalyzer(
                versionTransformer.transform(version),
                analyzerConfig,
            )

        val diagnostics = analyzer.analyze(result)

        println("User Id: $userId")
        println("Analyzer Config: $analyzerConfig")

        val analysisResult = validationResultFactory.createFromDiagnostics(diagnostics)
        log.warn("Analysis completed for user $userId, violations found: ${diagnostics.size}")
        return analysisResult
    }

    private fun getConfigForUser(userId: String): AnalyzerConfig {
        val entity =
            analyzerRepository.findById(userId).orElse(null)
                ?: return AnalyzerConfig()
        return analyzerConfigMapper.entityToConfig(entity)
    }
}
