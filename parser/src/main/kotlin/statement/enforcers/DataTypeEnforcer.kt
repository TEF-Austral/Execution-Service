package statement.enforcers

import parser.result.SemanticError
import parser.result.SemanticResult
import parser.result.SemanticSuccess
import parser.utils.verifyCurrentToken
import type.CommonTypes

class DataTypeEnforcer(
    private val nextEnforcer: SemanticEnforcers,
    private val possibleTypes: List<CommonTypes> = listOf(CommonTypes.NUMBER, CommonTypes.STRING),
) : SemanticEnforcers {
    override fun enforce(result: SemanticResult): SemanticResult {
        val currentParser = result.getParser()
        // Check hasNext before peeking or advancing
        if (!currentParser.hasNext() ||
            !(verifyCurrentToken(possibleTypes, currentParser)) ||
            !result.isSuccess()
        ){
            return SemanticError(
                "Expected data type " + result.message(),
                result.identifier(),
                result.dataType(),
                result.initialValue(),
                currentParser,
            )
        }
        return nextEnforcer.enforce(
            SemanticSuccess(
                result.message(),
                result.identifier(),
                currentParser.peak()!!,
                result.initialValue(),
                currentParser.advance(),
            ),
        )
    }
}
