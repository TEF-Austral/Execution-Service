package parser.statement.expression

import Token
import parser.Parser
import parser.result.ExpressionBuiltResult
import parser.result.ExpressionResult
import parser.utils.isSemiColon
import parser.utils.isValidResultAndCurrentToken
import type.CommonTypes

class ReadInputParser : ExpressionBuilder {
    override fun parse(
        parser: Parser,
        current: Token,
    ): ExpressionResult {
        if (isSemiColon(parser.advance().advance().peak())) throw Exception("Invalid structure")
        val readInput = parser.consume(CommonTypes.READ_INPUT)
        if (!isValidResultAndCurrentToken(readInput)) {
            throw Exception("Expected readInput")
        }
        val value =
            readInput.getParser().getExpressionParser().parseExpression(
                readInput.getParser(),
            )
        val literalExpression = value.getExpression()
        val builtStatement =
            value.getParser().getNodeBuilder().buildReadInputNode(
                literalExpression,
            )
        return ExpressionBuiltResult(value.getParser(), builtStatement)
    }

    override fun canHandle(types: CommonTypes): Boolean = types == CommonTypes.READ_INPUT
}
