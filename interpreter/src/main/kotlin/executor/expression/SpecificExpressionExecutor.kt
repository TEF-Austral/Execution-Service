package executor.expression

import node.Expression

interface SpecificExpressionExecutor : ExpressionExecutor {
    fun canHandle(expression: Expression): Boolean
}
