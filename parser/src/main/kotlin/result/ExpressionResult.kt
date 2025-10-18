package parser.result

import node.Expression

sealed interface ExpressionResult : ParserResult {
    fun getExpression(): Expression
}
