package parser.statement.expression

import Token
import type.CommonTypes
import parser.Parser
import parser.result.ExpressionBuiltResult

class IdentifierBuilder : ExpressionBuilder {
    override fun canHandle(types: CommonTypes): Boolean = types == CommonTypes.IDENTIFIER

    override fun parse(
        parser: Parser,
        current: Token,
    ): ExpressionBuiltResult {
        val newParser = parser.advance()
        return ExpressionBuiltResult(
            newParser,
            newParser.getNodeBuilder().buildIdentifierNode(current),
        )
    }
}
