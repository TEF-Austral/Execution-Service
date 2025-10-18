package parser.statement.expression

import type.CommonTypes

sealed interface ExpressionBuilder : TokenToExpression {
    fun canHandle(types: CommonTypes): Boolean
}
