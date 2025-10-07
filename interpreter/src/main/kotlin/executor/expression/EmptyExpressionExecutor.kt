package executor.expression

import data.DataBase
import node.EmptyExpression
import node.Expression
import result.InterpreterResult

class EmptyExpressionExecutor : SpecificExpressionExecutor {

    override fun execute(
        expression: Expression,
        database: DataBase,
    ): InterpreterResult = InterpreterResult(true, "Empty expression executed", null)

    override fun canHandle(expression: Expression): Boolean = expression is EmptyExpression
}
