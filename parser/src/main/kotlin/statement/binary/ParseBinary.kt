package parser.statement.binary

import Token
import node.Expression
import parser.Parser
import parser.result.ExpressionResult

sealed interface ParseBinary {
    fun parseBinary(
        left: ExpressionResult,
        minPrecedence: Int,
    ): ExpressionResult

    fun hasValidOperatorToken(parser: Parser): Boolean

    fun consumeOperator(parser: Parser): Parser

    fun parseRightOperand(parser: Parser): ExpressionResult

    fun processRightAssociativity(
        parser: Parser,
        right: ExpressionResult,
        operator: Token,
    ): ExpressionResult

    fun buildBinaryExpression(
        parser: Parser,
        left: Expression,
        operator: Token,
        right: Expression,
    ): Expression
}
