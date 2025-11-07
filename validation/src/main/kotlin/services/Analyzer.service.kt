package services

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
class AnalyzerService(
    private val getAnalyzerConfig: GetAnalyzerConfig,
) {
    fun analyze(
        src: InputStream,
        version: String,
        userId: String?,
    ): ValidationResultDTO {
        val parser = ParserFactory.parse(src, version)
        val result = parser.parse()

        val analyzerConfig = getAnalyzerConfig.getUserConfig(userId)
        val analyzer =
            createAnalyzer(
                StringToPrintScriptVersion().transform(version),
                analyzerConfig,
            )

        val diagnostics = analyzer.analyze(result)

        return if (diagnostics.isEmpty()) {
            ValidationResultDTO.Valid
        } else {
            ValidationResultDTO.Invalid(diagnostics.map { it.toViolation() })
        }
    }

    private fun Diagnostic.toViolation(): LintViolationDTO =
        LintViolationDTO(
            message = this.message,
            line = this.position.getRow(),
            column = this.position.getColumn(),
        )
}
