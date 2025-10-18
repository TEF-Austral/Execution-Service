package parser.statement

import Token
import parser.Parser
import parser.result.StatementBuiltResult
import parser.result.StatementResult
import parser.utils.advancePastSemiColon
import parser.utils.checkType
import parser.utils.isSemiColon
import parser.utils.isValidResultAndCurrentToken
import type.CommonTypes

class AssignmentParser : StatementBuilder {
    override fun canHandle(
        token: Token?,
        parser: Parser,
    ): Boolean {
        if (!checkType(CommonTypes.IDENTIFIER, token)) return false
        return checkType(CommonTypes.ASSIGNMENT, parser.advance().peak())
    }

    override fun parse(parser: Parser): StatementResult {
        if (isSemiColon(parser.advance().advance().peak())) throw Exception("Invalid structure")
        val identifier = parser.consume(CommonTypes.IDENTIFIER)
        if (!isValidResultAndCurrentToken(identifier)) {
            throw Exception("Expected identifier")
        }
        val assigmentParser =
            identifier
                .getParser()
                .consume(
                    CommonTypes.ASSIGNMENT,
                ).getParser()
        val value = assigmentParser.getExpressionParser().parseExpression(assigmentParser)
        val delimiterParser = advancePastSemiColon(value.getParser())
        val builtStatement =
            delimiterParser.getNodeBuilder().buildAssignmentStatementNode(
                parser.peak()!!,
                value.getExpression(),
            )
        return StatementBuiltResult(delimiterParser, builtStatement)
    }
}
