package statement.enforcers

import parser.result.SemanticError
import parser.result.SemanticResult
import parser.result.SemanticSuccess
import parser.utils.isSemiColon
import type.CommonTypes

class SemiColonEnforcer : SemanticEnforcers {
    override fun enforce(result: SemanticResult): SemanticResult {
        val currentParser = result.getParser()
        // Check hasNext before peeking or consuming
        if (!currentParser.hasNext() || !isSemiColon(currentParser.peak()) || !result.isSuccess()) {
            return SemanticError(
                "Variable declaration must end in ; " + result.message(),
                result.identifier(),
                result.dataType(),
                result.initialValue(),
                currentParser,
            )
        }
        val parserResult = currentParser.consume(CommonTypes.DELIMITERS)
        return SemanticSuccess(
            parserResult.message(),
            result.identifier(),
            result.dataType(),
            result.initialValue(),
            parserResult.getParser(),
        )
    }
}
