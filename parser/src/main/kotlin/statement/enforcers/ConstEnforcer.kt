package statement.enforcers

import parser.result.SemanticError
import parser.result.SemanticResult
import parser.result.SemanticSuccess
import type.CommonTypes

class ConstEnforcer(
    private val nextEnforcer: SemanticEnforcers,
) : SemanticEnforcers {
    override fun enforce(result: SemanticResult): SemanticResult {
        val currentParser = result.getParser()
        if (checkForConstAndValidState(result)) {
            return SemanticError(
                "Expected const declaration " + result.message(),
                result.identifier(),
                result.dataType(),
                result.initialValue(),
                currentParser,
            )
        }
        val parserResult = currentParser.consume(CommonTypes.CONST)
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

    private fun checkForConstAndValidState(result: SemanticResult): Boolean {
        val currentParser = result.getParser()
        return !currentParser.consume(CommonTypes.CONST).isSuccess() || !result.isSuccess()
    }
}
