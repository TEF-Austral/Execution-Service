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

    fun compile(
        src: InputStream,
        version: String,
    ): ValidationResultDTO {
        return try {
            val parser = ParserFactory.parse(src, version)
            val result = parser.parse()

            if (!result.isSuccess()) {
                val position =
                    result.getParser().peak()?.getCoordinates()
                        ?: coordinates.UnassignedPosition()

                return ValidationResultDTO.Invalid(
                    listOf(
                        LintViolationDTO(
                            message = result.message(),
                            line = position.getRow(),
                            column = position.getColumn(),
                        ),
                    ),
                )
            }

            ValidationResultDTO.Valid
        } catch (e: Exception) {
            ValidationResultDTO.Invalid(
                listOf(
                    LintViolationDTO(
                        message = "Unexpected error: ${e.message}",
                        line = -1,
                        column = -1,
                    ),
                ),
            )
        }
    }

    fun validate(
        src: InputStream,
        version: String,
        userId: String?,
    ): ValidationResultDTO {
        return try {
            val parser = ParserFactory.parse(src, version)
            val result = parser.parse()

            if (!result.isSuccess()) {
                val position =
                    result.getParser().peak()?.getCoordinates()
                        ?: coordinates.UnassignedPosition()

                return ValidationResultDTO.Invalid(
                    listOf(
                        LintViolationDTO(
                            message = result.message(),
                            line = position.getRow(),
                            column = position.getColumn(),
                        ),
                    ),
                )
            }

            val analyzerConfig = getAnalyzerConfig.getUserConfig(userId)
            val analyzer =
                createAnalyzer(
                    StringToPrintScriptVersion().transform(version),
                    analyzerConfig,
                )

            val diagnostics = analyzer.analyze(result)

            if (diagnostics.isEmpty()) {
                ValidationResultDTO.Valid
            } else {
                ValidationResultDTO.Invalid(diagnostics.map { it.toViolation() })
            }
        } catch (e: Exception) {
            ValidationResultDTO.Invalid(
                listOf(
                    LintViolationDTO(
                        message = "Unexpected error: ${e.message}",
                        line = -1,
                        column = -1,
                    ),
                ),
            )
        }
    }

    private fun Diagnostic.toViolation(): LintViolationDTO =
        LintViolationDTO(
            message = this.message,
            line = this.position.getRow(),
            column = this.position.getColumn(),
        )
}
