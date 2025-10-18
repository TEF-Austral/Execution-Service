package statement.enforcers

import parser.Parser
import parser.result.SemanticError
import parser.result.SemanticResult
import parser.result.SemanticSuccess
import type.CommonTypes

class IdentifierEnforcer(
    private val nextEnforcer: SemanticEnforcers,
) : SemanticEnforcers {
    override fun enforce(result: SemanticResult): SemanticResult {
        val currentParser = result.getParser()
        if (!currentParser.hasNext() || isIdentifier(currentParser) || !result.isSuccess()) {
            return SemanticError(
                "Expected identifier " + result.message(),
                result.identifier(),
                result.dataType(),
                result.initialValue(),
                currentParser,
            )
        }

        val parserResult = currentParser.consume(CommonTypes.IDENTIFIER)
        return nextEnforcer.enforce(
            SemanticSuccess(
                parserResult.message(),
                currentParser.peak()!!,
                result.dataType(),
                result.initialValue(),
                parserResult.getParser(),
            ),
        )
    }

    private fun isIdentifier(currentParser: Parser): Boolean =
        !currentParser.consume(CommonTypes.IDENTIFIER).isSuccess()
}
