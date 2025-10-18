package parser.statement.expression

import Token
import node.LiteralExpression
import parser.Parser
import parser.result.ExpressionBuiltResult
import parser.result.ExpressionResult
import parser.utils.isSemiColon
import parser.utils.isValidResultAndCurrentToken
import type.CommonTypes

class ReadEnvParser : ExpressionBuilder {
    override fun parse(
        parser: Parser,
        current: Token,
    ): ExpressionResult {
        if (isSemiColon(parser.advance().advance().peak())) throw Exception("Invalid structure")
        val readEnv = parser.consume(CommonTypes.READ_ENV)
        if (!isValidResultAndCurrentToken(readEnv)) {
            throw Exception("Expected readEnv")
        }
        val value = readEnv.getParser().getExpressionParser().parseExpression(readEnv.getParser())
        if (value.getExpression() !is LiteralExpression) {
            throw Exception("Only a literal expression can be inside of readEnv")
        }
        val literalExpression = value.getExpression() as LiteralExpression
        val builtStatement = value.getParser().getNodeBuilder().buildReadEnvNode(literalExpression)
        return ExpressionBuiltResult(value.getParser(), builtStatement)
    }

    override fun canHandle(types: CommonTypes): Boolean = types == CommonTypes.READ_ENV
}
