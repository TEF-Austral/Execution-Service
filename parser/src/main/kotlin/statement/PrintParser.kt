package parser.statement

import Token
import parser.Parser
import parser.result.StatementBuiltResult
import parser.result.StatementResult
import parser.utils.advancePastSemiColon
import parser.utils.isClosingParenthesis
import parser.utils.isOpeningParenthesis
import type.CommonTypes

class PrintParser : StatementBuilder {
    override fun canHandle(
        token: Token?,
        parser: Parser,
    ): Boolean = token?.getType() == CommonTypes.PRINT

    override fun parse(parser: Parser): StatementResult {
        val printParser = parser.consume(CommonTypes.PRINT).getParser()
        val afterOpeningParenthesisParser = openingParenthesisConsumption(printParser)
        val expression =
            afterOpeningParenthesisParser.getExpressionParser().parseExpression(
                afterOpeningParenthesisParser,
            )
        val beforeClosingParenthesisParser = closingParenthesisConsumption(expression.getParser())
        val finalDelimiterParser = consumeSemiColon(beforeClosingParenthesisParser)
        val builtStatement =
            finalDelimiterParser.getNodeBuilder().buildPrintStatementNode(
                expression.getExpression(),
            )
        return StatementBuiltResult(finalDelimiterParser, builtStatement)
    }

    private fun openingParenthesisConsumption(parser: Parser): Parser {
        if (isOpeningParenthesis(parser)) {
            return parser.consume(CommonTypes.DELIMITERS).getParser()
        }
        throw Exception("Was expecting opening parenthesis")
    }

    private fun closingParenthesisConsumption(parser: Parser): Parser {
        if (isClosingParenthesis(parser)) {
            return parser.consume(CommonTypes.DELIMITERS).getParser()
        }
        throw Exception("Was expecting closing parenthesis")
    }

    private fun consumeSemiColon(parser: Parser): Parser {
        val nextParser = advancePastSemiColon(parser)
        if (nextParser == parser) {
            throw Exception("Was expecting a semicolon")
        }
        return nextParser
    }
}
