package services

import config.AnalyzerConfig
import diagnostic.Diagnostic
import dtos.LintViolationDTO
import dtos.ValidationResultDTO
import factory.AnalyzerFactory.createAnalyzer
import helpers.GetAnalyzerConfig
import helpers.ParserFactory
import transformer.StringToPrintScriptVersion
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class PrintScriptAnalyzerService(
    private val getAnalyzerConfig: GetAnalyzerConfig,
) : LanguageAnalyzerService {
    private val log = org.slf4j.LoggerFactory.getLogger(PrintScriptAnalyzerService::class.java)

    override fun supportsLanguage(): String = Language.PRINTSCRIPT.name

    override fun compile(
        src: InputStream,
        version: String,
    ): ValidationResultDTO {
        log.info("Compiling code, version $version")
        val parser = ParserFactory.parse(src, version)
        val result = parser.parse()

        if (!result.isSuccess()) {
            val position = result.getCoordinates()
            log.warn(
                "Compilation failed at line ${position?.getRow()}, column ${position?.getColumn()}",
            )
            return ValidationResultDTO.Invalid(
                listOf(
                    LintViolationDTO(
                        message = result.message(),
                        line = position?.getRow() ?: -1,
                        column = position?.getColumn() ?: -1,
                    ),
                ),
            )
        }

        log.warn("Compilation successful")
        return ValidationResultDTO.Valid
    }

    override fun analyze(
        src: InputStream,
        version: String,
        userId: String,
    ): ValidationResultDTO {
        log.info("Analyzing code for user $userId, version $version")
        val parser = ParserFactory.parse(src, version)
        val result = parser.parse()

        if (!result.isSuccess()) {
            val position = result.getCoordinates()
            log.warn(
                "Analysis failed at line ${position?.getRow()}, column ${position?.getColumn()}",
            )
            return ValidationResultDTO.Invalid(
                listOf(
                    LintViolationDTO(
                        message = result.message(),
                        line = position?.getRow() ?: -1,
                        column = position?.getColumn() ?: -1,
                    ),
                ),
            )
        }

        val analyzerConfig: AnalyzerConfig = getAnalyzerConfig.getUserConfig(userId)
        val analyzer =
            createAnalyzer(StringToPrintScriptVersion().transform(version), analyzerConfig)
        val diagnostics = analyzer.analyze(result)

        println("User Id: $userId")
        println("Analyzer Config: $analyzerConfig")

        val analysisResult =
            if (diagnostics.isEmpty()) {
                ValidationResultDTO.Valid
            } else {
                ValidationResultDTO.Invalid(diagnostics.map { it.toViolation() })
            }

        log.warn("Analysis completed for user $userId, violations found: ${diagnostics.size}")
        return analysisResult
    }

    private fun Diagnostic.toViolation(): LintViolationDTO =
        LintViolationDTO(
            message = this.message,
            line = this.position.getRow(),
            column = this.position.getColumn(),
        )
}
