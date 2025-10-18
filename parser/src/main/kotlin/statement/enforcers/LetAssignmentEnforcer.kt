package statement.enforcers

import parser.result.SemanticError
import parser.result.SemanticResult
import parser.result.SemanticSuccess
import type.CommonTypes

class LetAssignmentEnforcer(
    private val nextEnforcer: SemanticEnforcers,
) : SemanticEnforcers {
    override fun enforce(result: SemanticResult): SemanticResult {
        val currentParser = result.getParser()
        if (!result.isSuccess()) {
            return SemanticError(
                "Expected Let Assignment Structure" + result.message(),
                result.identifier(),
                result.dataType(),
                result.initialValue(),
                currentParser,
            )
        } else if (!currentParser.consume(CommonTypes.ASSIGNMENT).isSuccess()) {
            return SemiColonEnforcer().enforce(result)
        }
        val parserResult =
            currentParser.getExpressionParser().parseExpression(
                currentParser.advance(),
            )
        return nextEnforcer.enforce(
            SemanticSuccess(
                parserResult.message(),
                result.identifier(),
                result.dataType(),
                parserResult.getExpression(),
                parserResult.getParser(),
            ),
        )
    }
}
