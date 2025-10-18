package statement.enforcers

import parser.Parser
import parser.result.SemanticError
import parser.result.SemanticResult
import parser.result.SemanticSuccess
import type.CommonTypes

class ColonEnforcer(
    private val nextEnforcer: SemanticEnforcers,
) : SemanticEnforcers {
    override fun enforce(result: SemanticResult): SemanticResult {
        val currentParser = result.getParser()
        // Check if there are tokens left before peeking or consuming
        if (!currentParser.hasNext() || isColon(currentParser) || !result.isSuccess()) {
            return SemanticError(
                "Expected delimiter " + result.message(),
                result.identifier(),
                result.dataType(),
                result.initialValue(),
                currentParser,
            )
        }
        val parserResult = currentParser.consume(CommonTypes.DELIMITERS)
        return nextEnforcer.enforce(
            SemanticSuccess(
                parserResult.message(),
                result.identifier(),
                result.dataType(),
                result.initialValue(),
                parserResult.getParser(),
            ),
        )
    }

    private fun isColon(currentParser: Parser): Boolean =
        !currentParser.consume(CommonTypes.DELIMITERS).isSuccess() ||
            (
                currentParser.hasNext() &&
                    currentParser
                        .peak()
                        ?.getValue()
                        ?.trimStart()
                        ?.trimEnd() != ":"
            )
}
