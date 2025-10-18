package parser.statement.expression

import Token
import parser.Parser
import parser.result.ExpressionResult

sealed interface TokenToExpression {
    fun parse(
        parser: Parser,
        current: Token,
    ): ExpressionResult
}
