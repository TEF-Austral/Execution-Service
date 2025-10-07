package executor.expression

import data.DataBase
import result.InterpreterResult
import node.Expression

sealed interface ExpressionExecutor {
    fun execute(
        expression: Expression,
        database: DataBase,
    ): InterpreterResult
}
