package factories

import diagnostic.Diagnostic
import dtos.LintViolationDTO
import dtos.ValidationResultDTO
import org.springframework.stereotype.Component
import parser.result.FinalResult

@Component
class ValidationResultFactory {

    fun createFromParserResult(result: FinalResult): ValidationResultDTO {
        if (result.isSuccess()) {
            return ValidationResultDTO.Valid
        }

        val position = result.getCoordinates()
        val violation =
            LintViolationDTO(
                message = result.message(),
                line = position?.getRow() ?: -1,
                column = position?.getColumn() ?: -1,
            )

        return ValidationResultDTO.Invalid(listOf(violation))
    }

    fun createFromDiagnostics(diagnostics: List<Diagnostic>): ValidationResultDTO {
        if (diagnostics.isEmpty()) {
            return ValidationResultDTO.Valid
        }

        val violations =
            diagnostics.map { diagnostic ->
                LintViolationDTO(
                    message = diagnostic.message,
                    line = diagnostic.position.getRow(),
                    column = diagnostic.position.getColumn(),
                )
            }

        return ValidationResultDTO.Invalid(violations)
    }
}
