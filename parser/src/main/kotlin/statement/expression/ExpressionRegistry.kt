package parser.statement.expression

import Token
import node.EmptyExpression
import parser.Parser
import parser.result.ExpressionBuiltResult
import parser.result.ExpressionResult

class ExpressionRegistry(
    private val expressionBuilder: List<ExpressionBuilder>,
) : TokenToExpression {
    override fun parse(
        parser: Parser,
        current: Token,
    ): ExpressionResult {
        for (builder in expressionBuilder) {
            if (builder.canHandle(current.getType())) {
                return builder.parse(parser, current)
            }
        }
        return ExpressionBuiltResult(parser, EmptyExpression())
    }
}
