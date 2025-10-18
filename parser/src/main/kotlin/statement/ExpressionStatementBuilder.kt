package parser.statement

import Token
import parser.Parser
import parser.result.StatementBuiltResult
import parser.result.StatementResult
import parser.utils.advancePastSemiColon
import parser.utils.isOpeningParenthesis
import parser.utils.isSemiColon
import type.CommonTypes

class ExpressionStatementBuilder(
    private val acceptedTypes: Map<CommonTypes, Boolean> =
        mapOf(
            CommonTypes.NUMBER_LITERAL to true,
            CommonTypes.STRING_LITERAL to true,
            CommonTypes.IDENTIFIER to true,
            CommonTypes.OPERATORS to true,
        ),
) : StatementBuilder {
    override fun canHandle(
        token: Token?,
        parser: Parser,
    ): Boolean {
        if (token == null) return false
        return isValidExpressionStart(token.getType(), parser)
    }

    private fun isValidExpressionStart(
        types: CommonTypes,
        parser: Parser,
    ): Boolean {
        if (acceptedTypes.containsKey(types)) {
            return acceptedTypes.getValue(types)
        } else if (types == CommonTypes.DELIMITERS) {
            return isOpeningParenthesis(parser)
        }
        return false
    }

    override fun parse(parser: Parser): StatementResult {
        if (isSemiColon(parser.advance().advance().peak())) throw Exception("Invalid structure")
        val expression = parser.getExpressionParser().parseExpression(parser)
        val delimiterParser = advancePastSemiColon(expression.getParser())
        val builtStatement =
            delimiterParser.getNodeBuilder().buildExpressionStatementNode(
                expression.getExpression(),
            )
        return StatementBuiltResult(delimiterParser, builtStatement)
    }
}
