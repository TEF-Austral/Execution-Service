package parser.statement.expression

import Token
import parser.Parser
import parser.result.ExpressionBuiltResult
import parser.result.ExpressionResult
import parser.utils.isClosingParenthesis
import parser.utils.isOpeningParenthesis
import type.CommonTypes

class DelimitersBuilder : ExpressionBuilder {
    override fun canHandle(types: CommonTypes): Boolean = types == CommonTypes.DELIMITERS

    override fun parse(
        parser: Parser,
        current: Token,
    ): ExpressionResult {
        if (isOpeningParenthesis(parser)) {
            val afterOpenParen = parser.advance()
            val innerExpression =
                afterOpenParen.getExpressionParser().parseExpression(
                    afterOpenParen,
                )
            if (!innerExpression.isSuccess()) {
                throw Exception(innerExpression.message())
            }

            if (!isClosingParenthesis(innerExpression.getParser())) {
                throw Exception("Expected closing parenthesis ')'")
            }

            val afterCloseParen = innerExpression.getParser().advance()
            return ExpressionBuiltResult(afterCloseParen, innerExpression.getExpression())
        }
        val newParser = parser.advance()
        val expression = newParser.getExpressionParser().parseExpression(newParser)
        return ExpressionBuiltResult(expression.getParser().advance(), expression.getExpression())
    }
}
