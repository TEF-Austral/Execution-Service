package parser.statement.binary

import Token
import node.Expression
import parser.Parser
import parser.statement.expression.TokenToExpression
import parser.result.ExpressionBuiltResult
import parser.result.ExpressionResult
import type.CommonTypes

class DefaultParseBinary(
    private val tokenToExpression: TokenToExpression,
) : ParseBinary {
    override fun parseBinary(
        left: ExpressionResult,
        minPrecedence: Int,
    ): ExpressionResult = parseRecursively(left, minPrecedence)

    private fun parseRecursively(
        currentLeft: ExpressionResult,
        minPrecedence: Int,
    ): ExpressionResult {
        val parserAtOperator = currentLeft.getParser()

        if (!hasValidOperatorToken(parserAtOperator) ||
            !meetsMinimumPrecedence(parserAtOperator, minPrecedence)
        ) {
            return currentLeft
        }

        val operator = parserAtOperator.peak()!!
        val parserAfterOperator = consumeOperator(parserAtOperator)
        val rightOperand = parseRightOperand(parserAfterOperator)
        if (!rightOperand.isSuccess()) {
            throw Exception(rightOperand.message())
        }

        if (operator.getType() == parserAfterOperator.peak()?.getType()) {
            throw Exception("Can't put operators side by side")
        }

        val processedRight = processRightAssociativity(parserAfterOperator, rightOperand, operator)

        val newExpression =
            buildBinaryExpression(
                processedRight.getParser(),
                currentLeft.getExpression(),
                operator,
                processedRight.getExpression(),
            )

        val newLeft = ExpressionBuiltResult(processedRight.getParser(), newExpression)

        return parseRecursively(newLeft, minPrecedence)
    }

    override fun hasValidOperatorToken(parser: Parser): Boolean {
        val currentToken = parser.peak() ?: return false
        return isOperatorToken(currentToken)
    }

    private fun meetsMinimumPrecedence(
        parser: Parser,
        minPrecedence: Int,
    ): Boolean {
        val currentToken = parser.peak()!!
        val precedence = getOperatorPrecedence(currentToken)
        return precedence >= minPrecedence
    }

    override fun consumeOperator(parser: Parser): Parser = parser.advance()

    override fun parseRightOperand(parser: Parser): ExpressionResult {
        val currentToken = parser.peak() ?: throw Exception("Exceeded parsing limits")
        return tokenToExpression.parse(parser, currentToken)
    }

    override fun processRightAssociativity(
        parser: Parser,
        right: ExpressionResult,
        operator: Token,
    ): ExpressionResult {
        val nextParser = parser.advance()
        val nextToken = nextParser.peak()

        if (nextToken == null || !isOperatorToken(nextToken)) {
            return right
        }

        val currentPrecedence = getOperatorPrecedence(operator)
        val nextPrecedence = getOperatorPrecedence(nextToken)

        val isRightAssociative = operator.getValue() == "^"

        val shouldParseRecursively =
            nextPrecedence > currentPrecedence ||
                (nextPrecedence == currentPrecedence && isRightAssociative)

        return if (shouldParseRecursively) {
            val nextMinPrecedence =
                if (nextPrecedence >
                    currentPrecedence
                ) {
                    currentPrecedence + 1
                } else {
                    currentPrecedence
                }
            parseBinary(right, nextMinPrecedence)
        } else {
            right
        }
    }

    override fun buildBinaryExpression(
        parser: Parser,
        left: Expression,
        operator: Token,
        right: Expression,
    ): Expression = parser.getNodeBuilder().buildBinaryExpressionNode(left, operator, right)

    private fun isOperatorToken(token: Token): Boolean =
        when (token.getType()) {
            CommonTypes.OPERATORS,
            CommonTypes.COMPARISON,
            CommonTypes.LOGICAL_OPERATORS,
            -> true
            else -> false
        }

    private fun getOperatorPrecedence(token: Token): Int =
        when (token.getType()) {
            CommonTypes.LOGICAL_OPERATORS -> 1
            CommonTypes.COMPARISON -> 2
            CommonTypes.OPERATORS ->
                when (token.getValue()) {
                    "+", "-" -> 3
                    "*", "/", "%" -> 4
                    "^" -> 5
                    else -> 0
                }
            else -> 0
        }
}
