package parser.statement.expression

import Token
import type.CommonTypes
import parser.Parser
import parser.result.ExpressionBuiltResult

class LiteralBuilder(
    private val handleableLiteralTypes: List<CommonTypes> =
        listOf(CommonTypes.NUMBER_LITERAL, CommonTypes.STRING_LITERAL),
) : ExpressionBuilder {
    override fun canHandle(types: CommonTypes): Boolean {
        for (type in handleableLiteralTypes) {
            if (types == type) return true
        }
        return false
    }

    override fun parse(
        parser: Parser,
        current: Token,
    ): ExpressionBuiltResult {
        val newParser = parser.advance()
        return ExpressionBuiltResult(
            newParser,
            newParser.getNodeBuilder().buildLiteralExpressionNode(current),
        )
    }
}
