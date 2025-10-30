package services

import diagnostic.Diagnostic
import factory.AnalyzerFactory.createAnalyzer
import helpers.InputStreamToAnalyzerConfig
import helpers.ParserFactory
import transformer.StringToPrintScriptVersion
import java.io.InputStream

class AnalyzerService {

    fun validate(src: InputStream, version: String, config: InputStream): ValidationResult {
        return try {
            val parser = ParserFactory.parse(src, version)
            val result = parser.parse()
            if (!result.isSuccess()) {
                val position = result.getParser().peak()?.getCoordinates()
                    ?: coordinates.UnassignedPosition()

                return ValidationResult.Invalid(
                    listOf(
                        LintViolation(
                            message = result.message(),
                            line = position.getRow(),
                            column = position.getColumn()
                        )
                    )
                )
            }

            val analyzerConfig = InputStreamToAnalyzerConfig.adapt(config)
            val linter = createAnalyzer(
                StringToPrintScriptVersion().transform(version),
                analyzerConfig
            )

            val diagnostics = linter.analyze(result)

            if (diagnostics.isEmpty()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(diagnostics.map { it.toViolation() })
            }
        } catch (e: Exception) {
            ValidationResult.Invalid(
                listOf(
                    LintViolation(
                        message = "Unexpected error: ${e.message}",
                        line = -1,
                        column = -1
                    )
                )
            )
        }
    }

    private fun Diagnostic.toViolation(): LintViolation {
        return LintViolation(
            message = this.message,
            line = this.position.getRow(),
            column = this.position.getColumn()
        )
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val violations: List<LintViolation>) : ValidationResult()
}

data class LintViolation(
    val message: String,
    val line: Int,
    val column: Int
)